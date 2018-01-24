package io.holunda.spike;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Starts main process and determins called subprocess dynamically based on variable.
 */
@SpringBootApplication
@EnableProcessApplication
@Slf4j
public class DynamicCallActivityApplication {

  private static BpmnModelInstance MAIN = Bpmn.createExecutableProcess("main")
    .startEvent()
    .callActivity("dynamic").calledElement("${determineSubProcess.apply(execution)}").camundaCalledElementVersion("deployment")
    .endEvent()
    .done();

  private static BpmnModelInstance SUB1 = Bpmn.createExecutableProcess("sub1")
    .startEvent()
    .serviceTask().camundaDelegateExpression("${sub1Delegate}")
    .endEvent()
    .done();

  private static BpmnModelInstance SUB2 = Bpmn.createExecutableProcess("sub2")
    .startEvent()
    .serviceTask().camundaDelegateExpression("${sub2Delegate}")
    .endEvent()
    .done();


  public static void main(String[] args) {
    SpringApplication.run(DynamicCallActivityApplication.class, args);
  }

  @EventListener
  public void init(PostDeployEvent event) {
    event.getProcessEngine().getRepositoryService().createDeployment()
      .addModelInstance("main.bpmn", MAIN)
      .addModelInstance("sub1.bpmn", SUB1)
      .addModelInstance("sub2.bpmn", SUB2)
      .deploy();

    Stream.of("sub1", "sub2").forEach(p -> event.getProcessEngine()
      .getRuntimeService()
      .startProcessInstanceByKey("main", Variables.putValue("subprocess", p)));
  }

  @Bean
  public Function<DelegateExecution, String> determineSubProcess() {
    return delegateExecution -> (String) delegateExecution.getVariable("subprocess");
  }

  @Bean
  public JavaDelegate sub1Delegate() {
    return execution -> log.info("executing subprocess 1 - {}", execution.getProcessInstanceId());
  }

  @Bean
  public JavaDelegate sub2Delegate() {
    return execution -> log.info("executing subprocess 2 - {}", execution.getProcessInstanceId());
  }
}
