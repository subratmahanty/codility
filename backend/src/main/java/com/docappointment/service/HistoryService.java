package com.docappointment.service;

import com.docappointment.model.AppointmentHistory;
import com.docappointment.repository.AppointmentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {

    @Autowired
    private AppointmentHistoryRepository historyRepository;

    @Async
    public void recordAppointmentAction(String appointmentId, AppointmentHistory.HistoryAction action, String actorId) {
        AppointmentHistory history = new AppointmentHistory(appointmentId, action, actorId);
        historyRepository.save(history);
    }

    @Async
    public void recordAppointmentAction(String appointmentId, AppointmentHistory.HistoryAction action, 
                                      String actorId, String oldStatus, String newStatus) {
        AppointmentHistory history = new AppointmentHistory(appointmentId, action, actorId, oldStatus, newStatus);
        historyRepository.save(history);
    }

    @Async
    public void recordAppointmentAction(String appointmentId, AppointmentHistory.HistoryAction action, 
                                      String actorId, String oldStatus, String newStatus, Map<String, Object> details) {
        AppointmentHistory history = new AppointmentHistory(appointmentId, action, actorId, oldStatus, newStatus, details);
        historyRepository.save(history);
    }

    public List<AppointmentHistory> getAppointmentHistory(String appointmentId) {
        return historyRepository.findByAppointmentIdOrderByTimestampDesc(appointmentId);
    }

    public List<AppointmentHistory> getUserActions(String actorId) {
        return historyRepository.findByActorId(actorId);
    }

    public List<AppointmentHistory> getActionsByType(AppointmentHistory.HistoryAction action) {
        return historyRepository.findByAction(action);
    }

    public List<AppointmentHistory> getAppointmentActionsByType(String appointmentId, AppointmentHistory.HistoryAction action) {
        return historyRepository.findByAppointmentAndAction(appointmentId, action);
    }

    public List<AppointmentHistory> getUserActionsInDateRange(String actorId, LocalDateTime start, LocalDateTime end) {
        return historyRepository.findByActorAndDateRange(actorId, start, end);
    }

    public List<AppointmentHistory> getAppointmentHistoryInDateRange(String appointmentId, LocalDateTime start, LocalDateTime end) {
        return historyRepository.findByAppointmentAndDateRange(appointmentId, start, end);
    }
}