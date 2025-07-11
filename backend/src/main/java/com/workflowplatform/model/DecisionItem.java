package com.workflowplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "decision_items")
public class DecisionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String itemName;

    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private DecisionType makerDecision;

    @Enumerated(EnumType.STRING)
    private DecisionType checkerDecision;

    @Enumerated(EnumType.STRING)
    private DecisionType finalDecision;

    @Size(max = 500)
    private String makerComments;

    @Size(max = 500)
    private String checkerComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_case_id")
    private WorkflowCase workflowCase;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public enum DecisionType {
        FM, // Financial Model
        PM  // Portfolio Model
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
    public DecisionItem() {}

    public DecisionItem(String itemName, String description, WorkflowCase workflowCase) {
        this.itemName = itemName;
        this.description = description;
        this.workflowCase = workflowCase;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DecisionType getMakerDecision() {
        return makerDecision;
    }

    public void setMakerDecision(DecisionType makerDecision) {
        this.makerDecision = makerDecision;
    }

    public DecisionType getCheckerDecision() {
        return checkerDecision;
    }

    public void setCheckerDecision(DecisionType checkerDecision) {
        this.checkerDecision = checkerDecision;
    }

    public DecisionType getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(DecisionType finalDecision) {
        this.finalDecision = finalDecision;
    }

    public String getMakerComments() {
        return makerComments;
    }

    public void setMakerComments(String makerComments) {
        this.makerComments = makerComments;
    }

    public String getCheckerComments() {
        return checkerComments;
    }

    public void setCheckerComments(String checkerComments) {
        this.checkerComments = checkerComments;
    }

    public WorkflowCase getWorkflowCase() {
        return workflowCase;
    }

    public void setWorkflowCase(WorkflowCase workflowCase) {
        this.workflowCase = workflowCase;
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

    public boolean isDecisionMatching() {
        return makerDecision != null && checkerDecision != null && 
               makerDecision.equals(checkerDecision);
    }
}