package com.heartbeat.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VitalsDTO {
    private UUID id;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @DecimalMin(value = "30.0", message = "Height must be at least 30 cm")
    @DecimalMax(value = "300.0", message = "Height must not exceed 300 cm")
    private BigDecimal heightCm;

    @DecimalMin(value = "0.5", message = "Weight must be at least 0.5 kg")
    @DecimalMax(value = "500.0", message = "Weight must not exceed 500 kg")
    private BigDecimal weightKg;

    @DecimalMin(value = "90.0", message = "Temperature must be at least 90°F")
    @DecimalMax(value = "110.0", message = "Temperature must not exceed 110°F")
    private BigDecimal temperatureF;

    @Min(value = 60, message = "Systolic pressure must be at least 60")
    @Max(value = 300, message = "Systolic pressure must not exceed 300")
    private Integer bloodPressureSystolic;

    @Min(value = 30, message = "Diastolic pressure must be at least 30")
    @Max(value = 200, message = "Diastolic pressure must not exceed 200")
    private Integer bloodPressureDiastolic;

    @Min(value = 30, message = "Heart rate must be at least 30 bpm")
    @Max(value = 300, message = "Heart rate must not exceed 300 bpm")
    private Integer heartRateBpm;

    @Min(value = 5, message = "Respiratory rate must be at least 5")
    @Max(value = 60, message = "Respiratory rate must not exceed 60")
    private Integer respiratoryRate;

    @DecimalMin(value = "50.0", message = "Oxygen saturation must be at least 50%")
    @DecimalMax(value = "100.0", message = "Oxygen saturation cannot exceed 100%")
    private BigDecimal oxygenSaturation;

    @DecimalMin(value = "20.0", message = "Blood sugar must be at least 20 mg/dL")
    @DecimalMax(value = "800.0", message = "Blood sugar must not exceed 800 mg/dL")
    private BigDecimal bloodSugar;

    private String notes;

    @NotNull(message = "Recorded date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordedDate;

    @NotNull(message = "Recorded by is required")
    private UUID recordedBy;

    private UserSummaryDTO recordedByUser;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    // Calculated BMI
    public BigDecimal getBmi() {
        if (heightCm != null && weightKg != null && 
            heightCm.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100));
            return weightKg.divide(heightM.multiply(heightM), 2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    // Blood pressure display
    public String getBloodPressureDisplay() {
        if (bloodPressureSystolic != null && bloodPressureDiastolic != null) {
            return bloodPressureSystolic + "/" + bloodPressureDiastolic;
        }
        return null;
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
}