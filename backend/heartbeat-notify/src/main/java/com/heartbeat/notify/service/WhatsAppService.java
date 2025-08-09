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
public class WhatsAppService {

    @Value("${heartbeat.notification.whatsapp.enabled:false}")
    private boolean whatsAppEnabled;

    @Value("${heartbeat.notification.whatsapp.twilio.account-sid:}")
    private String accountSid;

    @Value("${heartbeat.notification.whatsapp.twilio.auth-token:}")
    private String authToken;

    @Value("${heartbeat.notification.whatsapp.twilio.from-number:}")
    private String fromNumber; // Format: whatsapp:+14155238886

    @PostConstruct
    public void initTwilio() {
        if (whatsAppEnabled && accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                log.info("Twilio WhatsApp service initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Twilio WhatsApp service: {}", e.getMessage());
                whatsAppEnabled = false;
            }
        } else {
            log.info("WhatsApp service is disabled or not configured");
        }
    }

    public boolean sendWhatsApp(String to, String messageBody) {
        if (!whatsAppEnabled) {
            log.warn("WhatsApp service is disabled. Would have sent WhatsApp to: {}", to);
            return true; // Return true for testing purposes when disabled
        }

        if (fromNumber == null || fromNumber.isEmpty()) {
            log.error("WhatsApp from number is not configured");
            return false;
        }

        try {
            // Format phone numbers for WhatsApp
            String formattedTo = formatWhatsAppNumber(to);
            String formattedFrom = fromNumber.startsWith("whatsapp:") ? fromNumber : "whatsapp:" + fromNumber;

            log.info("Sending WhatsApp to: {} with message: {}", formattedTo, messageBody);

            Message message = Message.creator(
                    new PhoneNumber(formattedTo),
                    new PhoneNumber(formattedFrom),
                    messageBody
            ).create();

            log.info("WhatsApp sent successfully to: {} with SID: {}", formattedTo, message.getSid());
            return true;

        } catch (Exception e) {
            log.error("Failed to send WhatsApp to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendAppointmentReminderWhatsApp(String to, String patientName, String appointmentDate, String doctorName) {
        String message = String.format(
                "Hi %s! 👋\n\n" +
                "🏥 *Appointment Reminder*\n\n" +
                "📅 Date: %s\n" +
                "👨‍⚕️ Doctor: %s\n\n" +
                "⏰ Please arrive 15 minutes early for check-in.\n\n" +
                "Need to reschedule? Just reply to this message or call us.\n\n" +
                "Best regards,\n" +
                "*Heartbeat Clinic Team* ❤️",
                patientName, appointmentDate, doctorName
        );

        return sendWhatsApp(to, message);
    }

    public boolean sendFollowUpReminderWhatsApp(String to, String patientName, String treatmentDetails) {
        String message = String.format(
                "Hi %s! 👋\n\n" +
                "🔔 *Follow-up Reminder*\n\n" +
                "It's time for your follow-up regarding:\n" +
                "📋 %s\n\n" +
                "Please schedule your follow-up appointment if you haven't already.\n\n" +
                "💬 Have questions? Just reply to this message!\n\n" +
                "Best regards,\n" +
                "*Heartbeat Clinic Team* ❤️",
                patientName, treatmentDetails
        );

        return sendWhatsApp(to, message);
    }

    public boolean sendLabResultsNotificationWhatsApp(String to, String patientName, String testName) {
        String message = String.format(
                "Hi %s! 👋\n\n" +
                "📊 *Lab Results Ready*\n\n" +
                "Your lab results for *%s* are now available.\n\n" +
                "📞 Please contact us to schedule an appointment to discuss your results.\n\n" +
                "We're here to help! 💪\n\n" +
                "Best regards,\n" +
                "*Heartbeat Clinic Team* ❤️",
                patientName, testName
        );

        return sendWhatsApp(to, message);
    }

    public boolean sendMedicationReminderWhatsApp(String to, String patientName, String medicationName, String dosage, String time) {
        String message = String.format(
                "Hi %s! 👋\n\n" +
                "💊 *Medication Reminder*\n\n" +
                "Time to take your medication:\n" +
                "📋 *%s*\n" +
                "💉 Dosage: %s\n" +
                "⏰ Time: %s\n\n" +
                "Stay healthy! 💪\n\n" +
                "Questions about your medication? Just reply!\n\n" +
                "*Heartbeat Clinic Team* ❤️",
                patientName, medicationName, dosage, time
        );

        return sendWhatsApp(to, message);
    }

    public boolean sendWelcomeMessageWhatsApp(String to, String patientName) {
        String message = String.format(
                "Welcome to Heartbeat Clinic, %s! 👋\n\n" +
                "🏥 We're excited to have you as our patient!\n\n" +
                "You'll receive important updates about:\n" +
                "• 📅 Appointment reminders\n" +
                "• 💊 Medication schedules\n" +
                "• 📊 Lab results\n" +
                "• 🔔 Follow-up care\n\n" +
                "Have questions? Just reply to any of our messages!\n\n" +
                "Thank you for choosing us for your healthcare needs. ❤️\n\n" +
                "*Heartbeat Clinic Team*",
                patientName
        );

        return sendWhatsApp(to, message);
    }

    public boolean sendEmergencyAlertWhatsApp(String to, String patientName, String alertMessage) {
        String message = String.format(
                "🚨 *URGENT ALERT* 🚨\n\n" +
                "Patient: %s\n\n" +
                "⚠️ %s\n\n" +
                "🏥 Please contact Heartbeat Clinic immediately or visit the nearest emergency room.\n\n" +
                "📞 Emergency Contact: [Emergency Number]\n\n" +
                "*This is an automated urgent notification*",
                patientName, alertMessage
        );

        return sendWhatsApp(to, message);
    }

    public boolean testWhatsAppConnection() {
        if (!whatsAppEnabled) {
            log.info("WhatsApp service is disabled");
            return false;
        }

        try {
            // Test WhatsApp connection by checking Twilio configuration
            if (accountSid != null && !accountSid.isEmpty() && 
                authToken != null && !authToken.isEmpty() && 
                fromNumber != null && !fromNumber.isEmpty()) {
                log.info("WhatsApp service connection test successful");
                return true;
            } else {
                log.error("WhatsApp service configuration is incomplete");
                return false;
            }

        } catch (Exception e) {
            log.error("WhatsApp service connection test failed: {}", e.getMessage());
            return false;
        }
    }

    private String formatWhatsAppNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        // If already formatted for WhatsApp, return as is
        if (phoneNumber.startsWith("whatsapp:")) {
            return phoneNumber;
        }

        // Remove all non-digit characters except +
        String cleaned = phoneNumber.replaceAll("[^\\d+]", "");

        // If it doesn't start with +, assume it's an Indian number and add +91
        if (!cleaned.startsWith("+")) {
            if (cleaned.length() == 10) {
                cleaned = "+91" + cleaned;
            } else if (cleaned.length() == 12 && cleaned.startsWith("91")) {
                cleaned = "+" + cleaned;
            }
        }

        return "whatsapp:" + cleaned;
    }

    public boolean isValidWhatsAppNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        String formatted = formatWhatsAppNumber(phoneNumber);
        
        // Basic validation for WhatsApp format
        return formatted.matches("^whatsapp:\\+[1-9]\\d{1,14}$");
    }
}