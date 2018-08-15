package io.holunda.spike;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@EnableProcessApplication
public class NotificationCamundaJobHandlerApplication {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCamundaJobHandlerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(NotificationCamundaJobHandlerApplication.class, args);
    }

    @Lazy
    @Bean
    public CommandExecutor executorWithTxRequired(ProcessEngineConfigurationImpl processEngineConfiguration) {
        logger.warn("Process Engine Config is null: {}", processEngineConfiguration == null);
        return processEngineConfiguration.getCommandExecutorTxRequired();
    }


}
