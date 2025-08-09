package com.heartbeat.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heartbeat.common.enums.TreatmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TreatmentDTO {
    private UUID id;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    private PatientSummaryDTO patient;

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    private UserSummaryDTO doctor;

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis must not exceed 500 characters")
    private String diagnosis;

    private String symptoms;

    private String examinationNotes;

    private TreatmentStatus status = TreatmentStatus.ACTIVE;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate followUpDate;

    @NotNull(message = "Treatment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate treatmentDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;

    @Valid
    private List<PrescriptionItemDTO> prescriptionItems;

    @Valid
    private List<LabTestRequestDTO> labTestRequests;

    @Data
    public static class PatientSummaryDTO {
        private UUID id;
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String phone;

        public String getFullName() {
            return firstName + " " + lastName;
        }
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

    @Data
    public static class PrescriptionItemDTO {
        private UUID id;
        private UUID medicineId;
        private MedicineDTO medicine;

        @NotBlank(message = "Dosage is required")
        private String dosage;

        @NotBlank(message = "Frequency is required")
        private String frequency;

        @NotBlank(message = "Duration is required")
        private String duration;

        private String instructions;
        private Integer quantity;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdAt;
    }

    @Data
    public static class LabTestRequestDTO {
        private UUID id;
        private UUID labTestId;
        private LabTestDTO labTest;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate requestedDate;

        private String notes;
        private Boolean isUrgent = false;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdAt;

        private LabTestResultDTO result;
    }

    @Data
    public static class MedicineDTO {
        private UUID id;
        private String name;
        private String genericName;
        private String manufacturer;
        private String dosageForm;
        private String strength;
        private String unit;
        private String description;
        private Boolean isActive;

        public String getDisplayName() {
            return name + " " + strength + " " + unit + " (" + dosageForm + ")";
        }
    }

    @Data
    public static class LabTestDTO {
        private UUID id;
        private String testName;
        private String testCode;
        private String category;
        private Double normalRangeMin;
        private Double normalRangeMax;
        private String unit;
        private String sampleType;
        private String preparationNotes;
        private Double cost;
        private Boolean isActive;
    }

    @Data
    public static class LabTestResultDTO {
        private UUID id;
        private String resultValue;
        private String resultText;
        private Boolean isNormal;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate resultDate;

        private UserSummaryDTO performedBy;
        private UserSummaryDTO reviewedBy;
        private String notes;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdAt;
    }
}