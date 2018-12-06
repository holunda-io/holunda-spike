package io.holunda.spike.cughh.c710;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class C710Application {

  public static void main(String[] args) {
    SpringApplication.run(C710Application.class, args);
  }

  @EventListener
  public void onStart(PostDeployEvent event) {
    event.getProcessEngine().getRepositoryService().createDeployment()
      .addModelInstance("foo.bpmn", Bpmn.createExecutableProcess("process_foo")
        .camundaVersionTag("1")
        .camundaStartableInTasklist(false)
        .startEvent()
        .userTask("task_foo").name("Do foo")
        .endEvent()
        .done())
      .deploy();
  }
}
