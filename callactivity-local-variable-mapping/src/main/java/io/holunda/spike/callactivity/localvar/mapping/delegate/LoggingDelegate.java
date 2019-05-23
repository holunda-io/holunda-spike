package io.holunda.spike.callactivity.localvar.mapping.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component("logging")
public class LoggingDelegate implements ExecutionListener {
  @Override
  public void notify(DelegateExecution execution) {
    System.out.println("\nLOCAL VARIABLES:");
    execution.getVariablesLocal().forEach((key, value) -> System.out.println(key + ": " + value));

    System.out.println("\nVARIABLES:");
    execution.getVariables().forEach((key, value) -> System.out.println(key + ": " + value));
  }
}
