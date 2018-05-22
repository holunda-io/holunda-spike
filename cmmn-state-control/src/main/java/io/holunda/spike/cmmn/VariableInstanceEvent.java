package io.holunda.spike.cmmn;

import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.CaseVariableListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.delegate.DelegateCaseVariableInstance;
import org.camunda.bpm.engine.variable.value.TypedValue;

public abstract class VariableInstanceEvent implements DelegateCaseVariableInstance {

  public static VariableInstanceEvent of(DelegateCaseVariableInstance variable) {
    switch (variable.getEventName()) {
      case CaseVariableListener.CREATE:
        return new OnVariableCreate(variable);
      case CaseVariableListener.UPDATE:
        return new OnVariableUpdate(variable);
      case CaseVariableListener.DELETE:
        return new OnVariableDelete(variable);
      default:
        throw new IllegalArgumentException("invalid event");
    }
  }

  protected final DelegateCaseVariableInstance variable;

  protected VariableInstanceEvent(DelegateCaseVariableInstance variable) {
    this.variable = variable;
  }

  public static class OnVariableCreate extends VariableInstanceEvent {

    protected OnVariableCreate(DelegateCaseVariableInstance variable) {
      super(variable);
    }
  }

  public static class OnVariableUpdate extends VariableInstanceEvent {

    protected OnVariableUpdate(DelegateCaseVariableInstance variable) {
      super(variable);
    }
  }

  public static class OnVariableDelete extends VariableInstanceEvent {
    protected OnVariableDelete(DelegateCaseVariableInstance variable) {
      super(variable);
    }
  }


  @Override
  public String getEventName() {
    return variable.getEventName();
  }

  @Override
  public DelegateCaseExecution getSourceExecution() {
    return variable.getSourceExecution();
  }

  @Override
  public ProcessEngineServices getProcessEngineServices() {
    return variable.getProcessEngineServices();
  }

  @Override
  public String getId() {
    return variable.getId();
  }

  @Override
  public String getName() {
    return variable.getName();
  }

  @Override
  public String getTypeName() {
    return variable.getTypeName();
  }

  @Override
  public Object getValue() {
    return variable.getValue();
  }

  @Override
  public TypedValue getTypedValue() {
    return variable.getTypedValue();
  }

  @Override
  public String getProcessInstanceId() {
    return variable.getProcessInstanceId();
  }

  @Override
  public String getExecutionId() {
    return variable.getExecutionId();
  }

  @Override
  public String getCaseInstanceId() {
    return variable.getCaseInstanceId();
  }

  @Override
  public String getCaseExecutionId() {
    return variable.getCaseExecutionId();
  }

  @Override
  public String getTaskId() {
    return variable.getTaskId();
  }

  @Override
  public String getActivityInstanceId() {
    return variable.getActivityInstanceId();
  }

  @Override
  public String getErrorMessage() {
    return variable.getErrorMessage();
  }

  @Override
  public String getTenantId() {
    return variable.getTenantId();
  }
}
