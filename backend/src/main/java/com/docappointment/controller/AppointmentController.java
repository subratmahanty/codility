package com.docappointment.controller;

import com.docappointment.dto.AppointmentRequest;
import com.docappointment.dto.AppointmentResponse;
import com.docappointment.model.Appointment;
import com.docappointment.model.User;
import com.docappointment.security.JwtUtil;
import com.docappointment.service.AppointmentService;
import com.docappointment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String patientId = jwtUtil.extractUserId(token);

            Appointment appointment = appointmentService.bookAppointment(
                patientId,
                request.getDoctorId(),
                request.getStartTime(),
                request.getEndTime(),
                request.getNotes()
            );

            AppointmentResponse response = createAppointmentResponse(appointment);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            List<Appointment> appointments = new ArrayList<>();

            if ("DOCTOR".equals(role)) {
                // Doctor can only see their own appointments
                if (status != null) {
                    appointments = appointmentService.getAppointmentsByDoctorAndStatus(
                        userId, Appointment.AppointmentStatus.valueOf(status.toUpperCase())
                    );
                } else {
                    appointments = appointmentService.getAppointmentsByDoctor(userId);
                }
            } else if ("PATIENT".equals(role)) {
                // Patient can only see their own appointments
                if (status != null) {
                    appointments = appointmentService.getAppointmentsByPatientAndStatus(
                        userId, Appointment.AppointmentStatus.valueOf(status.toUpperCase())
                    );
                } else {
                    appointments = appointmentService.getAppointmentsByPatient(userId);
                }
            } else if ("ADMIN".equals(role)) {
                // Admin can see all appointments with filtering
                if (doctorId != null && status != null) {
                    appointments = appointmentService.getAppointmentsByDoctorAndStatus(
                        doctorId, Appointment.AppointmentStatus.valueOf(status.toUpperCase())
                    );
                } else if (patientId != null && status != null) {
                    appointments = appointmentService.getAppointmentsByPatientAndStatus(
                        patientId, Appointment.AppointmentStatus.valueOf(status.toUpperCase())
                    );
                } else if (doctorId != null) {
                    appointments = appointmentService.getAppointmentsByDoctor(doctorId);
                } else if (patientId != null) {
                    appointments = appointmentService.getAppointmentsByPatient(patientId);
                } else {
                    // Return all appointments for admin if no filters
                    return ResponseEntity.badRequest().body("Admin must specify filters");
                }
            }

            List<AppointmentResponse> responses = new ArrayList<>();
            for (Appointment appointment : appointments) {
                responses.add(createAppointmentResponse(appointment));
            }

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve appointments: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Appointment appointment = appointmentOpt.get();

            // Check authorization
            if (!"ADMIN".equals(role)) {
                if (!appointmentService.canUserAccessAppointment(userId, id)) {
                    return ResponseEntity.status(403).body("Access denied");
                }
            }

            AppointmentResponse response = createAppointmentResponse(appointment);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve appointment: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);

            // Check if user can access this appointment
            if (!appointmentService.canUserAccessAppointment(userId, id)) {
                return ResponseEntity.status(403).body("Access denied");
            }

            Appointment appointment = appointmentService.cancelAppointment(id, userId);
            AppointmentResponse response = createAppointmentResponse(appointment);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // Check authorization for doctors
            if ("DOCTOR".equals(role)) {
                if (!appointmentService.canUserAccessAppointment(userId, id)) {
                    return ResponseEntity.status(403).body("Access denied");
                }
            }

            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            Appointment appointment = appointmentService.updateAppointmentStatus(id, appointmentStatus, userId);
            
            AppointmentResponse response = createAppointmentResponse(appointment);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}/schedule")
    public ResponseEntity<?> getDoctorSchedule(
            @PathVariable String doctorId,
            @RequestParam String date) {
        try {
            LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            List<Appointment> appointments = appointmentService.getDoctorAppointmentsByDateRange(
                doctorId, startOfDay, endOfDay
            );

            List<AppointmentResponse> responses = new ArrayList<>();
            for (Appointment appointment : appointments) {
                responses.add(createAppointmentResponse(appointment));
            }

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve doctor schedule: " + e.getMessage());
        }
    }

    private AppointmentResponse createAppointmentResponse(Appointment appointment) {
        String patientName = "Unknown Patient";
        String doctorName = "Unknown Doctor";
        String doctorSpecialty = "";

        Optional<User> patient = userService.findById(appointment.getPatientId());
        if (patient.isPresent()) {
            patientName = patient.get().getFirstName() + " " + patient.get().getLastName();
        }

        Optional<User> doctor = userService.findById(appointment.getDoctorId());
        if (doctor.isPresent()) {
            doctorName = "Dr. " + doctor.get().getFirstName() + " " + doctor.get().getLastName();
            doctorSpecialty = doctor.get().getSpecialty() != null ? doctor.get().getSpecialty() : "";
        }

        return new AppointmentResponse(appointment, patientName, doctorName, doctorSpecialty);
    }
}