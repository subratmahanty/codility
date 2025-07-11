package com.workflowplatform.repository;

import com.workflowplatform.model.WorkflowCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowCaseRepository extends JpaRepository<WorkflowCase, Long> {
    
    List<WorkflowCase> findByCreatedBy(String createdBy);
    
    List<WorkflowCase> findByStatus(WorkflowCase.CaseStatus status);
    
    List<WorkflowCase> findByCurrentLevel(WorkflowCase.WorkflowLevel currentLevel);
    
    Optional<WorkflowCase> findByProcessInstanceId(String processInstanceId);
    
    @Query("SELECT wc FROM WorkflowCase wc WHERE wc.status = :status AND wc.currentLevel = :level")
    List<WorkflowCase> findByStatusAndCurrentLevel(@Param("status") WorkflowCase.CaseStatus status, 
                                                   @Param("level") WorkflowCase.WorkflowLevel level);
    
    @Query("SELECT wc FROM WorkflowCase wc WHERE wc.caseTitle LIKE %:title%")
    List<WorkflowCase> findByCaseTitleContaining(@Param("title") String title);
}