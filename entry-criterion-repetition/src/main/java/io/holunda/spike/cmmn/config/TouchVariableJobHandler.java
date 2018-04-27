package io.holunda.spike.cmmn.config;

import io.holunda.spike.cmmn.config.TriggerSentryReEvaluationListener.TouchVerpflichtungIdVariableAdapter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TouchVariableJobHandler implements JobHandler<TouchVariableJobHandler.Configuration> {

  public static final String TYPE = "TouchVariableJobHandler";

  @Autowired
  @Lazy
  private CaseService caseService;

  @Value
  @RequiredArgsConstructor(staticName = "of")
  public static class Configuration implements JobHandlerConfiguration {
    private String caseInstanceId;

    @Override
    public String toCanonicalString() {
      return caseInstanceId;
    }
  }


  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void execute(Configuration configuration, ExecutionEntity execution, CommandContext commandContext, String tenantId) {
    TouchVerpflichtungIdVariableAdapter.of(caseService, configuration.caseInstanceId).touch();
  }

  @Override
  public Configuration newConfiguration(String caseInstanceId) {
    return TouchVariableJobHandler.Configuration.of(caseInstanceId);
  }


  @Override
  public void onDelete(Configuration configuration, JobEntity jobEntity) {

  }
}
