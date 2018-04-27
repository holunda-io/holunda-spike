package io.holunda.spike;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static org.camunda.bpm.engine.delegate.ExecutionListener.EVENTNAME_END;

/**
 * Starts main process and determines called subprocess dynamically based on variable.
 */
@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class DynamicCallActivityApplication {

  public static void main(String[] args) {
    SpringApplication.run(DynamicCallActivityApplication.class, args);
  }

  private static BpmnModelInstance MAIN = Bpmn.createExecutableProcess("main")
    .startEvent()
    .callActivity("dynamic").calledElement("${determineSubProcess.apply(execution)}").camundaCalledElementVersion("deployment")
    .endEvent()
    .done();

  private static BpmnModelInstance SUB_GLOBAL = Bpmn.createExecutableProcess("subGlobal")
    .startEvent()
    .serviceTask().camundaDelegateExpression("${subGlobalDelegate}")
    .endEvent().camundaExecutionListenerDelegateExpression(EVENTNAME_END, "${logMainEnd}")
    .done();

  private static BpmnModelInstance SUB_EXTERNAL = Bpmn.createExecutableProcess("subExternal")
    .startEvent()
    .serviceTask().camundaDelegateExpression("${subExternalDelegate}")
    .endEvent()
    .done();


  private boolean ready = false;

  @Autowired
  private RuntimeService runtimeService;

  @EventListener
  public void init(PostDeployEvent event) {
    event.getProcessEngine().getRepositoryService().createDeployment()
      .addModelInstance("main.bpmn", MAIN)
      .addModelInstance("subGlobal.bpmn", SUB_GLOBAL)
      .addModelInstance("subExternal.bpmn", SUB_EXTERNAL)
      .deploy();

   // ready = true;
    startMainProcess("subExternal");

  }

   @Scheduled(fixedDelay = 2000L)
  public void start() {
    if (!ready) {
      return;
    }

    String subProcess = Arrays.asList("subGlobal", "subExternal").get(new Random().nextInt(2));

    ProcessInstance processInstance = startMainProcess(subProcess);
    log.info("started subprocess[{}]: {}", subProcess, processInstance.getId());
  }

  private ProcessInstance startMainProcess(String subProcess) {
    return runtimeService
      .startProcessInstanceByKey("main", Variables.putValue("subProcess", subProcess));
  }

  @Bean
  public Function<DelegateExecution, String> determineSubProcess() {
    return delegateExecution -> (String) delegateExecution.getVariable("subProcess");
  }

  @Bean
  public ExecutionListener logMainEnd() {
    return execution -> log.info("main ended with: {}", execution.getVariable("result"));
  }


}
