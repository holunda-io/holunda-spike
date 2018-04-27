package io.holunda.spike.cmmn.config;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class RepetitionRuleTest {

  public static class WhenNotEnabledOrActiveTest {

    private final RepetitionRule predicate = RepetitionRule.WHEN_NOT_ENABLED_OR_ACTIVE;

    private final CaseService caseService = mock(CaseService.class);
    private final DelegateCaseExecution execution = mock(DelegateCaseExecution.class, RETURNS_DEEP_STUBS);


    @Before
    public void setUp() {
      when(execution.getProcessEngineServices().getCaseService()).thenReturn(caseService);

    }
    @Test
    public void false_when_atLeast_one_execution_enabled_or_active() {

    }
  }
}
