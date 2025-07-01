package com.example.flowable.dto;

import java.util.Map;

public class StartProcessRequest {
    private String processKey;
    private String businessKey;
    private Map<String, Object> variables;

    public StartProcessRequest() {}

    public StartProcessRequest(String processKey, String businessKey, Map<String, Object> variables) {
        this.processKey = processKey;
        this.businessKey = businessKey;
        this.variables = variables;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}