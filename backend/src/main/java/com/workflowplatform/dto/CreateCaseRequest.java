package com.workflowplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateCaseRequest {

    @NotBlank(message = "Case title is required")
    @Size(max = 100, message = "Case title must not exceed 100 characters")
    private String caseTitle;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotEmpty(message = "At least one decision item is required")
    private List<DecisionItemRequest> decisionItems;

    // Constructors
    public CreateCaseRequest() {}

    public CreateCaseRequest(String caseTitle, String description, List<DecisionItemRequest> decisionItems) {
        this.caseTitle = caseTitle;
        this.description = description;
        this.decisionItems = decisionItems;
    }

    // Getters and setters
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

    public List<DecisionItemRequest> getDecisionItems() {
        return decisionItems;
    }

    public void setDecisionItems(List<DecisionItemRequest> decisionItems) {
        this.decisionItems = decisionItems;
    }

    public static class DecisionItemRequest {
        @NotBlank(message = "Item name is required")
        @Size(max = 100, message = "Item name must not exceed 100 characters")
        private String itemName;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        // Constructors
        public DecisionItemRequest() {}

        public DecisionItemRequest(String itemName, String description) {
            this.itemName = itemName;
            this.description = description;
        }

        // Getters and setters
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
    }
}