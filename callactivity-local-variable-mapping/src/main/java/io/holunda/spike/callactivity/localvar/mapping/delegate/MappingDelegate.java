package io.holunda.spike.callactivity.localvar.mapping.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateVariableMapping;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.stereotype.Component;

@Component("mapping")
public class MappingDelegate implements DelegateVariableMapping {
  @Override
  public void mapInputVariables(DelegateExecution superExecution, VariableMap subVariables) {
    subVariables.putValue("element", superExecution.getVariable("element"));
  }

  @Override
  public void mapOutputVariables(DelegateExecution superExecution, VariableScope subInstance) {
    superExecution.setVariableLocal("result", subInstance.getVariable("result"));
  }
}
