package io.holunda.spike.cughh.c710.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

@Component
public class CreateOrderIdDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) {
    Assert.isNull(execution.getProcessBusinessKey(), "businessKey already set");

    execution.setProcessBusinessKey(UUID.randomUUID().toString());
  }
}
