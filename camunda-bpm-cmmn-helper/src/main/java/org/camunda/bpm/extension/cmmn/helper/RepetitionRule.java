package org.camunda.bpm.extension.cmmn.helper;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;

import java.util.List;


public enum RepetitionRule implements EntryCriterionPredicate {
  WHEN_NOT_ENABLED_OR_ACTIVE {
    @Override
    public boolean test(final DelegateCaseExecution execution, String activityId) {

      List<CaseExecution> caseExecutions = caseService(execution)
        .createCaseExecutionQuery()
        .caseInstanceId(execution.getCaseInstanceId())
        .activityId(activityId)
        .list();

      return caseExecutions.stream().noneMatch(e -> e.isEnabled() || e.isActive());
    }
  };


  private static CaseService caseService(DelegateCaseExecution execution) {
    return execution.getProcessEngineServices().getCaseService();
  }
}
