package com.example.flowable.controller;

import com.example.flowable.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Flowable Spring Boot Application");
        response.put("version", "1.0.0");
        response.put("description", "A Spring Boot application integrated with Flowable workflow engine");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "flowable-spring-boot");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo/start-simple-approval")
    public ResponseEntity<Map<String, Object>> startSimpleApprovalDemo() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "user@example.com");
        variables.put("requestTitle", "Demo Request");
        variables.put("requestDescription", "This is a demo approval request");

        var processInstance = workflowService.startProcess("simpleApproval", "demo-" + System.currentTimeMillis(), variables);

        Map<String, Object> response = new HashMap<>();
        response.put("processInstanceId", processInstance.getId());
        response.put("processDefinitionId", processInstance.getProcessDefinitionId());
        response.put("businessKey", processInstance.getBusinessKey());
        response.put("message", "Simple approval process started successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo/start-vacation-request")
    public ResponseEntity<Map<String, Object>> startVacationRequestDemo(@RequestParam(defaultValue = "3") int days) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "employee@example.com");
        variables.put("startDate", "2024-01-15");
        variables.put("endDate", "2024-01-18");
        variables.put("numberOfDays", days);
        variables.put("reason", "Family vacation");

        var processInstance = workflowService.startProcess("vacationRequest", "vacation-" + System.currentTimeMillis(), variables);

        Map<String, Object> response = new HashMap<>();
        response.put("processInstanceId", processInstance.getId());
        response.put("processDefinitionId", processInstance.getProcessDefinitionId());
        response.put("businessKey", processInstance.getBusinessKey());
        response.put("numberOfDays", days);
        response.put("message", "Vacation request process started successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Flowable Spring Boot");
        info.put("description", "This application demonstrates integration between Spring Boot and Flowable workflow engine");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Flowable Modeler", "http://localhost:8080/flowable-modeler (admin/password)");
        endpoints.put("Flowable Admin", "http://localhost:8080/flowable-admin (admin/password)");
        endpoints.put("Flowable Task", "http://localhost:8080/flowable-task (admin/password)");
        endpoints.put("Flowable IDM", "http://localhost:8080/flowable-idm (admin/password)");
        endpoints.put("H2 Console", "http://localhost:8080/h2-console (sa/password)");
        
        Map<String, String> apis = new HashMap<>();
        apis.put("Start Simple Approval Demo", "POST /api/public/demo/start-simple-approval");
        apis.put("Start Vacation Request Demo", "POST /api/public/demo/start-vacation-request?days=3");
        apis.put("Get Process Definitions", "GET /api/workflow/definitions");
        apis.put("Get Active Processes", "GET /api/workflow/process/active");
        apis.put("Get Unassigned Tasks", "GET /api/workflow/tasks/unassigned");
        
        info.put("endpoints", endpoints);
        info.put("apis", apis);
        info.put("credentials", Map.of("admin", "admin/password", "user", "user/password"));
        
        return ResponseEntity.ok(info);
    }
}