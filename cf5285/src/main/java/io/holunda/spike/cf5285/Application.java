package io.holunda.spike.cf5285;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.extension.batch.CustomBatchBuilder;
import org.camunda.bpm.extension.batch.CustomBatchJobHandler;
import org.camunda.bpm.extension.batch.plugin.CustomBatchHandlerPlugin;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableProcessApplication
public class Application {

    public static final String JOB_TYPE = "print-string-batch-handler";
    public static final String FAIL = "fail";
    static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public class PrintStringBatchJobHandler extends CustomBatchJobHandler<String> {
        @Override
        public void execute(final List<String> data, CommandContext commandContext) {
            String element = data.get(0);

            if (FAIL.equals(element)) {
                String msg = "cannot accept value: " + data;
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }

            log.info("Work on data: {}", element);
        }

        @Override
        public String getType() {
            return JOB_TYPE;
        }
    }

    @Bean
    public ProcessEnginePlugin customBatchHandlerPlugin(final PrintStringBatchJobHandler printStringBatchJobHandler) {
        return CustomBatchHandlerPlugin.of(printStringBatchJobHandler);
    }

    @Bean
    public ProcessEnginePlugin noJobRetriesAndHistoryLevel() {
        return new AbstractProcessEnginePlugin() {
            @Override
            public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

                processEngineConfiguration.setDefaultNumberOfRetries(1);
                processEngineConfiguration.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);
            }

            @Override
            public String toString() {
                return "jobRetries=1";
            }
        };
    }

    @Autowired
    private PrintStringBatchJobHandler jobHandler;

    @EventListener
    public void onStart(final PostDeployEvent event) {
        CustomBatchBuilder.of(Arrays.asList("foo", FAIL,"bar"))
                .configuration(event.getProcessEngine().getProcessEngineConfiguration())
                .jobHandler(jobHandler)
                .create();
    }
}
