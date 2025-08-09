package com.heartbeat.service;

import com.heartbeat.common.dto.PatientDTO;
import com.heartbeat.common.exception.HeartbeatException;
import com.heartbeat.common.exception.ResourceNotFoundException;
import com.heartbeat.repo.entity.Patient;
import com.heartbeat.repo.entity.PatientIdentifier;
import com.heartbeat.repo.entity.User;
import com.heartbeat.repo.repository.PatientRepository;
import com.heartbeat.repo.repository.UserRepository;
import com.heartbeat.service.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO, UUID createdBy) {
        log.info("Creating new patient: {} {}", patientDTO.getFirstName(), patientDTO.getLastName());

        // Validate unique identifiers
        validateUniqueIdentifiers(patientDTO);

        // Get the user who is creating the patient
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> ResourceNotFoundException.user(createdBy));

        Patient patient = patientMapper.toEntity(patientDTO);
        patient.setCreatedBy(creator);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        // Set up identifiers if provided
        if (patientDTO.getIdentifiers() != null) {
            for (PatientDTO.PatientIdentifierDTO identifierDTO : patientDTO.getIdentifiers()) {
                PatientIdentifier identifier = new PatientIdentifier();
                identifier.setIdentifierType(identifierDTO.getType());
                identifier.setIdentifierValue(identifierDTO.getValue());
                identifier.setPatient(patient);
                identifier.setCreatedAt(LocalDateTime.now());
                patient.getIdentifiers().add(identifier);
            }
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());

        return patientMapper.toDTO(savedPatient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatient(UUID patientId) {
        log.debug("Fetching patient with ID: {}", patientId);
        
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> ResourceNotFoundException.patient(patientId));

        return patientMapper.toDTO(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        log.debug("Fetching all patients with pagination: {}", pageable);
        
        Page<Patient> patients = patientRepository.findByIsActiveTrue(pageable);
        return patients.map(patientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> searchPatients(String query, Pageable pageable) {
        log.debug("Searching patients with query: '{}' and pagination: {}", query, pageable);
        
        Page<Patient> patients = patientRepository.searchPatients(query, pageable);
        return patients.map(patientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PatientDTO findPatientByIdentifier(String identifierType, String identifierValue) {
        log.debug("Searching patient by identifier: {} = {}", identifierType, identifierValue);
        
        Patient patient = patientRepository.findByIdentifierTypeAndValue(identifierType, identifierValue)
                .orElseThrow(() -> new HeartbeatException(
                        "Patient not found with " + identifierType + ": " + identifierValue,
                        HttpStatus.NOT_FOUND,
                        "PATIENT_NOT_FOUND_BY_IDENTIFIER"
                ));

        return patientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO updatePatient(UUID patientId, PatientDTO patientDTO, UUID updatedBy) {
        log.info("Updating patient with ID: {}", patientId);

        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> ResourceNotFoundException.patient(patientId));

        // Validate unique identifiers if being updated
        if (patientDTO.getIdentifiers() != null) {
            validateUniqueIdentifiersForUpdate(patientDTO, patientId);
        }

        // Update patient fields
        updatePatientFields(existingPatient, patientDTO);
        existingPatient.setUpdatedAt(LocalDateTime.now());

        Patient savedPatient = patientRepository.save(existingPatient);
        log.info("Patient updated successfully: {}", patientId);

        return patientMapper.toDTO(savedPatient);
    }

    @Transactional
    public void deactivatePatient(UUID patientId, UUID deactivatedBy) {
        log.info("Deactivating patient with ID: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> ResourceNotFoundException.patient(patientId));

        patient.setIsActive(false);
        patient.setUpdatedAt(LocalDateTime.now());
        
        patientRepository.save(patient);
        log.info("Patient deactivated successfully: {}", patientId);
    }

    @Transactional
    public void reactivatePatient(UUID patientId, UUID reactivatedBy) {
        log.info("Reactivating patient with ID: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> ResourceNotFoundException.patient(patientId));

        patient.setIsActive(true);
        patient.setUpdatedAt(LocalDateTime.now());
        
        patientRepository.save(patient);
        log.info("Patient reactivated successfully: {}", patientId);
    }

    @Transactional
    public PatientDTO updatePatientPhoto(UUID patientId, String photoUrl) {
        log.info("Updating photo for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> ResourceNotFoundException.patient(patientId));

        patient.setPhotoUrl(photoUrl);
        patient.setUpdatedAt(LocalDateTime.now());

        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDTO(savedPatient);
    }

    @Transactional(readOnly = true)
    public long getPatientCount() {
        return patientRepository.countByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public long getNewPatientsCount(LocalDateTime since) {
        return patientRepository.countByCreatedAtAfterAndIsActiveTrue(since);
    }

    private void validateUniqueIdentifiers(PatientDTO patientDTO) {
        if (patientDTO.getIdentifiers() != null) {
            for (PatientDTO.PatientIdentifierDTO identifier : patientDTO.getIdentifiers()) {
                if (patientRepository.existsByIdentifierTypeAndValue(
                        identifier.getType().name(), identifier.getValue())) {
                    throw new HeartbeatException(
                            "Patient with " + identifier.getType() + " " + identifier.getValue() + " already exists",
                            HttpStatus.CONFLICT,
                            "DUPLICATE_IDENTIFIER"
                    );
                }
            }
        }
    }

    private void validateUniqueIdentifiersForUpdate(PatientDTO patientDTO, UUID patientId) {
        if (patientDTO.getIdentifiers() != null) {
            for (PatientDTO.PatientIdentifierDTO identifier : patientDTO.getIdentifiers()) {
                if (patientRepository.existsByIdentifierTypeAndValueExcludingPatient(
                        identifier.getType().name(), identifier.getValue(), patientId)) {
                    throw new HeartbeatException(
                            "Another patient with " + identifier.getType() + " " + identifier.getValue() + " already exists",
                            HttpStatus.CONFLICT,
                            "DUPLICATE_IDENTIFIER"
                    );
                }
            }
        }
    }

    private void updatePatientFields(Patient existingPatient, PatientDTO patientDTO) {
        if (patientDTO.getFirstName() != null) {
            existingPatient.setFirstName(patientDTO.getFirstName());
        }
        if (patientDTO.getLastName() != null) {
            existingPatient.setLastName(patientDTO.getLastName());
        }
        if (patientDTO.getDateOfBirth() != null) {
            existingPatient.setDateOfBirth(patientDTO.getDateOfBirth());
        }
        if (patientDTO.getGender() != null) {
            existingPatient.setGender(patientDTO.getGender());
        }
        if (patientDTO.getPhone() != null) {
            existingPatient.setPhone(patientDTO.getPhone());
        }
        if (patientDTO.getEmail() != null) {
            existingPatient.setEmail(patientDTO.getEmail());
        }
        if (patientDTO.getAddress() != null) {
            existingPatient.setAddress(patientDTO.getAddress());
        }
        if (patientDTO.getCity() != null) {
            existingPatient.setCity(patientDTO.getCity());
        }
        if (patientDTO.getState() != null) {
            existingPatient.setState(patientDTO.getState());
        }
        if (patientDTO.getPincode() != null) {
            existingPatient.setPincode(patientDTO.getPincode());
        }
        if (patientDTO.getEmergencyContactName() != null) {
            existingPatient.setEmergencyContactName(patientDTO.getEmergencyContactName());
        }
        if (patientDTO.getEmergencyContactPhone() != null) {
            existingPatient.setEmergencyContactPhone(patientDTO.getEmergencyContactPhone());
        }
        if (patientDTO.getBloodGroup() != null) {
            existingPatient.setBloodGroup(patientDTO.getBloodGroup());
        }
        if (patientDTO.getAllergies() != null) {
            existingPatient.setAllergies(patientDTO.getAllergies());
        }
        if (patientDTO.getMedicalHistory() != null) {
            existingPatient.setMedicalHistory(patientDTO.getMedicalHistory());
        }
    }
}