package io.holunda.spike;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class TaskViewApplication {
  public static void main(String[] args) {
    SpringApplication.run(TaskViewApplication.class, args);
  }
}
