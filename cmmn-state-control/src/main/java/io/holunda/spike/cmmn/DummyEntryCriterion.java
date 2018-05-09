package io.holunda.spike.cmmn;

import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.extension.cmmn.helper.EntryCriterion;
import org.camunda.bpm.extension.cmmn.helper.RepetitionRule;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_1;
import static io.holunda.spike.cmmn.DummyCase.ACTIVITIES.TASK_2;

@Component("entry")
public class DummyEntryCriterion implements EntryCriterion {

  private final Map<String,Boolean> enable = new HashMap<>();

  @PostConstruct
  public void init() {
    disable(TASK_1);
    disable(TASK_2);
  }

  public void enable(String activityId) {
    enable.put(activityId, true);
  }

  public void disable(String activityId) {
    enable.put(activityId, false);
  }

  public boolean isEnabled(String activityId) {
    return enable.get(activityId);
  }

  @Override
  public boolean test(final DelegateCaseExecution execution, final String activityId) {
    return RepetitionRule.WHEN_NOT_ENABLED_OR_ACTIVE.test(execution, activityId) && isEnabled(activityId);
  }
}
