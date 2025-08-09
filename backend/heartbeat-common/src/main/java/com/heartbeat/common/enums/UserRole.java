package com.heartbeat.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("Full system administration"),
    DOCTOR("Medical treatments and prescriptions"),
    PHARMA_ADMIN("Medicine and lab test management"),
    LAB_ADMIN("Lab test results and management"),
    NURSE("Patient care and vitals"),
    HELPDESK("Patient registration and support"),
    SUPERVISOR("Practice templates and supervision");

    private final String description;

    public boolean hasAdminPrivileges() {
        return this == ADMIN;
    }

    public boolean canManagePatients() {
        return this == ADMIN || this == HELPDESK || this == NURSE;
    }

    public boolean canViewMedicalData() {
        return this == ADMIN || this == DOCTOR || this == NURSE;
    }

    public boolean canManageMedicines() {
        return this == ADMIN || this == PHARMA_ADMIN;
    }

    public boolean canManageLabTests() {
        return this == ADMIN || this == PHARMA_ADMIN;
    }

    public boolean canEnterLabResults() {
        return this == ADMIN || this == LAB_ADMIN;
    }

    public boolean canCreateTreatments() {
        return this == ADMIN || this == DOCTOR;
    }

    public boolean canUploadFiles() {
        return this == ADMIN || this == DOCTOR || this == NURSE || this == LAB_ADMIN;
    }

    public boolean canManageNotifications() {
        return this == ADMIN || this == DOCTOR || this == NURSE;
    }

    public boolean canCreatePracticeTemplates() {
        return this == ADMIN || this == SUPERVISOR;
    }
}