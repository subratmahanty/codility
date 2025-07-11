package com.workflowplatform.service;

import com.workflowplatform.dto.CheckerDecisionRequest;
import com.workflowplatform.dto.CreateCaseRequest;
import com.workflowplatform.dto.MakerDecisionRequest;
import com.workflowplatform.model.*;
import com.workflowplatform.repository.DecisionItemRepository;
import com.workflowplatform.repository.WorkflowCaseRepository;
import com.workflowplatform.repository.WorkflowDecisionRepository;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private WorkflowCaseRepository workflowCaseRepository;

    @Autowired
    private DecisionItemRepository decisionItemRepository;

    @Autowired
    private WorkflowDecisionRepository workflowDecisionRepository;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final String PROCESS_DEFINITION_KEY = "threeLevelWorkflow";

    public WorkflowCase createCase(CreateCaseRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        // Create workflow case
        WorkflowCase workflowCase = new WorkflowCase(
            request.getCaseTitle(),
            request.getDescription(),
            currentUser
        );
        workflowCase.setStatus(WorkflowCase.CaseStatus.IN_PROGRESS);
        workflowCase = workflowCaseRepository.save(workflowCase);

        // Create decision items
        for (CreateCaseRequest.DecisionItemRequest itemRequest : request.getDecisionItems()) {
            DecisionItem item = new DecisionItem(
                itemRequest.getItemName(),
                itemRequest.getDescription(),
                workflowCase
            );
            decisionItemRepository.save(item);
        }

        // Start workflow process
        Map<String, Object> variables = new HashMap<>();
        variables.put("caseId", workflowCase.getId());
        variables.put("caseTitle", workflowCase.getCaseTitle());
        variables.put("createdBy", currentUser);
        variables.put("currentLevel", "L1");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            PROCESS_DEFINITION_KEY, 
            workflowCase.getId().toString(), 
            variables
        );

        workflowCase.setProcessInstanceId(processInstance.getId());
        return workflowCaseRepository.save(workflowCase);
    }

    public void processMakerDecision(MakerDecisionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        WorkflowCase workflowCase = workflowCaseRepository.findById(request.getCaseId())
            .orElseThrow(() -> new RuntimeException("Workflow case not found"));

        // Update decision items with maker decisions
        for (MakerDecisionRequest.ItemDecision decision : request.getDecisions()) {
            DecisionItem item = decisionItemRepository.findById(decision.getItemId())
                .orElseThrow(() -> new RuntimeException("Decision item not found"));
            
            item.setMakerDecision(decision.getDecision());
            item.setMakerComments(decision.getComments());
            decisionItemRepository.save(item);
        }

        // Record the maker decision
        WorkflowDecision workflowDecision = new WorkflowDecision(
            workflowCase.getCurrentLevel(),
            WorkflowDecision.DecisionRole.MAKER,
            currentUser,
            WorkflowDecision.DecisionAction.APPROVE,
            workflowCase
        );
        workflowDecision.setComments(request.getComments());
        workflowDecisionRepository.save(workflowDecision);

        // Complete the maker task
        Map<String, Object> variables = new HashMap<>();
        variables.put("makerDecision", "SUBMITTED");
        variables.put("makerBy", currentUser);
        
        taskService.complete(request.getTaskId(), variables);
    }

    public void processCheckerDecision(CheckerDecisionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        WorkflowCase workflowCase = workflowCaseRepository.findById(request.getCaseId())
            .orElseThrow(() -> new RuntimeException("Workflow case not found"));

        // Update decision items with checker decisions
        boolean allDecisionsMatch = true;
        for (CheckerDecisionRequest.ItemDecision decision : request.getDecisions()) {
            DecisionItem item = decisionItemRepository.findById(decision.getItemId())
                .orElseThrow(() -> new RuntimeException("Decision item not found"));
            
            item.setCheckerDecision(decision.getDecision());
            item.setCheckerComments(decision.getComments());
            
            // Check if checker decision matches maker decision
            if (!item.isDecisionMatching()) {
                allDecisionsMatch = false;
            }
            
            // Set final decision based on checker override or matching decisions
            if (decision.isOverride() || item.isDecisionMatching()) {
                item.setFinalDecision(decision.getDecision());
            }
            
            decisionItemRepository.save(item);
        }

        // Record the checker decision
        WorkflowDecision.DecisionAction action = allDecisionsMatch ? 
            WorkflowDecision.DecisionAction.APPROVE : 
            WorkflowDecision.DecisionAction.ESCALATE;

        WorkflowDecision workflowDecision = new WorkflowDecision(
            workflowCase.getCurrentLevel(),
            WorkflowDecision.DecisionRole.CHECKER,
            currentUser,
            action,
            workflowCase
        );
        workflowDecision.setComments(request.getComments());
        workflowDecisionRepository.save(workflowDecision);

        // Complete the checker task
        Map<String, Object> variables = new HashMap<>();
        variables.put("checkerDecision", allDecisionsMatch ? "APPROVE" : "ESCALATE");
        variables.put("checkerBy", currentUser);
        variables.put("allDecisionsMatch", allDecisionsMatch);
        
        // If escalating, update the workflow case level
        if (!allDecisionsMatch) {
            if (workflowCase.getCurrentLevel() == WorkflowCase.WorkflowLevel.L1) {
                workflowCase.setCurrentLevel(WorkflowCase.WorkflowLevel.L2);
            } else if (workflowCase.getCurrentLevel() == WorkflowCase.WorkflowLevel.L2) {
                workflowCase.setCurrentLevel(WorkflowCase.WorkflowLevel.L3);
            }
            workflowCaseRepository.save(workflowCase);
        } else {
            // If all decisions match, complete the case
            workflowCase.setStatus(WorkflowCase.CaseStatus.COMPLETED);
            workflowCaseRepository.save(workflowCase);
        }
        
        taskService.complete(request.getTaskId(), variables);
    }

    public void processL3Decision(Long caseId, String taskId, String comments) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        WorkflowCase workflowCase = workflowCaseRepository.findById(caseId)
            .orElseThrow(() -> new RuntimeException("Workflow case not found"));

        // Record the L3 decision
        WorkflowDecision workflowDecision = new WorkflowDecision(
            WorkflowCase.WorkflowLevel.L3,
            WorkflowDecision.DecisionRole.MAKER, // L3 acts as final decision maker
            currentUser,
            WorkflowDecision.DecisionAction.APPROVE,
            workflowCase
        );
        workflowDecision.setComments(comments);
        workflowDecisionRepository.save(workflowDecision);

        // Complete the case
        workflowCase.setStatus(WorkflowCase.CaseStatus.COMPLETED);
        workflowCaseRepository.save(workflowCase);

        // Complete the L3 task
        Map<String, Object> variables = new HashMap<>();
        variables.put("l3Decision", "APPROVE");
        variables.put("l3By", currentUser);
        
        taskService.complete(taskId, variables);
    }

    public List<WorkflowCase> getMyCases() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        return workflowCaseRepository.findByCreatedBy(currentUser);
    }

    public List<Task> getMyTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        return taskService.createTaskQuery().taskAssignee(currentUser).list();
    }

    public List<Task> getMyGroupTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        List<String> groups = user.getRoles().stream()
            .map(role -> mapRoleToGroup(role))
            .filter(group -> group != null)
            .toList();
        
        return taskService.createTaskQuery().taskCandidateGroupIn(groups).list();
    }

    private String mapRoleToGroup(User.Role role) {
        return switch (role) {
            case ROLE_L1_MAKER -> "L1_MAKERS";
            case ROLE_L1_CHECKER -> "L1_CHECKERS";
            case ROLE_L2_MAKER -> "L2_MAKERS";
            case ROLE_L2_CHECKER -> "L2_CHECKERS";
            case ROLE_L3_DECISION_MAKER -> "L3_DECISION_MAKERS";
            default -> null;
        };
    }

    public Optional<WorkflowCase> getCaseById(Long id) {
        return workflowCaseRepository.findById(id);
    }

    public List<DecisionItem> getDecisionItems(Long caseId) {
        return decisionItemRepository.findByWorkflowCaseId(caseId);
    }

    public List<WorkflowDecision> getCaseDecisions(Long caseId) {
        return workflowDecisionRepository.findByWorkflowCaseId(caseId);
    }

    public void claimTask(String taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        taskService.claim(taskId, currentUser);
    }
}