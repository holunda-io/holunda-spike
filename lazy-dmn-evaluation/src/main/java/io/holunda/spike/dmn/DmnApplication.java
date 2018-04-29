package io.holunda.spike.dmn;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class DmnApplication {

  public static void main(String[] args) {
    SpringApplication.run(DmnApplication.class, args);
  }
}
