package com.heartbeat.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends HeartbeatException {
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s with id '%s' not found", resourceType, id), 
              HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public static ResourceNotFoundException patient(Object id) {
        return new ResourceNotFoundException("Patient", id);
    }

    public static ResourceNotFoundException user(Object id) {
        return new ResourceNotFoundException("User", id);
    }

    public static ResourceNotFoundException treatment(Object id) {
        return new ResourceNotFoundException("Treatment", id);
    }

    public static ResourceNotFoundException medicine(Object id) {
        return new ResourceNotFoundException("Medicine", id);
    }

    public static ResourceNotFoundException labTest(Object id) {
        return new ResourceNotFoundException("Lab Test", id);
    }

    public static ResourceNotFoundException labTestRequest(Object id) {
        return new ResourceNotFoundException("Lab Test Request", id);
    }

    public static ResourceNotFoundException notification(Object id) {
        return new ResourceNotFoundException("Notification", id);
    }

    public static ResourceNotFoundException file(Object id) {
        return new ResourceNotFoundException("File", id);
    }
}