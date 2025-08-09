package com.heartbeat.service.mapper;

import com.heartbeat.common.dto.PatientDTO;
import com.heartbeat.repo.entity.Patient;
import com.heartbeat.repo.entity.PatientIdentifier;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "identifiers", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "vitals", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Patient toEntity(PatientDTO patientDTO);

    @Mapping(target = "age", expression = "java(patient.getAge())")
    @Mapping(target = "identifiers", source = "identifiers")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "mapUserSummary")
    PatientDTO toDTO(Patient patient);

    List<PatientDTO> toDTOList(List<Patient> patients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "identifierValue", source = "value")
    PatientIdentifier toIdentifierEntity(PatientDTO.PatientIdentifierDTO identifierDTO);

    @Mapping(target = "type", source = "identifierType")
    @Mapping(target = "value", source = "identifierValue")
    PatientDTO.PatientIdentifierDTO toIdentifierDTO(PatientIdentifier identifier);

    @Named("mapUserSummary")
    default PatientDTO.UserSummaryDTO mapUserSummary(com.heartbeat.repo.entity.User user) {
        if (user == null) {
            return null;
        }
        PatientDTO.UserSummaryDTO userSummary = new PatientDTO.UserSummaryDTO();
        userSummary.setId(user.getId());
        userSummary.setFirstName(user.getFirstName());
        userSummary.setLastName(user.getLastName());
        userSummary.setRole(user.getRole().name());
        return userSummary;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "identifiers", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "vitals", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updatePatientFromDTO(PatientDTO patientDTO, @MappingTarget Patient patient);
}