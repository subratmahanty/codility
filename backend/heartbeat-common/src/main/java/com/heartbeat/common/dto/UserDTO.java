package com.heartbeat.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heartbeat.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscore")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @JsonIgnore
    private String passwordHash;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password; // Only used for creation/update

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotNull(message = "Role is required")
    private UserRole role;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Phone number must be in format +91-xxxxxxxxxx")
    private String phone;

    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    private UUID createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastLogin;

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getRoleDescription() {
        return role != null ? role.getDescription() : null;
    }

    // Permission helper methods
    public boolean hasAdminPrivileges() {
        return role != null && role.hasAdminPrivileges();
    }

    public boolean canManagePatients() {
        return role != null && role.canManagePatients();
    }

    public boolean canViewMedicalData() {
        return role != null && role.canViewMedicalData();
    }

    public boolean canManageMedicines() {
        return role != null && role.canManageMedicines();
    }

    public boolean canManageLabTests() {
        return role != null && role.canManageLabTests();
    }

    public boolean canEnterLabResults() {
        return role != null && role.canEnterLabResults();
    }

    public boolean canCreateTreatments() {
        return role != null && role.canCreateTreatments();
    }

    public boolean canUploadFiles() {
        return role != null && role.canUploadFiles();
    }

    public boolean canManageNotifications() {
        return role != null && role.canManageNotifications();
    }

    public boolean canCreatePracticeTemplates() {
        return role != null && role.canCreatePracticeTemplates();
    }
}