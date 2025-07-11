package com.workflowplatform.repository;

import com.workflowplatform.model.DecisionItem;
import com.workflowplatform.model.WorkflowCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionItemRepository extends JpaRepository<DecisionItem, Long> {
    
    List<DecisionItem> findByWorkflowCase(WorkflowCase workflowCase);
    
    List<DecisionItem> findByWorkflowCaseId(Long workflowCaseId);
    
    @Query("SELECT di FROM DecisionItem di WHERE di.workflowCase.id = :caseId AND di.makerDecision IS NULL")
    List<DecisionItem> findPendingMakerDecisions(@Param("caseId") Long caseId);
    
    @Query("SELECT di FROM DecisionItem di WHERE di.workflowCase.id = :caseId AND di.makerDecision IS NOT NULL AND di.checkerDecision IS NULL")
    List<DecisionItem> findPendingCheckerDecisions(@Param("caseId") Long caseId);
    
    @Query("SELECT di FROM DecisionItem di WHERE di.workflowCase.id = :caseId AND di.makerDecision != di.checkerDecision")
    List<DecisionItem> findConflictingDecisions(@Param("caseId") Long caseId);
}