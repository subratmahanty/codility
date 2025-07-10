package com.workflowplatform.dto;

import java.util.Map;

public class CompleteTaskRequest {
    
    private Map<String, Object> variables;
    private String comment;

    public CompleteTaskRequest() {}

    public CompleteTaskRequest(Map<String, Object> variables, String comment) {
        this.variables = variables;
        this.comment = comment;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}