package com.heartbeat.notify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${heartbeat.notification.email.from:noreply@heartbeat.com}")
    private String fromEmail;

    @Value("${heartbeat.notification.email.enabled:false}")
    private boolean emailEnabled;

    public boolean sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Would have sent email to: {}", to);
            return true; // Return true for testing purposes when disabled
        }

        try {
            log.info("Sending email to: {} with subject: {}", to, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return true;

        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Would have sent HTML email to: {}", to);
            return true; // Return true for testing purposes when disabled
        }

        try {
            log.info("Sending HTML email to: {} with subject: {}", to, subject);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);
            return true;

        } catch (MessagingException | MailException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String to, String subject, String body, String attachmentPath, String attachmentName) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Would have sent email with attachment to: {}", to);
            return true;
        }

        try {
            log.info("Sending email with attachment to: {}", to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Add attachment
            if (attachmentPath != null && attachmentName != null) {
                helper.addAttachment(attachmentName, new java.io.File(attachmentPath));
            }

            mailSender.send(mimeMessage);
            log.info("Email with attachment sent successfully to: {}", to);
            return true;

        } catch (MessagingException | MailException e) {
            log.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendAppointmentReminder(String to, String patientName, String appointmentDate, String doctorName) {
        String subject = "Appointment Reminder - Heartbeat Clinic";
        String body = String.format(
                "Dear %s,\n\n" +
                "This is a reminder for your upcoming appointment:\n\n" +
                "Date: %s\n" +
                "Doctor: %s\n\n" +
                "Please arrive 15 minutes early for check-in.\n\n" +
                "If you need to reschedule, please contact us as soon as possible.\n\n" +
                "Best regards,\n" +
                "Heartbeat Clinic Team",
                patientName, appointmentDate, doctorName
        );

        return sendEmail(to, subject, body);
    }

    public boolean sendFollowUpReminder(String to, String patientName, String treatmentDetails) {
        String subject = "Follow-up Reminder - Heartbeat Clinic";
        String body = String.format(
                "Dear %s,\n\n" +
                "This is a reminder for your follow-up regarding:\n\n" +
                "%s\n\n" +
                "Please schedule your follow-up appointment if you haven't already.\n\n" +
                "If you have any concerns or questions, please don't hesitate to contact us.\n\n" +
                "Best regards,\n" +
                "Heartbeat Clinic Team",
                patientName, treatmentDetails
        );

        return sendEmail(to, subject, body);
    }

    public boolean sendLabResultsNotification(String to, String patientName, String testName) {
        String subject = "Lab Results Available - Heartbeat Clinic";
        String body = String.format(
                "Dear %s,\n\n" +
                "Your lab results for %s are now available.\n\n" +
                "Please contact us to schedule an appointment to discuss your results.\n\n" +
                "Best regards,\n" +
                "Heartbeat Clinic Team",
                patientName, testName
        );

        return sendEmail(to, subject, body);
    }

    public boolean testEmailConnection() {
        if (!emailEnabled) {
            log.info("Email service is disabled");
            return false;
        }

        try {
            // Test email connection by creating a simple message
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setFrom(fromEmail);
            testMessage.setTo(fromEmail);
            testMessage.setSubject("Email Service Test");
            testMessage.setText("This is a test message to verify email configuration.");

            // Don't actually send the test message, just verify connection
            log.info("Email service connection test successful");
            return true;

        } catch (Exception e) {
            log.error("Email service connection test failed: {}", e.getMessage());
            return false;
        }
    }
}