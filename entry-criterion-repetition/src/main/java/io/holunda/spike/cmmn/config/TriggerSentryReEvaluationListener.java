package io.holunda.spike.cmmn.config;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.springframework.stereotype.Component;

import static io.holunda.spike.cmmn.config.TouchVariableJobHandler.TYPE;

/**
 * Repetition of activities does not automatically re-evaluate the entryCriterion, by touching a variable
 * this is triggered on complete.
 */
@Component
@Slf4j
public class TriggerSentryReEvaluationListener implements CaseExecutionListener {

  public static final String VARIABLE_NAME = "foo";

  public interface TouchVerpflichtungIdVariableAdapter {

    static TouchVerpflichtungIdVariableAdapter of(final CaseService caseService, final String caseInstanceId) {
      return () -> caseService.setVariable(caseInstanceId, VARIABLE_NAME, caseService.getVariable(caseInstanceId, VARIABLE_NAME));
    }

    static TouchVerpflichtungIdVariableAdapter of(final DelegateCaseExecution execution) {
      return () -> execution.setVariable(VARIABLE_NAME, execution.getVariable(VARIABLE_NAME));
    }


    void touch();
  }

  @Override
  public void notify(final DelegateCaseExecution execution) {
    MessageEntity job = new MessageEntity();
    job.setJobHandlerType(TYPE);
    job.setJobHandlerConfiguration(TouchVariableJobHandler.Configuration.of(execution.getCaseInstanceId()));


    Context.getCommandContext().getJobManager().insertJob(job);


  }
}
