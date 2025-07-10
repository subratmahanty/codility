package com.docappointment.service;

import com.docappointment.model.Appointment;
import com.docappointment.model.AppointmentHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HistoryService historyService;

    /**
     * Send appointment reminders every 15 minutes
     * Configured via: app.scheduler.reminder-cron in application.yml
     */
    @Scheduled(cron = "${app.scheduler.reminder-cron}")
    public void sendAppointmentReminders() {
        logger.info("Starting appointment reminder task");
        
        try {
            List<Appointment> appointmentsForReminder = appointmentService.getAppointmentsForReminder();
            
            for (Appointment appointment : appointmentsForReminder) {
                try {
                    // Send reminder notification
                    notificationService.sendAppointmentReminder(appointment);
                    
                    // Mark reminder as sent
                    appointmentService.markReminderSent(appointment.getId());
                    
                    // Record in history
                    historyService.recordAppointmentAction(
                        appointment.getId(), 
                        AppointmentHistory.HistoryAction.REMINDER_SENT, 
                        "SYSTEM"
                    );
                    
                    logger.info("Sent reminder for appointment ID: {}", appointment.getId());
                    
                } catch (Exception e) {
                    logger.error("Failed to send reminder for appointment ID: {}", appointment.getId(), e);
                }
            }
            
            if (!appointmentsForReminder.isEmpty()) {
                logger.info("Processed {} appointment reminders", appointmentsForReminder.size());
            }
            
        } catch (Exception e) {
            logger.error("Error in appointment reminder task", e);
        }
    }

    /**
     * Update appointment statuses to COMPLETED for past appointments
     * Runs every hour
     * Configured via: app.scheduler.status-update-cron in application.yml
     */
    @Scheduled(cron = "${app.scheduler.status-update-cron}")
    public void updateAppointmentStatuses() {
        logger.info("Starting appointment status update task");
        
        try {
            List<Appointment> appointmentsToComplete = appointmentService.getAppointmentsToComplete();
            
            for (Appointment appointment : appointmentsToComplete) {
                try {
                    // Update status to COMPLETED
                    appointmentService.updateAppointmentStatus(
                        appointment.getId(), 
                        Appointment.AppointmentStatus.COMPLETED, 
                        "SYSTEM"
                    );
                    
                    logger.info("Updated appointment ID: {} to COMPLETED", appointment.getId());
                    
                } catch (Exception e) {
                    logger.error("Failed to update status for appointment ID: {}", appointment.getId(), e);
                }
            }
            
            if (!appointmentsToComplete.isEmpty()) {
                logger.info("Updated {} appointments to COMPLETED status", appointmentsToComplete.size());
            }
            
        } catch (Exception e) {
            logger.error("Error in appointment status update task", e);
        }
    }

    /**
     * Cleanup old pending notifications (optional maintenance task)
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldPendingNotifications() {
        logger.info("Starting cleanup of old pending notifications");
        
        try {
            // This is a maintenance task that could be implemented to clean up
            // notifications that have been pending for too long
            // Implementation depends on business requirements
            
            logger.info("Completed cleanup of old pending notifications");
            
        } catch (Exception e) {
            logger.error("Error in notification cleanup task", e);
        }
    }
}