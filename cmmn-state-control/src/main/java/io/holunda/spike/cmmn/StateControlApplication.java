package io.holunda.spike.cmmn;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class StateControlApplication {

  public static void main(String[] args) {
    SpringApplication.run(StateControlApplication.class, args);
  }
}
