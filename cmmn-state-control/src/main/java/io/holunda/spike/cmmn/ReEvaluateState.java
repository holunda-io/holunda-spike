package io.holunda.spike.cmmn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateCaseVariableInstance;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_1;
import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_2;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ReEvaluateState {

  private final DummyEntryCriterion criterion;
  private final CaseService caseService;

  @EventListener(condition = "#variable.eventName.equals('create')")
  public void onBusinessKeyUpdate(VariableInstanceEvent.OnVariableCreate variable) {
    log.info("create on {} - {}", variable.getEventName(), variable.getName());
    //notify(variable);
  }

  @EventListener(condition = "#variable.eventName.equals('update')")
  public void onBusinessKeyUpdate(VariableInstanceEvent.OnVariableUpdate variable) {
    log.info("update on {} - {}", variable.getEventName(), variable.getName());
    notify(variable);
  }

  @EventListener(condition = "#variable.eventName.equals('delete')")
  public void onBusinessKeyUpdate(VariableInstanceEvent.OnVariableDelete variable) {
    log.info("delete on {} - {}", variable.getEventName(), variable.getName());
  }

  public void notify(final DelegateCaseVariableInstance variable) {
    String caseInstanceId = variable.getCaseInstanceId();

    Stream.of(TASK_1, TASK_2).forEach(t -> {
      refresh(caseInstanceId, t);
    });


  }

  public void refresh(String caseInstanceId, String activityId) {
    List<CaseExecution> list = caseService.createCaseExecutionQuery().caseInstanceId(caseInstanceId).activityId(activityId).list();
    list.stream().filter(c -> c.isEnabled() || c.isDisabled()).findFirst().ifPresent(c -> {

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
