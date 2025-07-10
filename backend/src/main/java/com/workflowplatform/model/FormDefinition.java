package com.workflowplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "form_definitions")
public class FormDefinition {
    
    @Id
    private String id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "key", unique = true, nullable = false)
    private String key;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "description")
    private String description;
    
    @Lob
    @Column(name = "form_model", columnDefinition = "TEXT")
    private String formModel;
    
    @Column(name = "deployment_id")
    private String deploymentId;
    
    @Column(name = "resource_name")
    private String resourceName;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "last_updated_time")
    private LocalDateTime lastUpdatedTime;

    public FormDefinition() {}

    public FormDefinition(String id, String name, String key, Integer version) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.version = version;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}