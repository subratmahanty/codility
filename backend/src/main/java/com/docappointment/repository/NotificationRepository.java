package com.docappointment.repository;

import com.docappointment.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByRecipientId(String recipientId);
    
    List<Notification> findByAppointmentId(String appointmentId);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    List<Notification> findByRecipientIdAndType(String recipientId, Notification.NotificationType type);
    
    Optional<Notification> findByExternalMessageId(String externalMessageId);
    
    @Query("{'status': 'PENDING', 'sentAt': {'$lt': ?0}}")
    List<Notification> findPendingNotificationsOlderThan(LocalDateTime dateTime);
    
    @Query("{'recipientId': ?0, 'sentAt': {'$gte': ?1, '$lte': ?2}}")
    List<Notification> findByRecipientAndDateRange(String recipientId, LocalDateTime start, LocalDateTime end);
}