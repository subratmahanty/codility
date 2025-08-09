package com.heartbeat.api.controller;

import com.heartbeat.auth.service.AuthenticationService;
import com.heartbeat.common.dto.UserDTO;
import com.heartbeat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthenticationService.AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        AuthenticationService.AuthenticationRequest authRequest = 
                new AuthenticationService.AuthenticationRequest(request.getUsername(), request.getPassword());
        
        AuthenticationService.AuthenticationResponse response = authenticationService.authenticate(authRequest);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO, Authentication authentication) {
        log.info("User registration attempt for username: {}", userDTO.getUsername());
        
        // Get the ID of the user creating this account
        UUID createdBy = getCurrentUserId(authentication);
        
        UserDTO createdUser = userService.createUser(userDTO, createdBy);
        
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token")
    public ResponseEntity<AuthenticationService.AuthenticationResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        log.info("Token refresh attempt");
        
        AuthenticationService.AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate token")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        log.info("User logout");
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authenticationService.logout(token);
        }
        
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        log.info("Password change request for user: {}", username);
        
        UserDTO updatedUser = authenticationService.changePassword(
                username, 
                request.getCurrentPassword(), 
                request.getNewPassword()
        );
        
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Getting current user details for: {}", username);
        
        UserDTO user = userService.getUserByUsername(username);
        
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update current authenticated user details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.info("Updating current user: {}", userId);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(request.getFirstName());
        userDTO.setLastName(request.getLastName());
        userDTO.setEmail(request.getEmail());
        userDTO.setPhone(request.getPhone());
        
        UserDTO updatedUser = userService.updateUser(userId, userDTO, userId);
        
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
        String username = authentication.getName();
        
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "authorities", authentication.getAuthorities()
        ));
    }

    private UUID getCurrentUserId(Authentication authentication) {
        // This would typically extract the user ID from the JWT token or UserDetails
        // For now, we'll get it from the user service
        try {
            UserDTO user = userService.getUserByUsername(authentication.getName());
            return user.getId();
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage());
            return null;
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }

    @Data
    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }
}