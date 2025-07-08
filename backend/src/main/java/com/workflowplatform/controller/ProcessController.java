package com.workflowplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/processes")
@Tag(name = "Process Management", description = "BPMN and DMN process management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProcessController {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private DmnRepositoryService dmnRepositoryService;

    @PostMapping("/deploy/bpmn")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    @Operation(summary = "Deploy BPMN process", description = "Deploy a BPMN 2.0 process definition")
    public ResponseEntity<Map<String, Object>> deployBpmnProcess(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String deploymentName) {
        
        try {
            String name = deploymentName != null ? deploymentName : file.getOriginalFilename();
            
            Deployment deployment = repositoryService.createDeployment()
                    .name(name)
                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    .deploy();

            List<ProcessDefinition> processDefinitions = repositoryService
                    .createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .list();

            Map<String, Object> response = new HashMap<>();
            response.put("deploymentId", deployment.getId());
            response.put("deploymentName", deployment.getName());
            response.put("processDefinitions", processDefinitions.stream().map(pd -> {
                Map<String, Object> pdMap = new HashMap<>();
                pdMap.put("id", pd.getId());
                pdMap.put("key", pd.getKey());
                pdMap.put("name", pd.getName());
                pdMap.put("version", pd.getVersion());
                pdMap.put("resourceName", pd.getResourceName());
                return pdMap;
            }).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to deploy BPMN process");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/deploy/dmn")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    @Operation(summary = "Deploy DMN decision", description = "Deploy a DMN decision definition")
    public ResponseEntity<Map<String, Object>> deployDmnDecision(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String deploymentName) {
        
        try {
            String name = deploymentName != null ? deploymentName : file.getOriginalFilename();
            
            org.flowable.dmn.api.DmnDeployment deployment = dmnRepositoryService.createDeployment()
                    .name(name)
                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    .deploy();

            Map<String, Object> response = new HashMap<>();
            response.put("deploymentId", deployment.getId());
            response.put("deploymentName", deployment.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to deploy DMN decision");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "List process definitions", description = "Get all deployed process definitions")
    public ResponseEntity<List<Map<String, Object>>> getProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .latestVersion()
                .list();

        List<Map<String, Object>> response = processDefinitions.stream().map(pd -> {
            Map<String, Object> pdMap = new HashMap<>();
            pdMap.put("id", pd.getId());
            pdMap.put("key", pd.getKey());
            pdMap.put("name", pd.getName());
            pdMap.put("version", pd.getVersion());
            pdMap.put("category", pd.getCategory());
            pdMap.put("resourceName", pd.getResourceName());
            pdMap.put("deploymentId", pd.getDeploymentId());
            pdMap.put("suspended", pd.isSuspended());
            return pdMap;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{processDefinitionKey}/start")
    @Operation(summary = "Start process instance", description = "Start a new process instance")
    public ResponseEntity<Map<String, Object>> startProcessInstance(
            @PathVariable String processDefinitionKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        try {
            ProcessInstance processInstance;
            if (variables != null && !variables.isEmpty()) {
                processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
            } else {
                processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("processInstanceId", processInstance.getId());
            response.put("processDefinitionId", processInstance.getProcessDefinitionId());
            response.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
            response.put("businessKey", processInstance.getBusinessKey());
            response.put("isEnded", processInstance.isEnded());
            response.put("isSuspended", processInstance.isSuspended());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to start process instance");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/instances")
    @Operation(summary = "List process instances", description = "Get all running process instances")
    public ResponseEntity<List<Map<String, Object>>> getProcessInstances() {
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .list();

        List<Map<String, Object>> response = processInstances.stream().map(pi -> {
            Map<String, Object> piMap = new HashMap<>();
            piMap.put("id", pi.getId());
            piMap.put("processDefinitionId", pi.getProcessDefinitionId());
            piMap.put("processDefinitionKey", pi.getProcessDefinitionKey());
            piMap.put("businessKey", pi.getBusinessKey());
            piMap.put("startTime", pi.getStartTime());
            piMap.put("isEnded", pi.isEnded());
            piMap.put("isSuspended", pi.isSuspended());
            return piMap;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/instances/{processInstanceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    @Operation(summary = "Delete process instance", description = "Delete a running process instance")
    public ResponseEntity<Map<String, Object>> deleteProcessInstance(
            @PathVariable String processInstanceId,
            @RequestParam(value = "reason", required = false) String deleteReason) {
        
        try {
            String reason = deleteReason != null ? deleteReason : "Deleted by user";
            runtimeService.deleteProcessInstance(processInstanceId, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Process instance deleted successfully");
            response.put("processInstanceId", processInstanceId);
            response.put("deleteReason", reason);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete process instance");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/definitions/{deploymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Undeploy process", description = "Remove a process deployment")
    public ResponseEntity<Map<String, Object>> undeployProcess(@PathVariable String deploymentId) {
        try {
            repositoryService.deleteDeployment(deploymentId, true);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Process deployment deleted successfully");
            response.put("deploymentId", deploymentId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to undeploy process");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}