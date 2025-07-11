package com.workflowplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_decisions")
public class WorkflowDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WorkflowCase.WorkflowLevel level;

    @Enumerated(EnumType.STRING)
    private DecisionRole role;

    @Column(name = "decision_by")
    private String decisionBy;

    @Enumerated(EnumType.STRING)
    private DecisionAction action;

    @Size(max = 1000)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_case_id")
    private WorkflowCase workflowCase;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    public enum DecisionRole {
        MAKER,
        CHECKER
    }

    public enum DecisionAction {
        APPROVE,
        REJECT,
        ESCALATE,
        OVERRIDE
    }

    @PrePersist
    protected void onCreate() {
        decisionDate = LocalDateTime.now();
    }

    // Constructors
    public WorkflowDecision() {}

    public WorkflowDecision(WorkflowCase.WorkflowLevel level, DecisionRole role, 
                           String decisionBy, DecisionAction action, WorkflowCase workflowCase) {
        this.level = level;
        this.role = role;
        this.decisionBy = decisionBy;
        this.action = action;
        this.workflowCase = workflowCase;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkflowCase.WorkflowLevel getLevel() {
        return level;
    }

    public void setLevel(WorkflowCase.WorkflowLevel level) {
        this.level = level;
    }

    public DecisionRole getRole() {
        return role;
    }

    public void setRole(DecisionRole role) {
        this.role = role;
    }

    public String getDecisionBy() {
        return decisionBy;
    }

    public void setDecisionBy(String decisionBy) {
        this.decisionBy = decisionBy;
    }

    public DecisionAction getAction() {
        return action;
    }

    public void setAction(DecisionAction action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public WorkflowCase getWorkflowCase() {
        return workflowCase;
    }

    public void setWorkflowCase(WorkflowCase workflowCase) {
        this.workflowCase = workflowCase;
    }

    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }
}