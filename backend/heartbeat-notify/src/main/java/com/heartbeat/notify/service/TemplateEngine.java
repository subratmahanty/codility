package com.heartbeat.notify.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TemplateEngine {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * Process a template string by replacing placeholders with actual values
     * Placeholders format: {{variableName}}
     */
    public String processTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(0); // Full match including {{}}
            String variableName = matcher.group(1).trim(); // Variable name without {{}}

            Object value = getNestedValue(variables, variableName);
            String replacement = value != null ? value.toString() : "";

            result = result.replace(placeholder, replacement);
        }

        return result;
    }

    /**
     * Get value from nested map using dot notation
     * Example: patient.firstName, treatment.diagnosis
     */
    private Object getNestedValue(Map<String, Object> variables, String path) {
        String[] parts = path.split("\\.");
        Object current = variables;

        for (String part : parts) {
            if (current instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) current;
                current = map.get(part);
            } else {
                return null;
            }

            if (current == null) {
                break;
            }
        }

        return current;
    }

    /**
     * Create variables map for appointment reminder
     */
    public Map<String, Object> createAppointmentVariables(
            String patientName, 
            String appointmentDate, 
            String appointmentTime,
            String doctorName,
            String clinicName,
            String clinicAddress) {
        
        return Map.of(
            "patientName", patientName != null ? patientName : "",
            "appointmentDate", appointmentDate != null ? appointmentDate : "",
            "appointmentTime", appointmentTime != null ? appointmentTime : "",
            "doctorName", doctorName != null ? doctorName : "",
            "clinicName", clinicName != null ? clinicName : "Heartbeat Clinic",
            "clinicAddress", clinicAddress != null ? clinicAddress : ""
        );
    }

    /**
     * Create variables map for follow-up reminder
     */
    public Map<String, Object> createFollowUpVariables(
            String patientName,
            String treatmentDate,
            String diagnosis,
            String doctorName,
            String followUpInstructions) {
        
        return Map.of(
            "patientName", patientName != null ? patientName : "",
            "treatmentDate", treatmentDate != null ? treatmentDate : "",
            "diagnosis", diagnosis != null ? diagnosis : "",
            "doctorName", doctorName != null ? doctorName : "",
            "followUpInstructions", followUpInstructions != null ? followUpInstructions : ""
        );
    }

    /**
     * Create variables map for lab results notification
     */
    public Map<String, Object> createLabResultsVariables(
            String patientName,
            String testName,
            String resultDate,
            String doctorName,
            String urgencyLevel) {
        
        return Map.of(
            "patientName", patientName != null ? patientName : "",
            "testName", testName != null ? testName : "",
            "resultDate", resultDate != null ? resultDate : "",
            "doctorName", doctorName != null ? doctorName : "",
            "urgencyLevel", urgencyLevel != null ? urgencyLevel : "normal"
        );
    }

    /**
     * Create variables map for medication reminder
     */
    public Map<String, Object> createMedicationVariables(
            String patientName,
            String medicationName,
            String dosage,
            String frequency,
            String instructions,
            String doctorName) {
        
        return Map.of(
            "patientName", patientName != null ? patientName : "",
            "medicationName", medicationName != null ? medicationName : "",
            "dosage", dosage != null ? dosage : "",
            "frequency", frequency != null ? frequency : "",
            "instructions", instructions != null ? instructions : "",
            "doctorName", doctorName != null ? doctorName : ""
        );
    }

    /**
     * Create variables map for general patient communication
     */
    public Map<String, Object> createPatientVariables(
            String patientName,
            String patientId,
            String phone,
            String email) {
        
        return Map.of(
            "patient", Map.of(
                "name", patientName != null ? patientName : "",
                "id", patientId != null ? patientId : "",
                "phone", phone != null ? phone : "",
                "email", email != null ? email : ""
            )
        );
    }

    /**
     * Validate template syntax
     */
    public boolean isValidTemplate(String template) {
        if (template == null || template.isEmpty()) {
            return true;
        }

        try {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                if (variableName.isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Template validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all placeholders from a template
     */
    public java.util.Set<String> extractPlaceholders(String template) {
        java.util.Set<String> placeholders = new java.util.HashSet<>();
        
        if (template != null && !template.isEmpty()) {
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
            while (matcher.find()) {
                placeholders.add(matcher.group(1).trim());
            }
        }
        
        return placeholders;
    }

    /**
     * Default templates for common notification types
     */
    public static class DefaultTemplates {
        public static final String APPOINTMENT_REMINDER_EMAIL = 
            "Dear {{patientName}},\n\n" +
            "This is a reminder for your upcoming appointment:\n\n" +
            "Date: {{appointmentDate}}\n" +
            "Time: {{appointmentTime}}\n" +
            "Doctor: {{doctorName}}\n\n" +
            "Please arrive 15 minutes early for check-in.\n\n" +
            "Best regards,\n" +
            "{{clinicName}} Team";

        public static final String APPOINTMENT_REMINDER_SMS = 
            "Hi {{patientName}}, reminder: appointment on {{appointmentDate}} at {{appointmentTime}} with {{doctorName}}. " +
            "Arrive 15 min early. - {{clinicName}}";

        public static final String FOLLOW_UP_REMINDER_EMAIL = 
            "Dear {{patientName}},\n\n" +
            "This is a reminder for your follow-up regarding your treatment on {{treatmentDate}} for {{diagnosis}}.\n\n" +
            "{{followUpInstructions}}\n\n" +
            "Please contact us to schedule your follow-up appointment.\n\n" +
            "Best regards,\n" +
            "{{clinicName}} Team";

        public static final String LAB_RESULTS_EMAIL = 
            "Dear {{patientName}},\n\n" +
            "Your lab results for {{testName}} ({{resultDate}}) are now available.\n\n" +
            "Please contact us to schedule an appointment with {{doctorName}} to discuss your results.\n\n" +
            "Best regards,\n" +
            "{{clinicName}} Team";

        public static final String MEDICATION_REMINDER_SMS = 
            "Hi {{patientName}}, time to take {{medicationName}} - {{dosage}}. {{instructions}} - {{clinicName}}";
    }
}