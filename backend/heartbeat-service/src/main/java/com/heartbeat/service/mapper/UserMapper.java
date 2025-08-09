package com.heartbeat.service.mapper;

import com.heartbeat.common.dto.UserDTO;
import com.heartbeat.repo.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "patientsCreated", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "vitalsRecorded", ignore = true)
    @Mapping(target = "filesUploaded", ignore = true)
    @Mapping(target = "usersCreated", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "patientsCreated", ignore = true)
    @Mapping(target = "treatments", ignore = true)
    @Mapping(target = "vitalsRecorded", ignore = true)
    @Mapping(target = "filesUploaded", ignore = true)
    @Mapping(target = "usersCreated", ignore = true)
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);
}