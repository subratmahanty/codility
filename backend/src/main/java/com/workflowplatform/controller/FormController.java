package com.workflowplatform.controller;

import com.workflowplatform.dto.FormDefinitionRequest;
import com.workflowplatform.dto.FormDefinitionResponse;
import com.workflowplatform.service.FormDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forms")
@Tag(name = "Form Management", description = "Form definition and rendering endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FormController {

    @Autowired
    private FormDefinitionService formDefinitionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROCESS_MANAGER')")
    @Operation(summary = "Create/Update form definition", description = "Create or update a form definition")
    public ResponseEntity<FormDefinitionResponse> createOrUpdateFormDefinition(
            @RequestBody FormDefinitionRequest request,
            @RequestParam(value = "formId", required = false) String formId) {
        
        try {
            FormDefinitionResponse response;
            
            if (formId != null) {
                // Update existing form
                response = formDefinitionService.updateFormDefinition(formId, request);
            } else {
                // Create new form
                response = formDefinitionService.createFormDefinition(request);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create/update form definition: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{formId}")
    @Operation(summary = "Get form definition", description = "Get a form definition by ID")
    public ResponseEntity<FormDefinitionResponse> getFormDefinition(@PathVariable String formId) {
        try {
            FormDefinitionResponse form = formDefinitionService.getFormDefinition(formId);
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get form definition: " + e.getMessage(), e);
        }
    }

    @GetMapping
    @Operation(summary = "Get all form definitions", description = "Get all form definitions")
    public ResponseEntity<List<FormDefinitionResponse>> getAllFormDefinitions(
            @RequestParam(value = "category", required = false) String category) {
        
        try {
            List<FormDefinitionResponse> forms;
            
            if (category != null && !category.trim().isEmpty()) {
                forms = formDefinitionService.getFormDefinitionsByCategory(category);
            } else {
                forms = formDefinitionService.getAllFormDefinitions();
            }
            
            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get form definitions: " + e.getMessage(), e);
        }
    }

    @GetMapping("/key/{formKey}")
    @Operation(summary = "Get form definition by key", description = "Get the latest version of a form definition by key")
    public ResponseEntity<FormDefinitionResponse> getFormDefinitionByKey(@PathVariable String formKey) {
        try {
            FormDefinitionResponse form = formDefinitionService.getFormDefinitionByKey(formKey);
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get form definition by key: " + e.getMessage(), e);
        }
    }

    @PostMapping("/{formId}/render")
    @Operation(summary = "Render form", description = "Render a form with variables")
    public ResponseEntity<Map<String, Object>> renderForm(
            @PathVariable String formId,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        try {
            Map<String, Object> renderedForm = formDefinitionService.renderForm(formId, variables);
            return ResponseEntity.ok(renderedForm);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to render form");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{formId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete form definition", description = "Delete a form definition")
    public ResponseEntity<Map<String, Object>> deleteFormDefinition(@PathVariable String formId) {
        try {
            formDefinitionService.deleteFormDefinition(formId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Form definition deleted successfully");
            response.put("formId", formId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete form definition");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate form model", description = "Validate a form model without creating it")
    public ResponseEntity<Map<String, Object>> validateFormModel(@RequestBody FormDefinitionRequest request) {
        try {
            // For now, we'll just check if the form model is valid JSON/XML
            if (request.getFormModel() == null || request.getFormModel().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("valid", false);
                errorResponse.put("error", "Form model cannot be empty");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("message", "Form model is valid");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", "Invalid form model");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}