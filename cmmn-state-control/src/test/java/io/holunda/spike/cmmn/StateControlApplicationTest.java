package io.holunda.spike.cmmn;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.extension.cmmn.helper.CmmnStateAssertHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_1;
import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.impl.cmmn.execution.CaseExecutionState.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateControlApplicationTest {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private DummyCase dummyCase;

  @Autowired
  private CaseService caseService;

  @Autowired
  private DummyEntryCriterion criterion;

  private CaseInstance caseInstance;
  private CmmnStateAssertHelper task1;
  private CmmnStateAssertHelper task2;

  private void startCase() {
    caseInstance = dummyCase.start("12345");
    task1 = new CmmnStateAssertHelper(historyService, TASK_1, caseInstance.getCaseInstanceId());
    task2 = new CmmnStateAssertHelper(historyService, TASK_2, caseInstance.getCaseInstanceId());
  }

  @Test
  public void starts() {
    // ok
  }

  @Test
  public void bothTasksAvailableAfterStart() {
    startCase();

    //caseService.setVariable(caseInstance.getCaseInstanceId(), "foo", "bar");
    //caseService.setVariable(caseInstance.getCaseInstanceId(), "foo", "bar1  ");

    assertThat(task1.get()).containsOnly(AVAILABLE);
    assertThat(task2.get()).containsOnly(AVAILABLE);
  }

  @Test
  public void bothTasksEnabledAfterStart() {
    criterion.enable(TASK_1);
    criterion.enable(TASK_2);

    startCase();

    assertThat(task1.get()).containsOnlyOnce(ENABLED);
    assertThat(task2.get()).containsOnlyOnce(ENABLED);

    criterion.disable(TASK_2);

    caseService.setVariable(caseInstance.getCaseInstanceId(), DummyCase.VARIABLES.BUSINESS_KEY, "12345");

    assertThat(task2.get()).containsOnlyOnce(DISABLED);
  }

}
