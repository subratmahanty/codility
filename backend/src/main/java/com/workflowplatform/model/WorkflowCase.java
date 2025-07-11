package com.workflowplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_cases")
public class WorkflowCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String caseTitle;

    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private WorkflowLevel currentLevel = WorkflowLevel.L1;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "workflowCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DecisionItem> decisionItems = new ArrayList<>();

    @OneToMany(mappedBy = "workflowCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowDecision> decisions = new ArrayList<>();

    public enum CaseStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        REJECTED,
        CANCELLED
    }

    public enum WorkflowLevel {
        L1,
        L2,
        L3
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    // Constructors
    public WorkflowCase() {}

    public WorkflowCase(String caseTitle, String description, String createdBy) {
        this.caseTitle = caseTitle;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public WorkflowLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(WorkflowLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<DecisionItem> getDecisionItems() {
        return decisionItems;
    }

    public void setDecisionItems(List<DecisionItem> decisionItems) {
        this.decisionItems = decisionItems;
    }

    public List<WorkflowDecision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<WorkflowDecision> decisions) {
        this.decisions = decisions;
    }
}