package com.workflowplatform.service;

import com.workflowplatform.dto.FormDefinitionRequest;
import com.workflowplatform.dto.FormDefinitionResponse;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FormDefinitionService {

    @Autowired
    private FormRepositoryService formRepositoryService;

    public FormDefinitionResponse createFormDefinition(FormDefinitionRequest request) {
        try {
            // Deploy the form definition
            FormDeployment deployment = formRepositoryService.createDeployment()
                    .name(request.getName())
                    .addFormBytes(request.getKey() + ".form", request.getFormModel().getBytes())
                    .deploy();

            // Get the deployed form definition
            FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            return convertToFormDefinitionResponse(formDefinition);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create form definition: " + e.getMessage(), e);
        }
    }

    public FormDefinitionResponse updateFormDefinition(String formId, FormDefinitionRequest request) {
        // For updates, we create a new version of the form
        return createFormDefinition(request);
    }

    public FormDefinitionResponse getFormDefinition(String formId) {
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                .formDefinitionId(formId)
                .singleResult();
        
        if (formDefinition == null) {
            throw new RuntimeException("Form definition not found with id: " + formId);
        }
        
        return convertToFormDefinitionResponse(formDefinition);
    }

    public FormDefinitionResponse getFormDefinitionByKey(String formKey) {
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                .formDefinitionKey(formKey)
                .latestVersion()
                .singleResult();
        
        if (formDefinition == null) {
            throw new RuntimeException("Form definition not found with key: " + formKey);
        }
        
        return convertToFormDefinitionResponse(formDefinition);
    }

    public List<FormDefinitionResponse> getAllFormDefinitions() {
        List<FormDefinition> formDefinitions = formRepositoryService.createFormDefinitionQuery()
                .latestVersion()
                .list();
        
        return formDefinitions.stream()
                .map(this::convertToFormDefinitionResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> renderForm(String formId, Map<String, Object> variables) {
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                .formDefinitionId(formId)
                .singleResult();
        
        if (formDefinition == null) {
            throw new RuntimeException("Form definition not found with id: " + formId);
        }

        try {
            // Get form info with variables
            FormInfo formInfo = formRepositoryService.getFormModelWithVariablesById(
                formId, null, variables != null ? variables : new HashMap<>());
            
            Map<String, Object> result = new HashMap<>();
            result.put("formModel", formInfo);
            result.put("formKey", formDefinition.getKey());
            result.put("formId", formDefinition.getId());
            result.put("formName", formDefinition.getName());
            result.put("version", formDefinition.getVersion());
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render form: " + e.getMessage(), e);
        }
    }

    public void deleteFormDefinition(String formId) {
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                .formDefinitionId(formId)
                .singleResult();
        
        if (formDefinition == null) {
            throw new RuntimeException("Form definition not found with id: " + formId);
        }
        
        formRepositoryService.deleteDeployment(formDefinition.getDeploymentId());
    }

    public List<FormDefinitionResponse> getFormDefinitionsByCategory(String category) {
        List<FormDefinition> formDefinitions = formRepositoryService.createFormDefinitionQuery()
                .formDefinitionCategory(category)
                .latestVersion()
                .list();
        
        return formDefinitions.stream()
                .map(this::convertToFormDefinitionResponse)
                .collect(Collectors.toList());
    }

    private FormDefinitionResponse convertToFormDefinitionResponse(FormDefinition formDefinition) {
        FormDefinitionResponse response = new FormDefinitionResponse();
        response.setId(formDefinition.getId());
        response.setName(formDefinition.getName());
        response.setKey(formDefinition.getKey());
        response.setVersion(formDefinition.getVersion());
        response.setDescription(formDefinition.getDescription());
        response.setDeploymentId(formDefinition.getDeploymentId());
        response.setResourceName(formDefinition.getResourceName());
        response.setCategory(formDefinition.getCategory());
        response.setTenantId(formDefinition.getTenantId());
        
        // For created time and last updated time, we'll use the current time as placeholder
        // In a real implementation, you might want to store these in a custom table
        response.setCreatedTime(LocalDateTime.now());
        response.setLastUpdatedTime(LocalDateTime.now());
        
        // Get the form model content
        try {
            FormInfo formInfo = formRepositoryService.getFormModelById(formDefinition.getId());
            response.setFormModel(formInfo != null ? formInfo.toString() : null);
        } catch (Exception e) {
            // If we can't get the form model, just set it to null
            response.setFormModel(null);
        }
        
        return response;
    }
}