package com.example.flowable.controller;

import com.example.flowable.dto.ProcessInstanceDto;
import com.example.flowable.dto.StartProcessRequest;
import com.example.flowable.dto.TaskDto;
import com.example.flowable.service.WorkflowService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/process/start")
    public ResponseEntity<ProcessInstanceDto> startProcess(@RequestBody StartProcessRequest request) {
        ProcessInstance processInstance = workflowService.startProcess(
                request.getProcessKey(),
                request.getBusinessKey(),
                request.getVariables()
        );

        ProcessInstanceDto dto = new ProcessInstanceDto(
                processInstance.getId(),
                processInstance.getProcessDefinitionId(),
                processInstance.getBusinessKey(),
                processInstance.isEnded()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/process/{processInstanceId}")
    public ResponseEntity<ProcessInstanceDto> getProcessInstance(@PathVariable String processInstanceId) {
        ProcessInstance processInstance = workflowService.getProcessInstance(processInstanceId);
        
        if (processInstance == null) {
            return ResponseEntity.notFound().build();
        }

        ProcessInstanceDto dto = new ProcessInstanceDto(
                processInstance.getId(),
                processInstance.getProcessDefinitionId(),
                processInstance.getBusinessKey(),
                processInstance.isEnded()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/process/active")
    public ResponseEntity<List<ProcessInstanceDto>> getActiveProcessInstances() {
        List<ProcessInstance> processInstances = workflowService.getActiveProcessInstances();
        
        List<ProcessInstanceDto> dtos = processInstances.stream()
                .map(pi -> new ProcessInstanceDto(
                        pi.getId(),
                        pi.getProcessDefinitionId(),
                        pi.getBusinessKey(),
                        pi.isEnded()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tasks/user/{assignee}")
    public ResponseEntity<List<TaskDto>> getTasksForUser(@PathVariable String assignee) {
        List<Task> tasks = workflowService.getTasksForUser(assignee);
        
        List<TaskDto> taskDtos = tasks.stream()
                .map(task -> new TaskDto(
                        task.getId(),
                        task.getName(),
                        task.getAssignee(),
                        task.getProcessInstanceId(),
                        task.getCreateTime()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDtos);
    }

    @GetMapping("/tasks/unassigned")
    public ResponseEntity<List<TaskDto>> getUnassignedTasks() {
        List<Task> tasks = workflowService.getUnassignedTasks();
        
        List<TaskDto> taskDtos = tasks.stream()
                .map(task -> new TaskDto(
                        task.getId(),
                        task.getName(),
                        task.getAssignee(),
                        task.getProcessInstanceId(),
                        task.getCreateTime()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDtos);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable String taskId, @RequestBody(required = false) Map<String, Object> variables) {
        if (variables != null && !variables.isEmpty()) {
            workflowService.completeTask(taskId, variables);
        } else {
            workflowService.completeTask(taskId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/{taskId}/assign/{assignee}")
    public ResponseEntity<Void> assignTask(@PathVariable String taskId, @PathVariable String assignee) {
        workflowService.assignTask(taskId, assignee);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<ProcessDefinition>> getProcessDefinitions() {
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        return ResponseEntity.ok(definitions);
    }

    @DeleteMapping("/process/{processInstanceId}")
    public ResponseEntity<Void> deleteProcessInstance(@PathVariable String processInstanceId, @RequestParam(required = false) String reason) {
        workflowService.deleteProcessInstance(processInstanceId, reason != null ? reason : "Deleted via API");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/process/{processInstanceId}/variables")
    public ResponseEntity<Map<String, Object>> getProcessVariables(@PathVariable String processInstanceId) {
        Map<String, Object> variables = workflowService.getProcessVariables(processInstanceId);
        return ResponseEntity.ok(variables);
    }

    @PostMapping("/process/{processInstanceId}/variables")
    public ResponseEntity<Void> setProcessVariables(@PathVariable String processInstanceId, @RequestBody Map<String, Object> variables) {
        workflowService.setProcessVariables(processInstanceId, variables);
        return ResponseEntity.ok().build();
    }
}