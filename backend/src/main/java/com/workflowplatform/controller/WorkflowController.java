package com.workflowplatform.controller;

import com.workflowplatform.dto.CheckerDecisionRequest;
import com.workflowplatform.dto.CreateCaseRequest;
import com.workflowplatform.dto.MakerDecisionRequest;
import com.workflowplatform.model.DecisionItem;
import com.workflowplatform.model.WorkflowCase;
import com.workflowplatform.model.WorkflowDecision;
import com.workflowplatform.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow")
@Tag(name = "Workflow", description = "3-Level Workflow Management API")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/cases")
    @Operation(summary = "Create a new workflow case")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WorkflowCase> createCase(@Valid @RequestBody CreateCaseRequest request) {
        WorkflowCase workflowCase = workflowService.createCase(request);
        return ResponseEntity.ok(workflowCase);
    }

    @GetMapping("/cases/my")
    @Operation(summary = "Get my workflow cases")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkflowCase>> getMyCases() {
        List<WorkflowCase> cases = workflowService.getMyCases();
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/cases/{id}")
    @Operation(summary = "Get workflow case by ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WorkflowCase> getCaseById(@PathVariable Long id) {
        return workflowService.getCaseById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cases/{id}/items")
    @Operation(summary = "Get decision items for a case")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<DecisionItem>> getDecisionItems(@PathVariable Long id) {
        List<DecisionItem> items = workflowService.getDecisionItems(id);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/cases/{id}/decisions")
    @Operation(summary = "Get all decisions for a case")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkflowDecision>> getCaseDecisions(@PathVariable Long id) {
        List<WorkflowDecision> decisions = workflowService.getCaseDecisions(id);
        return ResponseEntity.ok(decisions);
    }

    @GetMapping("/tasks/my")
    @Operation(summary = "Get my assigned tasks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getMyTasks() {
        List<Task> tasks = workflowService.getMyTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/group")
    @Operation(summary = "Get tasks available to my groups")
    @PreAuthorize("hasRole('L1_MAKER') or hasRole('L1_CHECKER') or hasRole('L2_MAKER') or hasRole('L2_CHECKER') or hasRole('L3_DECISION_MAKER')")
    public ResponseEntity<List<Task>> getMyGroupTasks() {
        List<Task> tasks = workflowService.getMyGroupTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks/{taskId}/claim")
    @Operation(summary = "Claim a task")
    @PreAuthorize("hasRole('L1_MAKER') or hasRole('L1_CHECKER') or hasRole('L2_MAKER') or hasRole('L2_CHECKER') or hasRole('L3_DECISION_MAKER')")
    public ResponseEntity<Void> claimTask(@PathVariable String taskId) {
        workflowService.claimTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decisions/maker")
    @Operation(summary = "Submit maker decision")
    @PreAuthorize("hasRole('L1_MAKER') or hasRole('L2_MAKER')")
    public ResponseEntity<Void> submitMakerDecision(@Valid @RequestBody MakerDecisionRequest request) {
        workflowService.processMakerDecision(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decisions/checker")
    @Operation(summary = "Submit checker decision")
    @PreAuthorize("hasRole('L1_CHECKER') or hasRole('L2_CHECKER')")
    public ResponseEntity<Void> submitCheckerDecision(@Valid @RequestBody CheckerDecisionRequest request) {
        workflowService.processCheckerDecision(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decisions/l3")
    @Operation(summary = "Submit L3 final decision")
    @PreAuthorize("hasRole('L3_DECISION_MAKER')")
    public ResponseEntity<Void> submitL3Decision(
            @RequestParam Long caseId,
            @RequestParam String taskId,
            @RequestParam(required = false) String comments) {
        workflowService.processL3Decision(caseId, taskId, comments);
        return ResponseEntity.ok().build();
    }

    // Additional endpoints for dashboard and monitoring
    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get workflow dashboard statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        // Implementation for dashboard statistics
        Map<String, Object> stats = Map.of(
            "totalCases", 0,
            "pendingCases", 0,
            "completedCases", 0,
            "escalatedCases", 0
        );
        return ResponseEntity.ok(stats);
    }
}