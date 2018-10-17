package io.holunda.spike.forum9302;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.MockExpressionManager;
import org.camunda.bpm.extension.mockito.mock.FluentTaskListenerMock;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.extension.mockito.CamundaMockito.registerTaskListenerMock;
import static org.camunda.bpm.extension.mockito.CamundaMockito.verifyTaskListenerMock;

public class TaskListenerOnBoundaryEventTest {

  private final static String TASK_DEFINITION_KEY = "test-task";
  private final static String END_SUCCESS = "success";
  private final static String END_INTERRUPT = "interrupted";
  private final static String MSG_INTERRUPT = "INTERRUPT";
  private static final String PROCESS_KEY = "test";

  // -- register two listeners --
  private final FluentTaskListenerMock onDelete = registerTaskListenerMock("onDelete");
  private final FluentTaskListenerMock onComplete = registerTaskListenerMock("onComplete");

  private ProcessInstance processInstance;
  private Task task;

  @Rule
  public final ProcessEngineRule camunda = new ProcessEngineRule(new StandaloneInMemProcessEngineConfiguration() {{
    jobExecutorActivate = false;
    expressionManager = new MockExpressionManager();
    databaseSchemaUpdate = DB_SCHEMA_UPDATE_DROP_CREATE;
    isDbMetricsReporterActivate = false;
    historyLevel = HistoryLevel.HISTORY_LEVEL_FULL;
  }}.buildProcessEngine());

  @Before
  public void generateAndDeployProcess() {
    camunda.manageDeployment(camunda.getRepositoryService().createDeployment()
      .addModelInstance(PROCESS_KEY + ".bpmn", Bpmn.createExecutableProcess(PROCESS_KEY)
        .startEvent()
        .userTask(TASK_DEFINITION_KEY)
        .camundaTaskListenerDelegateExpression(TaskListener.EVENTNAME_DELETE, "${onDelete}")
        .camundaTaskListenerDelegateExpression(TaskListener.EVENTNAME_COMPLETE, "${onComplete}")
        .boundaryEvent().message(MSG_INTERRUPT).endEvent(END_INTERRUPT)
        .moveToNode(TASK_DEFINITION_KEY)
        .endEvent(END_SUCCESS)
        .done())
      .deploy());
  }

  @Test
  public void onDelete_when_process_cancelled() {
    startProcess();

    camunda.getRuntimeService().deleteProcessInstance(processInstance.getProcessInstanceId(), "test");

    verifyTaskListenerMock(onComplete).executedNever();
    verifyTaskListenerMock(onDelete).executed();
  }

  @Test
  public void onComplete_when_task_completed() {
    startProcess();

    camunda.getTaskService().complete(task.getId());

    assertThat(processInstanceEndedWith()).isEqualTo(END_SUCCESS);

    verifyTaskListenerMock(onComplete).executed();
    verifyTaskListenerMock(onDelete).executedNever();
  }

  @Test
  public void onDelete_when_boundary() {
    startProcess();

    camunda.getRuntimeService().correlateMessage(MSG_INTERRUPT);

    assertThat(processInstanceEndedWith()).isEqualTo(END_INTERRUPT);

    verifyTaskListenerMock(onComplete).executedNever();
    verifyTaskListenerMock(onDelete).executed();
  }

  private void startProcess() {
    processInstance = camunda.getRuntimeService().startProcessInstanceByKey(PROCESS_KEY);
    task = camunda.getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();

    assertThat(processInstanceEndedWith()).isNull();
    assertThat(task).isNotNull();

    // so far nothing happened
    verifyTaskListenerMock(onComplete).executedNever();
    verifyTaskListenerMock(onDelete).executedNever();
  }

  private String processInstanceEndedWith() {
    return camunda.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstance.getProcessInstanceId())
      .singleResult()
      .getEndActivityId();

  }
}

