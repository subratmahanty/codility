package com.example.flowable.dto;

public class ProcessInstanceDto {
    private String id;
    private String processDefinitionId;
    private String businessKey;
    private boolean ended;

    public ProcessInstanceDto() {}

    public ProcessInstanceDto(String id, String processDefinitionId, String businessKey, boolean ended) {
        this.id = id;
        this.processDefinitionId = processDefinitionId;
        this.businessKey = businessKey;
        this.ended = ended;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }
}