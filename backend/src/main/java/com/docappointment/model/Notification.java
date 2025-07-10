package com.docappointment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @NotNull
    private NotificationType type;
    
    @NotNull
    private String recipientId;
    
    @NotNull
    private String message;
    
    @NotNull
    private NotificationStatus status;
    
    private String externalMessageId; // Twilio SID or other external service ID
    
    private String appointmentId; // Optional reference to appointment
    
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    
    public enum NotificationType {
        SMS, WHATSAPP, EMAIL, IN_APP
    }
    
    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, FAILED
    }
    
    // Constructors
    public Notification() {
        this.sentAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
    }
    
    public Notification(NotificationType type, String recipientId, String message) {
        this();
        this.type = type;
        this.recipientId = recipientId;
        this.message = message;
    }
    
    public Notification(NotificationType type, String recipientId, String message, String appointmentId) {
        this(type, recipientId, message);
        this.appointmentId = appointmentId;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { 
        this.status = status;
        if (status == NotificationStatus.SENT) {
            this.sentAt = LocalDateTime.now();
        } else if (status == NotificationStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        } else if (status == NotificationStatus.FAILED) {
            this.failedAt = LocalDateTime.now();
        }
    }
    
    public String getExternalMessageId() { return externalMessageId; }
    public void setExternalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; }
    
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
}