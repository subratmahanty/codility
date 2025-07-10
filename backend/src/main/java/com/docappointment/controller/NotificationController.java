package com.docappointment.controller;

import com.docappointment.model.Notification;
import com.docappointment.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/sms/callback")
    public ResponseEntity<?> handleSmsCallback(
            @RequestParam String MessageSid,
            @RequestParam String MessageStatus) {
        try {
            // Handle Twilio callback for SMS status updates
            Notification.NotificationStatus status;
            
            switch (MessageStatus.toLowerCase()) {
                case "delivered":
                    status = Notification.NotificationStatus.DELIVERED;
                    break;
                case "failed":
                case "undelivered":
                    status = Notification.NotificationStatus.FAILED;
                    break;
                default:
                    status = Notification.NotificationStatus.SENT;
                    break;
            }
            
            notificationService.updateNotificationStatus(MessageSid, status);
            return ResponseEntity.ok("Callback processed");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process callback: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Notification>> getAppointmentNotifications(@PathVariable String appointmentId) {
        List<Notification> notifications = notificationService.getNotificationsByAppointment(appointmentId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
        try {
            Notification.NotificationType notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsByType(notificationType);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable String status) {
        try {
            Notification.NotificationStatus notificationStatus = Notification.NotificationStatus.valueOf(status.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsByStatus(notificationStatus);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}