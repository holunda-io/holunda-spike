package io.holunda.spike.external;

import io.holunda.spike.Messaging;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SubExternalDelegate extends AbstractBpmnActivityBehavior {

  @Autowired
  private Messaging messaging;


  public static final String EXECUTION_ID = "executionId";

  @Override
  public void execute(final ActivityExecution execution) throws Exception {
    messaging.start(new Messaging.StartWork(execution.getId(), Variables.fromMap(execution.getVariables())));
  }

  @Override
  public void signal(ActivityExecution execution, String signalName, Object signalData) throws Exception {
    log.info("received signal: {}, {}, {}", execution.getId(), signalName, signalData);
    // leave the service task activity:
    leave(execution);
  }

}
