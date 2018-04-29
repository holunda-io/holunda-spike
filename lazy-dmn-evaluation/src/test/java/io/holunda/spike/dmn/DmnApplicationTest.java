package io.holunda.spike.dmn;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DmnApplicationTest {

  @Autowired
  private DecisionService decisionService;

  public static class FooDataService implements DataService {

    @Override
    public Object apply(final String key) {
      switch (key) {
        case "a":
          return "foo";
        case "b":
          return "a";
        case "c":
          throw new UnsupportedOperationException("not supposed to be called");
        default:
          return null;
      }

    }
  }

  @Test
  public void aIsFoo_matches() {

    DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey("table-definition")
      .variables(new DmnVariableContext(new FooDataService())
        .register("a", ValueType.STRING)
        .register("b", ValueType.STRING)
        .register("c", ValueType.STRING)
        .toVariables())
      .evaluate();

    assertThat(result.getFirstResult().get(null)).isEqualTo(1);
  }
}
