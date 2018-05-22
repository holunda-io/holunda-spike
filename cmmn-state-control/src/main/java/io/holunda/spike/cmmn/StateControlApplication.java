package io.holunda.spike.cmmn;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.CaseVariableListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnActivity;
import org.camunda.bpm.engine.impl.cmmn.transformer.AbstractCmmnTransformListener;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.model.cmmn.impl.instance.CasePlanModel;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.util.SpringBootProcessEnginePlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.util.stream.Stream;

@SpringBootApplication
@EnableProcessApplication
@Slf4j
public class StateControlApplication {

  public static void main(final String[] args) {
    SpringApplication.run(StateControlApplication.class, args);
  }

  @Bean
  public ProcessEnginePlugin onVariableTouch(final ApplicationEventPublisher publisher) {
    return new SpringBootProcessEnginePlugin() {
      @Override
      public void preInit(final SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.getCustomPostCmmnTransformListeners()
          .add(new AbstractCmmnTransformListener() {
                 @Override
                 public void transformCasePlanModel(CasePlanModel casePlanModel, CmmnActivity activity) {
                   Stream.of(
                     CaseVariableListener.CREATE,
                     CaseVariableListener.UPDATE,
                     CaseVariableListener.DELETE).forEach(event -> {
                     activity.addVariableListener(event, (CaseVariableListener) variableInstance -> {
                       publisher.publishEvent(VariableInstanceEvent.of(variableInstance));
                     });
                   });
                 }
               }
          );
      }

      @Override
      public String toString() {
        return "onUpdateVariable";
      }
    };
  }

}
