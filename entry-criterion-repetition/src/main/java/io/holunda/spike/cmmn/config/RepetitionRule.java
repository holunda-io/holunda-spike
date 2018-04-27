package io.holunda.spike.cmmn.config;

import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;

import java.util.List;
import java.util.function.BiPredicate;


public enum RepetitionRule implements BiPredicate<DelegateCaseExecution, String> {
  WHEN_NOT_ENABLED_OR_ACTIVE{
    @Override
    public boolean test(DelegateCaseExecution execution, String activityId) {


      List<CaseExecution> caseExecutions = execution.getProcessEngineServices().getCaseService()
        .createCaseExecutionQuery()
        .caseInstanceId(execution.getCaseInstanceId())
        .activityId(activityId)
        .list();

      return caseExecutions.stream().noneMatch(e -> e.isEnabled() || e.isActive());
    }
  };


}
