package com.docappointment.service;

import com.docappointment.model.Appointment;
import com.docappointment.model.Notification;
import com.docappointment.model.User;
import com.docappointment.repository.NotificationRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && authToken != null && 
            !accountSid.equals("your_account_sid") && !authToken.equals("your_auth_token")) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        Optional<User> patient = userService.findById(appointment.getPatientId());
        Optional<User> doctor = userService.findById(appointment.getDoctorId());

        if (patient.isPresent() && doctor.isPresent()) {
            String message = String.format(
                "Your appointment with Dr. %s %s has been confirmed for %s. Location: [Clinic Address]",
                doctor.get().getFirstName(),
                doctor.get().getLastName(),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy at hh:mm a"))
            );

            sendSmsNotification(patient.get().getPhoneNumber(), message, appointment.getPatientId(), appointment.getId());
        }
    }

    @Async
    public void sendDoctorNotification(Appointment appointment, String customMessage) {
        Optional<User> doctor = userService.findById(appointment.getDoctorId());
        Optional<User> patient = userService.findById(appointment.getPatientId());

        if (doctor.isPresent() && patient.isPresent()) {
            String message = String.format(
                "%s - Patient: %s %s, Time: %s",
                customMessage,
                patient.get().getFirstName(),
                patient.get().getLastName(),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy at hh:mm a"))
            );

            sendSmsNotification(doctor.get().getPhoneNumber(), message, appointment.getDoctorId(), appointment.getId());
        }
    }

    @Async
    public void sendDoctorCancellationNotification(Appointment appointment) {
        sendDoctorNotification(appointment, "Appointment cancelled by patient");
    }

    @Async
    public void sendPatientCancellationNotification(Appointment appointment) {
        Optional<User> patient = userService.findById(appointment.getPatientId());
        Optional<User> doctor = userService.findById(appointment.getDoctorId());

        if (patient.isPresent() && doctor.isPresent()) {
            String message = String.format(
                "Your appointment with Dr. %s %s scheduled for %s has been cancelled by the doctor. Please contact the clinic to reschedule.",
                doctor.get().getFirstName(),
                doctor.get().getLastName(),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy at hh:mm a"))
            );

            sendSmsNotification(patient.get().getPhoneNumber(), message, appointment.getPatientId(), appointment.getId());
        }
    }

    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        Optional<User> patient = userService.findById(appointment.getPatientId());
        Optional<User> doctor = userService.findById(appointment.getDoctorId());

        if (patient.isPresent() && doctor.isPresent()) {
            String message = String.format(
                "Reminder: You have an appointment with Dr. %s %s in 1 hour (%s). Location: [Clinic Address]",
                doctor.get().getFirstName(),
                doctor.get().getLastName(),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
            );

            sendSmsNotification(patient.get().getPhoneNumber(), message, appointment.getPatientId(), appointment.getId());
        }
    }

    public void sendSmsNotification(String phoneNumber, String message, String recipientId, String appointmentId) {
        Notification notification = new Notification(
            Notification.NotificationType.SMS, 
            recipientId, 
            message, 
            appointmentId
        );

        try {
            // Save notification as pending
            notification = notificationRepository.save(notification);

            // Check if Twilio is configured
            if (accountSid == null || authToken == null || 
                accountSid.equals("your_account_sid") || authToken.equals("your_auth_token")) {
                // Simulate sending for development/testing
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setExternalMessageId("SIMULATED_" + System.currentTimeMillis());
                notificationRepository.save(notification);
                return;
            }

            // Send actual SMS via Twilio
            Message twilioMessage = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message
            ).create();

            // Update notification status
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setExternalMessageId(twilioMessage.getSid());
            notificationRepository.save(notification);

        } catch (Exception e) {
            // Update notification as failed
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw new RuntimeException("Failed to send SMS notification", e);
        }
    }

    public void sendWhatsAppNotification(String phoneNumber, String message, String recipientId, String appointmentId) {
        Notification notification = new Notification(
            Notification.NotificationType.WHATSAPP, 
            recipientId, 
            message, 
            appointmentId
        );

        try {
            // Save notification as pending
            notification = notificationRepository.save(notification);

            // Check if Twilio is configured
            if (accountSid == null || authToken == null || 
                accountSid.equals("your_account_sid") || authToken.equals("your_auth_token")) {
                // Simulate sending for development/testing
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setExternalMessageId("SIMULATED_WA_" + System.currentTimeMillis());
                notificationRepository.save(notification);
                return;
            }

            // Send WhatsApp message via Twilio
            Message twilioMessage = Message.creator(
                new PhoneNumber("whatsapp:" + phoneNumber),
                new PhoneNumber("whatsapp:" + twilioPhoneNumber),
                message
            ).create();

            // Update notification status
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setExternalMessageId(twilioMessage.getSid());
            notificationRepository.save(notification);

        } catch (Exception e) {
            // Update notification as failed
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw new RuntimeException("Failed to send WhatsApp notification", e);
        }
    }

    public void updateNotificationStatus(String externalMessageId, Notification.NotificationStatus status) {
        Optional<Notification> notificationOpt = notificationRepository.findByExternalMessageId(externalMessageId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(status);
            notificationRepository.save(notification);
        }
    }

    public List<Notification> getNotificationsByRecipient(String recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }

    public List<Notification> getNotificationsByAppointment(String appointmentId) {
        return notificationRepository.findByAppointmentId(appointmentId);
    }

    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }

    public List<Notification> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }
}