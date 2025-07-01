package com.example.flowable;

import com.example.flowable.service.WorkflowService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlowableApplicationTests {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() {
        assertNotNull(workflowService);
        assertNotNull(repositoryService);
        assertNotNull(runtimeService);
        assertNotNull(taskService);
    }

    @Test
    void testProcessDefinitionsAreDeployed() {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .list();

        assertTrue(processDefinitions.size() >= 2);
        
        boolean simpleApprovalFound = processDefinitions.stream()
                .anyMatch(pd -> "simpleApproval".equals(pd.getKey()));
        boolean vacationRequestFound = processDefinitions.stream()
                .anyMatch(pd -> "vacationRequest".equals(pd.getKey()));

        assertTrue(simpleApprovalFound, "Simple approval process should be deployed");
        assertTrue(vacationRequestFound, "Vacation request process should be deployed");
    }

    @Test
    void testStartSimpleApprovalProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "test@example.com");
        variables.put("requestTitle", "Test Request");

        ProcessInstance processInstance = workflowService.startProcess("simpleApproval", variables);

        assertNotNull(processInstance);
        assertNotNull(processInstance.getId());
        assertFalse(processInstance.isEnded());

        // Check that the first user task is created
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        assertEquals(1, tasks.size());
        assertEquals("submitRequest", tasks.get(0).getTaskDefinitionKey());
    }

    @Test
    void testStartVacationRequestProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "employee@example.com");
        variables.put("startDate", "2024-01-15");
        variables.put("endDate", "2024-01-18");
        variables.put("numberOfDays", 3);

        ProcessInstance processInstance = workflowService.startProcess("vacationRequest", variables);

        assertNotNull(processInstance);
        assertNotNull(processInstance.getId());
        assertFalse(processInstance.isEnded());

        // Check that the first user task is created
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        assertEquals(1, tasks.size());
        assertEquals("fillVacationForm", tasks.get(0).getTaskDefinitionKey());
    }

    @Test
    void testCompleteTaskInSimpleApprovalProcess() {
        // Start process
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "test@example.com");
        variables.put("requestTitle", "Test Request");

        ProcessInstance processInstance = workflowService.startProcess("simpleApproval", variables);

        // Complete submit request task
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        Task submitTask = tasks.get(0);
        workflowService.completeTask(submitTask.getId());

        // Check that manager approval task is created
        tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        assertEquals(1, tasks.size());
        assertEquals("managerApproval", tasks.get(0).getTaskDefinitionKey());

        // Complete manager approval with approval
        Map<String, Object> approvalVariables = new HashMap<>();
        approvalVariables.put("approved", true);

        workflowService.completeTask(tasks.get(0).getId(), approvalVariables);

        // Process should continue to service task and end
        // Check that no more user tasks exist
        tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        assertEquals(0, tasks.size());
    }
}