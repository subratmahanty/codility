package com.docappointment.repository;

import com.docappointment.model.AppointmentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentHistoryRepository extends MongoRepository<AppointmentHistory, String> {
    
    List<AppointmentHistory> findByAppointmentId(String appointmentId);
    
    List<AppointmentHistory> findByActorId(String actorId);
    
    List<AppointmentHistory> findByAction(AppointmentHistory.HistoryAction action);
    
    List<AppointmentHistory> findByAppointmentIdOrderByTimestampDesc(String appointmentId);
    
    @Query("{'appointmentId': ?0, 'action': ?1}")
    List<AppointmentHistory> findByAppointmentAndAction(String appointmentId, AppointmentHistory.HistoryAction action);
    
    @Query("{'actorId': ?0, 'timestamp': {'$gte': ?1, '$lte': ?2}}")
    List<AppointmentHistory> findByActorAndDateRange(String actorId, LocalDateTime start, LocalDateTime end);
    
    @Query("{'appointmentId': ?0, 'timestamp': {'$gte': ?1, '$lte': ?2}}")
    List<AppointmentHistory> findByAppointmentAndDateRange(String appointmentId, LocalDateTime start, LocalDateTime end);
}