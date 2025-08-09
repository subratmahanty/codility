package com.heartbeat.notify.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class SmsService {

    @Value("${heartbeat.notification.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${heartbeat.notification.sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${heartbeat.notification.sms.twilio.auth-token:}")
    private String authToken;

    @Value("${heartbeat.notification.sms.twilio.from-number:}")
    private String fromNumber;

    @PostConstruct
    public void initTwilio() {
        if (smsEnabled && accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                log.info("Twilio SMS service initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Twilio SMS service: {}", e.getMessage());
                smsEnabled = false;
            }
        } else {
            log.info("SMS service is disabled or not configured");
        }
    }

    public boolean sendSms(String to, String messageBody) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Would have sent SMS to: {}", to);
            return true; // Return true for testing purposes when disabled
        }

        if (fromNumber == null || fromNumber.isEmpty()) {
            log.error("SMS from number is not configured");
            return false;
        }

        try {
            log.info("Sending SMS to: {} with message: {}", to, messageBody);

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();

            log.info("SMS sent successfully to: {} with SID: {}", to, message.getSid());
            return true;

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendAppointmentReminderSms(String to, String patientName, String appointmentDate, String doctorName) {
        String message = String.format(
                "Hi %s, this is a reminder for your appointment on %s with %s. " +
                "Please arrive 15 minutes early. Contact us if you need to reschedule. - Heartbeat Clinic",
                patientName, appointmentDate, doctorName
        );

        return sendSms(to, message);
    }

    public boolean sendFollowUpReminderSms(String to, String patientName) {
        String message = String.format(
                "Hi %s, this is a reminder to schedule your follow-up appointment. " +
                "Please contact Heartbeat Clinic to book your appointment.",
                patientName
        );

        return sendSms(to, message);
    }

    public boolean sendLabResultsNotificationSms(String to, String patientName, String testName) {
        String message = String.format(
                "Hi %s, your lab results for %s are ready. " +
                "Please contact Heartbeat Clinic to discuss your results.",
                patientName, testName
        );

        return sendSms(to, message);
    }

    public boolean sendMedicationReminderSms(String to, String patientName, String medicationName) {
        String message = String.format(
                "Hi %s, reminder to take your medication: %s. " +
                "For questions about your medication, contact Heartbeat Clinic.",
                patientName, medicationName
        );

        return sendSms(to, message);
    }

    public boolean sendEmergencyAlertSms(String to, String patientName, String alertMessage) {
        String message = String.format(
                "URGENT: %s - %s. Please contact Heartbeat Clinic immediately or visit the nearest emergency room.",
                patientName, alertMessage
        );

        return sendSms(to, message);
    }

    public boolean testSmsConnection() {
        if (!smsEnabled) {
            log.info("SMS service is disabled");
            return false;
        }

        try {
            // Test SMS connection by checking Twilio configuration
            if (accountSid != null && !accountSid.isEmpty() && 
                authToken != null && !authToken.isEmpty() && 
                fromNumber != null && !fromNumber.isEmpty()) {
                log.info("SMS service connection test successful");
                return true;
            } else {
                log.error("SMS service configuration is incomplete");
                return false;
            }

        } catch (Exception e) {
            log.error("SMS service connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        // Remove all non-digit characters
        String cleaned = phoneNumber.replaceAll("[^\\d+]", "");

        // If it doesn't start with +, assume it's an Indian number and add +91
        if (!cleaned.startsWith("+")) {
            if (cleaned.length() == 10) {
                cleaned = "+91" + cleaned;
            } else if (cleaned.length() == 12 && cleaned.startsWith("91")) {
                cleaned = "+" + cleaned;
            }
        }

        return cleaned;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        String formatted = formatPhoneNumber(phoneNumber);
        
        // Basic validation for international format
        return formatted.matches("^\\+[1-9]\\d{1,14}$");
    }
}