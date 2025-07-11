package com.workflowplatform.repository;

import com.workflowplatform.model.WorkflowCase;
import com.workflowplatform.model.WorkflowDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowDecisionRepository extends JpaRepository<WorkflowDecision, Long> {
    
    List<WorkflowDecision> findByWorkflowCase(WorkflowCase workflowCase);
    
    List<WorkflowDecision> findByWorkflowCaseId(Long workflowCaseId);
    
    List<WorkflowDecision> findByDecisionBy(String decisionBy);
    
    @Query("SELECT wd FROM WorkflowDecision wd WHERE wd.workflowCase.id = :caseId AND wd.level = :level")
    List<WorkflowDecision> findByWorkflowCaseIdAndLevel(@Param("caseId") Long caseId, 
                                                        @Param("level") WorkflowCase.WorkflowLevel level);
    
    @Query("SELECT wd FROM WorkflowDecision wd WHERE wd.workflowCase.id = :caseId AND wd.level = :level AND wd.role = :role")
    List<WorkflowDecision> findByWorkflowCaseIdAndLevelAndRole(@Param("caseId") Long caseId, 
                                                               @Param("level") WorkflowCase.WorkflowLevel level,
                                                               @Param("role") WorkflowDecision.DecisionRole role);
}