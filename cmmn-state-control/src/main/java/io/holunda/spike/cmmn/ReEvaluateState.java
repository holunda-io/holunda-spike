package io.holunda.spike.cmmn;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.CaseVariableListener;
import org.camunda.bpm.engine.delegate.DelegateCaseVariableInstance;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_1;
import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_2;

@Component
@RequiredArgsConstructor
public class ReEvaluateState implements CaseVariableListener {

  private final DummyEntryCriterion criterion;
  private final CaseService caseService;

  @Override
  public void notify(final DelegateCaseVariableInstance variable) throws Exception {
    String caseInstanceId = variable.getCaseInstanceId();

    Stream.of(TASK_1, TASK_2).forEach(t -> {
      refresh(caseInstanceId, t);
    });


  }

  public void refresh(String caseInstanceId, String activityId ) {
    List<CaseExecution> list = caseService.createCaseExecutionQuery().caseInstanceId(caseInstanceId).activityId(activityId).list();



    Optional<CaseExecution> caseExecution =list.stream()
      .filter(c -> c.isEnabled() || c.isDisabled()).findFirst();



    caseExecution.ifPresent(c -> {

      boolean isDisabled = c.isDisabled();
      boolean isEnabled = c.isEnabled();

      if (criterion.isEnabled(activityId) && c.isDisabled()) {
        caseService.reenableCaseExecution(c.getId());
      } else if (!criterion.isEnabled(activityId) && c.isEnabled()) {
        caseService.disableCaseExecution(c.getId());
      }
    });

  }

}
