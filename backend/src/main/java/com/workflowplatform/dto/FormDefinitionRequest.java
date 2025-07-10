package com.workflowplatform.dto;

public class FormDefinitionRequest {
    
    private String name;
    private String key;
    private String description;
    private String formModel;
    private String category;
    private String tenantId;

    public FormDefinitionRequest() {}

    public FormDefinitionRequest(String name, String key, String formModel) {
        this.name = name;
        this.key = key;
        this.formModel = formModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormModel() {
        return formModel;
    }

    public void setFormModel(String formModel) {
        this.formModel = formModel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}