package com.docappointment.service;

import com.docappointment.model.Appointment;
import com.docappointment.model.AppointmentHistory;
import com.docappointment.model.User;
import com.docappointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HistoryService historyService;

    public Appointment bookAppointment(String patientId, String doctorId, LocalDateTime startTime, LocalDateTime endTime, String notes) {
        // Validate doctor exists
        Optional<User> doctor = userService.findById(doctorId);
        if (doctor.isEmpty() || doctor.get().getRole() != User.Role.DOCTOR) {
            throw new RuntimeException("Invalid doctor ID");
        }

        // Validate patient exists
        Optional<User> patient = userService.findById(patientId);
        if (patient.isEmpty() || patient.get().getRole() != User.Role.PATIENT) {
            throw new RuntimeException("Invalid patient ID");
        }

        // Check if doctor is available at the requested time
        if (isDoctorBusy(doctorId, startTime, endTime)) {
            throw new RuntimeException("Doctor is not available at the requested time");
        }

        // Validate appointment time is in the future
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book appointments in the past");
        }

        // Create and save appointment
        Appointment appointment = new Appointment(patientId, doctorId, startTime, endTime);
        appointment.setNotes(notes);
        appointment = appointmentRepository.save(appointment);

        // Record history
        historyService.recordAppointmentAction(appointment.getId(), AppointmentHistory.HistoryAction.BOOKED, patientId);

        // Send notifications
        notificationService.sendAppointmentConfirmation(appointment);
        notificationService.sendDoctorNotification(appointment, "New appointment booked");

        return appointment;
    }

    public Appointment cancelAppointment(String appointmentId, String actorId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }

        // Determine who is cancelling
        Optional<User> actor = userService.findById(actorId);
        if (actor.isEmpty()) {
            throw new RuntimeException("Invalid user ID");
        }

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);

        // Record history based on who cancelled
        AppointmentHistory.HistoryAction action;
        if (actor.get().getRole() == User.Role.DOCTOR && actorId.equals(appointment.getDoctorId())) {
            action = AppointmentHistory.HistoryAction.CANCELLED_BY_DOCTOR;
            // Notify patient
            notificationService.sendPatientCancellationNotification(appointment);
        } else if (actor.get().getRole() == User.Role.PATIENT && actorId.equals(appointment.getPatientId())) {
            action = AppointmentHistory.HistoryAction.CANCELLED_BY_PATIENT;
            // Notify doctor
            notificationService.sendDoctorCancellationNotification(appointment);
        } else {
            throw new RuntimeException("User not authorized to cancel this appointment");
        }

        historyService.recordAppointmentAction(appointment.getId(), action, actorId, oldStatus, appointment.getStatus().name());

        return appointment;
    }

    public Appointment updateAppointmentStatus(String appointmentId, Appointment.AppointmentStatus status, String actorId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        String oldStatus = appointment.getStatus().name();
        
        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);

        // Record history
        AppointmentHistory.HistoryAction action = status == Appointment.AppointmentStatus.COMPLETED ? 
            AppointmentHistory.HistoryAction.STATUS_UPDATED_TO_COMPLETED : 
            AppointmentHistory.HistoryAction.STATUS_UPDATED;
            
        historyService.recordAppointmentAction(appointment.getId(), action, actorId, oldStatus, status.name());

        return appointment;
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatientAndStatus(String patientId, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status);
    }

    public List<Appointment> getAppointmentsByDoctorAndStatus(String doctorId, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByStartTimeBetween(start, end);
    }

    public List<Appointment> getDoctorAppointmentsByDateRange(String doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDoctorAndDateRange(doctorId, start, end);
    }

    public Optional<Appointment> getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> getAppointmentsForReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindow = now.plusHours(1); // Send reminders for appointments within 1 hour
        return appointmentRepository.findAppointmentsForReminder(now, reminderWindow);
    }

    public List<Appointment> getAppointmentsToComplete() {
        return appointmentRepository.findAppointmentsToComplete(LocalDateTime.now());
    }

    public void markReminderSent(String appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setReminderSent(true);
            appointmentRepository.save(appointment);
        }
    }

    private boolean isDoctorBusy(String doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.existsByDoctorIdAndStartTimeBetween(doctorId, startTime, endTime);
    }

    public boolean canUserAccessAppointment(String userId, String appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }

        Appointment appointment = appointmentOpt.get();
        return appointment.getPatientId().equals(userId) || appointment.getDoctorId().equals(userId);
    }
}