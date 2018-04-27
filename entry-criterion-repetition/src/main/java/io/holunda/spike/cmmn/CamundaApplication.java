package io.holunda.spike.cmmn;

import io.holunda.spike.cmmn.config.RepetitionRule;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.BiPredicate;

@SpringBootApplication
@EnableProcessApplication
public class CamundaApplication {

  public static void main(String... args) {
    SpringApplication.run(CamundaApplication.class, args);
  }

  //@Bean
  BiPredicate<DelegateCaseExecution, String> whenNotEnabledOrActive() {
    return (c,a) -> true;
  }
}
