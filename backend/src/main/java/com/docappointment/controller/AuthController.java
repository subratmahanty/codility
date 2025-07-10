package com.docappointment.controller;

import com.docappointment.dto.LoginRequest;
import com.docappointment.dto.LoginResponse;
import com.docappointment.dto.RegisterRequest;
import com.docappointment.model.User;
import com.docappointment.security.JwtUtil;
import com.docappointment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Convert DTO to User entity
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setRole(request.getRole());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            
            if (request.getRole() == User.Role.DOCTOR) {
                user.setSpecialty(request.getSpecialty());
                user.setAvailability(request.getAvailability());
            }

            User registeredUser = userService.registerUser(user);

            // Generate JWT token
            String token = jwtUtil.generateToken(
                registeredUser.getUsername(), 
                registeredUser.getId(), 
                registeredUser.getRole().name()
            );

            LoginResponse response = new LoginResponse(
                token,
                registeredUser.getUsername(),
                registeredUser.getId(),
                registeredUser.getRole().name(),
                registeredUser.getFirstName(),
                registeredUser.getLastName(),
                registeredUser.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                Optional<User> userOpt = userService.findByUsername(request.getUsername());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // Generate JWT token
                    String token = jwtUtil.generateToken(
                        user.getUsername(), 
                        user.getId(), 
                        user.getRole().name()
                    );

                    LoginResponse response = new LoginResponse(
                        token,
                        user.getUsername(),
                        user.getId(),
                        user.getRole().name(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                    );

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.badRequest().body("Invalid username or password");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String userId = jwtUtil.extractUserId(token);
                    String role = jwtUtil.extractRole(token);
                    
                    return ResponseEntity.ok().body(new LoginResponse(
                        token, username, userId, role, null, null, null
                    ));
                }
            }
            
            return ResponseEntity.badRequest().body("Invalid token");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed: " + e.getMessage());
        }
    }
}