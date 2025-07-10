package com.docappointment.repository;

import com.docappointment.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByRoleAndSpecialty(User.Role role, String specialty);
    
    @Query("{'role': 'DOCTOR'}")
    List<User> findAllDoctors();
    
    @Query("{'role': 'PATIENT'}")
    List<User> findAllPatients();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}