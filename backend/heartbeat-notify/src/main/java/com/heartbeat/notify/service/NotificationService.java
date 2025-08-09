package com.heartbeat.notify.service;

import com.heartbeat.common.enums.NotificationChannel;
import com.heartbeat.common.enums.NotificationStatus;
import com.heartbeat.common.exception.HeartbeatException;
import com.heartbeat.repo.entity.Notification;
import com.heartbeat.repo.entity.NotificationTemplate;
import com.heartbeat.repo.entity.Patient;
import com.heartbeat.repo.repository.NotificationRepository;
import com.heartbeat.repo.repository.NotificationTemplateRepository;
import com.heartbeat.repo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final PatientRepository patientRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final WhatsAppService whatsAppService;
    private final TemplateEngine templateEngine;

    @Transactional
    public Notification scheduleNotification(
            UUID patientId,
            UUID templateId,
            String recipient,
            NotificationChannel channel,
            LocalDateTime scheduledAt,
            Map<String, Object> variables) {

        log.info("Scheduling notification for patient: {} via {}", patientId, channel);

        Patient patient = null;
        if (patientId != null) {
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new HeartbeatException("Patient not found", HttpStatus.NOT_FOUND, "PATIENT_NOT_FOUND"));
        }

        NotificationTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new HeartbeatException("Template not found", HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND"));

        if (!template.getChannel().equals(channel)) {
            throw new HeartbeatException("Template channel mismatch", HttpStatus.BAD_REQUEST, "CHANNEL_MISMATCH");
        }

        // Process template with variables
        String processedMessage = templateEngine.processTemplate(template.getMessageTemplate(), variables);
        String processedSubject = template.getSubject() != null ? 
                templateEngine.processTemplate(template.getSubject(), variables) : null;

        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setTemplate(template);
        notification.setRecipient(recipient);
        notification.setChannel(channel);
        notification.setSubject(processedSubject);
        notification.setMessage(processedMessage);
        notification.setScheduledAt(scheduledAt);
        notification.setStatus(NotificationStatus.SCHEDULED);
        notification.setRetryCount(0);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification scheduled with ID: {}", savedNotification.getId());

        return savedNotification;
    }

    @Transactional
    public Notification sendImmediateNotification(
            UUID patientId,
            String recipient,
            NotificationChannel channel,
            String subject,
            String message) {

        log.info("Sending immediate notification via {}", channel);

        Patient patient = null;
        if (patientId != null) {
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new HeartbeatException("Patient not found", HttpStatus.NOT_FOUND, "PATIENT_NOT_FOUND"));
        }

        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setRecipient(recipient);
        notification.setChannel(channel);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setScheduledAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.SCHEDULED);
        notification.setRetryCount(0);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        
        // Send immediately
        sendNotificationAsync(savedNotification);

        return savedNotification;
    }

    @Async
    public void sendNotificationAsync(Notification notification) {
        try {
            sendNotification(notification);
        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
            markNotificationFailed(notification.getId(), e.getMessage());
        }
    }

    @Transactional
    public void sendNotification(Notification notification) {
        log.info("Sending notification: {} via {}", notification.getId(), notification.getChannel());

        try {
            boolean success = false;
            
            switch (notification.getChannel()) {
                case EMAIL:
                    success = emailService.sendEmail(
                            notification.getRecipient(),
                            notification.getSubject(),
                            notification.getMessage()
                    );
                    break;
                case SMS:
                    success = smsService.sendSms(
                            notification.getRecipient(),
                            notification.getMessage()
                    );
                    break;
                case WHATSAPP:
                    success = whatsAppService.sendWhatsApp(
                            notification.getRecipient(),
                            notification.getMessage()
                    );
                    break;
            }

            if (success) {
                markNotificationSent(notification.getId());
            } else {
                markNotificationFailed(notification.getId(), "Service returned failure");
            }

        } catch (Exception e) {
            log.error("Error sending notification {}: {}", notification.getId(), e.getMessage());
            markNotificationFailed(notification.getId(), e.getMessage());
        }
    }

    @Transactional
    public void markNotificationSent(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new HeartbeatException("Notification not found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND"));

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);

        log.info("Notification {} marked as sent", notificationId);
    }

    @Transactional
    public void markNotificationFailed(UUID notificationId, String errorMessage) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new HeartbeatException("Notification not found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND"));

        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);

        log.warn("Notification {} marked as failed: {}", notificationId, errorMessage);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processPendingNotifications() {
        log.debug("Processing pending notifications");

        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingNotifications = notificationRepository
                .findByStatusAndScheduledAtLessThanEqual(NotificationStatus.SCHEDULED, now);

        for (Notification notification : pendingNotifications) {
            sendNotificationAsync(notification);
        }

        if (!pendingNotifications.isEmpty()) {
            log.info("Processed {} pending notifications", pendingNotifications.size());
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void retryFailedNotifications() {
        log.debug("Retrying failed notifications");

        LocalDateTime retryAfter = LocalDateTime.now().minusMinutes(5);
        List<Notification> failedNotifications = notificationRepository
                .findFailedNotificationsForRetry(retryAfter, 3);

        for (Notification notification : failedNotifications) {
            if (notification.getRetryCount() < 3) {
                log.info("Retrying notification: {}", notification.getId());
                notification.setStatus(NotificationStatus.SCHEDULED);
                notificationRepository.save(notification);
                sendNotificationAsync(notification);
            }
        }

        if (!failedNotifications.isEmpty()) {
            log.info("Retried {} failed notifications", failedNotifications.size());
        }
    }

    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(Pageable pageable) {
        return notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByPatient(UUID patientId, Pageable pageable) {
        return notificationRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByStatus(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Transactional(readOnly = true)
    public long getNotificationCount() {
        return notificationRepository.count();
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByStatus(NotificationStatus status) {
        return notificationRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long getPendingNotificationCount() {
        return notificationRepository.countByStatus(NotificationStatus.SCHEDULED);
    }

    @Transactional
    public void cancelNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new HeartbeatException("Notification not found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND"));

        if (notification.getStatus() != NotificationStatus.SCHEDULED) {
            throw new HeartbeatException("Can only cancel scheduled notifications", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }

        notification.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(notification);

        log.info("Notification {} cancelled", notificationId);
    }

    @Transactional
    public void rescheduleNotification(UUID notificationId, LocalDateTime newScheduledAt) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new HeartbeatException("Notification not found", HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND"));

        if (notification.getStatus() != NotificationStatus.SCHEDULED && notification.getStatus() != NotificationStatus.FAILED) {
            throw new HeartbeatException("Can only reschedule scheduled or failed notifications", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }

        notification.setScheduledAt(newScheduledAt);
        notification.setStatus(NotificationStatus.SCHEDULED);
        notification.setErrorMessage(null);
        notificationRepository.save(notification);

        log.info("Notification {} rescheduled for {}", notificationId, newScheduledAt);
    }
}