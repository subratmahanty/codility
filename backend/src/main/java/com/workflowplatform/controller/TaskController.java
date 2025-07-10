package com.workflowplatform.controller;

import com.workflowplatform.dto.TaskResponse;
import com.workflowplatform.dto.CompleteTaskRequest;
import com.workflowplatform.service.WorkflowTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.form.api.FormInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "Workflow task management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    @Autowired
    private WorkflowTaskService taskService;

    @GetMapping
    @Operation(summary = "Get user tasks", description = "Get all tasks assigned to the current user")
    public ResponseEntity<List<TaskResponse>> getUserTasks() {
        try {
            List<TaskResponse> tasks = taskService.getCurrentUserTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user tasks: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID", description = "Get a specific task by its ID")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String taskId) {
        try {
            TaskResponse task = taskService.getTaskById(taskId);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch task: " + e.getMessage(), e);
        }
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Complete task", description = "Complete a task with optional variables and comment")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) CompleteTaskRequest request) {
        
        try {
            if (request == null) {
                request = new CompleteTaskRequest();
            }
            
            taskService.completeTask(taskId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task completed successfully");
            response.put("taskId", taskId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to complete task");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{taskId}/form")
    @Operation(summary = "Get task form", description = "Get the form associated with a task")
    public ResponseEntity<Map<String, Object>> getTaskForm(@PathVariable String taskId) {
        try {
            FormInfo formInfo = taskService.getTaskForm(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("formInfo", formInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get task form");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{taskId}/claim")
    @Operation(summary = "Claim task", description = "Claim a task for the current user")
    public ResponseEntity<Map<String, Object>> claimTask(@PathVariable String taskId) {
        try {
            String currentUser = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            
            taskService.claimTask(taskId, currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task claimed successfully");
            response.put("taskId", taskId);
            response.put("assignee", currentUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to claim task");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{taskId}/unclaim")
    @Operation(summary = "Unclaim task", description = "Unclaim a task")
    public ResponseEntity<Map<String, Object>> unclaimTask(@PathVariable String taskId) {
        try {
            taskService.unclaimTask(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task unclaimed successfully");
            response.put("taskId", taskId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to unclaim task");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{taskId}/delegate")
    @Operation(summary = "Delegate task", description = "Delegate a task to another user")
    public ResponseEntity<Map<String, Object>> delegateTask(
            @PathVariable String taskId,
            @RequestParam String userId) {
        
        try {
            taskService.delegateTask(taskId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task delegated successfully");
            response.put("taskId", taskId);
            response.put("delegatedTo", userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delegate task");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{taskId}/assign")
    @Operation(summary = "Assign task", description = "Assign a task to a user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    public ResponseEntity<Map<String, Object>> assignTask(
            @PathVariable String taskId,
            @RequestParam String userId) {
        
        try {
            taskService.setTaskAssignee(taskId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task assigned successfully");
            response.put("taskId", taskId);
            response.put("assignee", userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to assign task");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{taskId}/priority")
    @Operation(summary = "Set task priority", description = "Set the priority of a task")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    public ResponseEntity<Map<String, Object>> setTaskPriority(
            @PathVariable String taskId,
            @RequestParam int priority) {
        
        try {
            taskService.setTaskPriority(taskId, priority);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task priority updated successfully");
            response.put("taskId", taskId);
            response.put("priority", priority);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update task priority");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}