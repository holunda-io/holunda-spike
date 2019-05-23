package io.holunda.spike.callactivity.localvar.mapping;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class Application {

  private RuntimeService runtimeService;

  public Application(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @EventListener
  public void onStart(PostDeployEvent event) {
    runtimeService.startProcessInstanceByKey("main_process");
  }
}
