package com.docappointment.repository;

import com.docappointment.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    
    List<Appointment> findByPatientId(String patientId);
    
    List<Appointment> findByDoctorId(String doctorId);
    
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    List<Appointment> findByPatientIdAndStatus(String patientId, Appointment.AppointmentStatus status);
    
    List<Appointment> findByDoctorIdAndStatus(String doctorId, Appointment.AppointmentStatus status);
    
    List<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByDoctorIdAndStartTimeBetween(String doctorId, LocalDateTime start, LocalDateTime end);
    
    @Query("{'doctorId': ?0, 'startTime': {'$gte': ?1, '$lt': ?2}}")
    List<Appointment> findByDoctorAndDateRange(String doctorId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'status': 'SCHEDULED', 'startTime': {'$gte': ?0, '$lte': ?1}, 'reminderSent': false}")
    List<Appointment> findAppointmentsForReminder(LocalDateTime now, LocalDateTime reminderWindow);
    
    @Query("{'status': 'SCHEDULED', 'endTime': {'$lt': ?0}}")
    List<Appointment> findAppointmentsToComplete(LocalDateTime now);
    
    boolean existsByDoctorIdAndStartTimeBetween(String doctorId, LocalDateTime start, LocalDateTime end);
}