package io.holunda.spike.cughh.externaltask;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableProcessApplication
@Slf4j
@EnableScheduling
public class ExternalTaskServerApplication {

  public static final String PROCESS_KEY = "processWithExternalTask";

  public static void main(String[] args) {
    SpringApplication.run(ExternalTaskServerApplication.class, args);
  }


  private final AtomicInteger count = new AtomicInteger(0);

  @Autowired
  private RuntimeService runtimeService;

  @EventListener
  public void handle(PostDeployEvent event) {
    event.getProcessEngine().getRepositoryService().createDeployment()
      .addModelInstance(
        PROCESS_KEY + "bpmn",
        Bpmn.createExecutableProcess(PROCESS_KEY)
          // start event
          .startEvent()
          // external task
          .serviceTask("external").camundaExternalTask("topic")
          // log task
          .serviceTask("log")
          .camundaDelegateExpression("${logDelegate}")
          .camundaAsyncBefore()
          // end event
          .endEvent()
          .done())
      .deploy();
  }

  @Scheduled(initialDelay = 5000L, fixedDelay = 5000L)
  void startProcess() {
    runtimeService.startProcessInstanceByKey(PROCESS_KEY, String.valueOf(count.incrementAndGet()));
    log.info("starting process: {}", count.get());
  }

  @Bean
  JavaDelegate logDelegate() {
    return execution -> log.info("completing process: {}  {}", execution.getProcessBusinessKey(), execution.getVariables());
  }
}
