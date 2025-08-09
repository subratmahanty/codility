package com.heartbeat.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heartbeat.common.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PatientDTO {
    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Phone number must be in format +91-xxxxxxxxxx")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+91-[0-9]{10}$", message = "Emergency contact phone must be in format +91-xxxxxxxxxx")
    private String emergencyContactPhone;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    private String bloodGroup;

    private String allergies;
    private String medicalHistory;
    private String photoUrl;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    private UserSummaryDTO createdBy;

    @Valid
    private List<PatientIdentifierDTO> identifiers;

    // Age calculation helper
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    @Data
    public static class PatientIdentifierDTO {
        private UUID id;

        @NotNull(message = "Identifier type is required")
        private IdentifierType type;

        @NotBlank(message = "Identifier value is required")
        private String value;

        private Boolean isVerified;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime verifiedAt;

        private UserSummaryDTO verifiedBy;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdAt;
    }

    @Data
    public static class UserSummaryDTO {
        private UUID id;
        private String firstName;
        private String lastName;
        private String role;

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    public enum IdentifierType {
        PAN, AADHAR, VOTER_ID
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // Full name helper
    public String getFullName() {
        return firstName + " " + lastName;
    }
}