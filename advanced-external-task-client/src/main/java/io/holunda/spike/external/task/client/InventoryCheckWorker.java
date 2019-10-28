package io.holunda.spike.external.task.client;

import org.camunda.bpm.client.task.impl.ExternalTaskImpl;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.camunda.bpm.engine.variable.Variables.putValue;

@Component
public class InventoryCheckWorker {

  private static final String EXTERNAL_TASK_ID = "EXTERNAL_TASK_ID";
  private static final String MESSAGE = "inventory_check_needed";

  private final ExternalTaskClientWrapper wrapper;
  private final RuntimeService runtimeService;

  public InventoryCheckWorker(ExternalTaskClientWrapper wrapper, RuntimeService runtimeService) {
    this.wrapper = wrapper;
    this.runtimeService = runtimeService;
  }

  /**
   * Setup worker
   */
  @Scheduled(initialDelay = 60_000, fixedDelay = Long.MAX_VALUE)
  public void registerWorker() {
    wrapper
      .getExternalTaskClient()
      .subscribe("topic")
      .lockDuration(60_000)
      .handler(
        (externalTask, externalTaskService) -> {
          String businessKey = externalTask.getBusinessKey();
          // check if already started
          List<ProcessInstance> running = runtimeService
            .createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey)
            .list();
          // be idempotent, start only if not already started
          if (running.isEmpty()) {
            VariableMap variables = putValue(EXTERNAL_TASK_ID, externalTask.getId());
            runtimeService
              .startProcessInstanceByMessage(
                MESSAGE,
                businessKey,
                variables
              );
          }
        }
      )
      .open();
  }


  /**
   * Completion listener.
   *
   * @return an execution listener which completes the external task that started the process.
   */
  public ExecutionListener acknowledgeStarted() {
    return execution -> {

      String externalTaskId = (String) execution.getVariable(EXTERNAL_TASK_ID);
      ExternalTaskImpl task = new ExternalTaskImpl();
      task.setId(externalTaskId);

      this.wrapper.getExternalTaskService().complete(task);
    };
  }
}
