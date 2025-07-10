package com.workflowplatform.service;

import com.workflowplatform.dto.TaskResponse;
import com.workflowplatform.dto.CompleteTaskRequest;
import org.flowable.engine.TaskService;
import org.flowable.engine.FormService;
import org.flowable.task.api.Task;
import org.flowable.form.api.FormInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkflowTaskService {

    @Autowired
    private TaskService flowableTaskService;

    @Autowired
    private FormService formService;

    public List<TaskResponse> getUserTasks(String userId) {
        List<Task> tasks = flowableTaskService.createTaskQuery()
                .taskAssignee(userId)
                .list();

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getCurrentUserTasks() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserTasks(currentUser);
    }

    public TaskResponse getTaskById(String taskId) {
        Task task = flowableTaskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        
        return convertToTaskResponse(task);
    }

    public void completeTask(String taskId, CompleteTaskRequest request) {
        Task task = flowableTaskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        // Add comment if provided
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            flowableTaskService.addComment(taskId, task.getProcessInstanceId(), request.getComment());
        }

        // Complete task with variables
        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            flowableTaskService.complete(taskId, request.getVariables());
        } else {
            flowableTaskService.complete(taskId);
        }
    }

    public FormInfo getTaskForm(String taskId) {
        Task task = flowableTaskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        
        if (task == null) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        String formKey = task.getFormKey();
        if (formKey == null) {
            throw new RuntimeException("No form associated with task: " + taskId);
        }

        return formService.getTaskFormModel(taskId);
    }

    public void claimTask(String taskId, String userId) {
        flowableTaskService.claim(taskId, userId);
    }

    public void unclaimTask(String taskId) {
        flowableTaskService.unclaim(taskId);
    }

    public void delegateTask(String taskId, String userId) {
        flowableTaskService.delegateTask(taskId, userId);
    }

    public void setTaskAssignee(String taskId, String userId) {
        flowableTaskService.setAssignee(taskId, userId);
    }

    public void setTaskPriority(String taskId, int priority) {
        flowableTaskService.setPriority(taskId, priority);
    }

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setAssignee(task.getAssignee());
        response.setOwner(task.getOwner());
        response.setProcessInstanceId(task.getProcessInstanceId());
        response.setProcessDefinitionId(task.getProcessDefinitionId());
        response.setTaskDefinitionKey(task.getTaskDefinitionKey());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate() != null ? task.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        response.setCreateTime(task.getCreateTime() != null ? task.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        response.setClaimTime(task.getClaimTime() != null ? task.getClaimTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        response.setFormKey(task.getFormKey());
        response.setCategory(task.getCategory());
        response.setTenantId(task.getTenantId());
        response.setSuspended(task.isSuspended());
        
        // Get task variables
        Map<String, Object> variables = flowableTaskService.getVariables(task.getId());
        response.setVariables(variables);
        
        return response;
    }
}