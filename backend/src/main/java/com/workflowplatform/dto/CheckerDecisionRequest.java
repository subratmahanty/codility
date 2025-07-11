package com.workflowplatform.dto;

import com.workflowplatform.model.DecisionItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CheckerDecisionRequest {

    @NotNull(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Case ID is required")
    private Long caseId;

    @NotEmpty(message = "Decision items are required")
    private List<ItemDecision> decisions;

    @Size(max = 1000, message = "Comments must not exceed 1000 characters")
    private String comments;

    // Constructors
    public CheckerDecisionRequest() {}

    public CheckerDecisionRequest(String taskId, Long caseId, List<ItemDecision> decisions, String comments) {
        this.taskId = taskId;
        this.caseId = caseId;
        this.decisions = decisions;
        this.comments = comments;
    }

    // Getters and setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public List<ItemDecision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<ItemDecision> decisions) {
        this.decisions = decisions;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public static class ItemDecision {
        @NotNull(message = "Decision item ID is required")
        private Long itemId;

        @NotNull(message = "Decision is required")
        private DecisionItem.DecisionType decision;

        @Size(max = 500, message = "Comments must not exceed 500 characters")
        private String comments;

        private boolean override = false;

        // Constructors
        public ItemDecision() {}

        public ItemDecision(Long itemId, DecisionItem.DecisionType decision, String comments, boolean override) {
            this.itemId = itemId;
            this.decision = decision;
            this.comments = comments;
            this.override = override;
        }

        // Getters and setters
        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public DecisionItem.DecisionType getDecision() {
            return decision;
        }

        public void setDecision(DecisionItem.DecisionType decision) {
            this.decision = decision;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public boolean isOverride() {
            return override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }
    }
}