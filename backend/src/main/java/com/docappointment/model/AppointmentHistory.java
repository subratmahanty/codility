package com.docappointment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "appointment_history")
public class AppointmentHistory {
    
    @Id
    private String id;
    
    @NotNull
    private String appointmentId;
    
    @NotNull
    private HistoryAction action;
    
    private String oldStatus;
    private String newStatus;
    
    @NotNull
    private String actorId; // User who performed the action
    
    @NotNull
    private LocalDateTime timestamp;
    
    private Map<String, Object> details; // Additional context
    
    public enum HistoryAction {
        BOOKED,
        CANCELLED_BY_PATIENT,
        CANCELLED_BY_DOCTOR,
        STATUS_UPDATED_TO_COMPLETED,
        REMINDER_SENT,
        STATUS_UPDATED,
        NOTES_UPDATED
    }
    
    // Constructors
    public AppointmentHistory() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AppointmentHistory(String appointmentId, HistoryAction action, String actorId) {
        this();
        this.appointmentId = appointmentId;
        this.action = action;
        this.actorId = actorId;
    }
    
    public AppointmentHistory(String appointmentId, HistoryAction action, String actorId, 
                             String oldStatus, String newStatus) {
        this(appointmentId, action, actorId);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
    
    public AppointmentHistory(String appointmentId, HistoryAction action, String actorId, 
                             String oldStatus, String newStatus, Map<String, Object> details) {
        this(appointmentId, action, actorId, oldStatus, newStatus);
        this.details = details;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public HistoryAction getAction() { return action; }
    public void setAction(HistoryAction action) { this.action = action; }
    
    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}