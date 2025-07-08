package com.workflowplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application for the Workflow Platform.
 * Integrates Flowable BPMN, DMN, and Form engines with REST API support.
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class WorkflowPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowPlatformApplication.class, args);
    }
}