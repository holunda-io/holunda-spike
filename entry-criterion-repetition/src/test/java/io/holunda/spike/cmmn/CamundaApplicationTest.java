package io.holunda.spike.cmmn;

import io.holunda.spike.cmmn.config.TriggerSentryReEvaluationListener.TouchVerpflichtungIdVariableAdapter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.impl.cmmn.execution.CaseExecutionState;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.cmmn.helper.CmmnStateAssertHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.holunda.spike.cmmn.config.TriggerSentryReEvaluationListener.VARIABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.camunda.bpm.engine.impl.cmmn.execution.CaseExecutionState.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CamundaApplicationTest {

  private static final String KEY = "OneTaskWithEntryCriterionCase";
  private static final String HUMAN_TASK_KEY = "humanTask";

  @Autowired
  private CaseService caseService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private ManagementService managementService;


  @Test
  public void enabled_again_after_completion() {
    final String id = UUID.randomUUID().toString();

    final CaseInstance caseInstance = caseService.createCaseInstanceByKey(KEY, id, Variables.putValue(VARIABLE_NAME, id));
    final TouchVerpflichtungIdVariableAdapter touch = TouchVerpflichtungIdVariableAdapter.of(caseService, caseInstance.getCaseInstanceId());
    final CmmnStateAssertHelper states = new CmmnStateAssertHelper(historyService, HUMAN_TASK_KEY, caseInstance.getCaseInstanceId());

    touchVariableDoesNotChangeStates(touch, states, ENABLED, AVAILABLE);

    final CaseExecution caseExecution = findHumanTaskExecution(caseInstance);
    caseService.manuallyStartCaseExecution(caseExecution.getId());

    touchVariableDoesNotChangeStates(touch, states, ACTIVE, AVAILABLE);

    caseService.completeCaseExecution(caseExecution.getId());

    // first task completed
    assertThat(states.get()).contains(COMPLETED);
    // repetitive task avaliable
    assertThat(states.get()).contains(AVAILABLE);

    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
    assertThat(managementService.createJobQuery().active().list()).isNotEmpty()
    );

    await().untilAsserted(() ->
      assertThat(states.get())
        .as("expected follow up task to be enabled")
        .contains(ENABLED)
    );
  }

  private void touchVariableDoesNotChangeStates(TouchVerpflichtungIdVariableAdapter touch, CmmnStateAssertHelper stateAssertHelper, CaseExecutionState... expectedStates) {
    // after start: one is enabled, another is available (potential repetition)
    assertThat(stateAssertHelper.get()).containsOnlyOnce(expectedStates);

    // when a variable is touched, the states get re-evaluated
    touch.touch();

    // but this should not change anything
    assertThat(stateAssertHelper.get()).containsOnlyOnce(expectedStates);
  }

  private CaseExecution findHumanTaskExecution(final CaseInstance caseInstance) {
    return caseService.createCaseExecutionQuery()
      .enabled()
      .activityId(HUMAN_TASK_KEY)
      .caseInstanceId(caseInstance.getCaseInstanceId())
      .singleResult();
  }
}
