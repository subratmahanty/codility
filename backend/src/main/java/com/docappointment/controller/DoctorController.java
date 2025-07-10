package com.docappointment.controller;

import com.docappointment.model.User;
import com.docappointment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllDoctors() {
        List<User> doctors = userService.findAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable String id) {
        Optional<User> doctor = userService.findById(id);
        
        if (doctor.isEmpty() || doctor.get().getRole() != User.Role.DOCTOR) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(doctor.get());
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<User>> getDoctorsBySpecialty(@PathVariable String specialty) {
        List<User> doctors = userService.findDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors);
    }
}