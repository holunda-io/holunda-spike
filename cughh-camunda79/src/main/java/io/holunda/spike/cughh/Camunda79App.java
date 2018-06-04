package io.holunda.spike.cughh;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class Camunda79App {

  public static void main(String... args) {
    SpringApplication.run(Camunda79App.class, args);
  }
}
