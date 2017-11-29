package io.holunda.spike.cf5285;

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.incident.DefaultIncidentHandler;
import org.camunda.bpm.engine.impl.incident.IncidentContext;
import org.camunda.bpm.engine.impl.incident.IncidentHandler;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.runtime.Incident;
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

/**
 * For sake of simplicity, we use a Spring Boot Application to run the process-engine. No web-context involved!
 */
@SpringBootApplication
@EnableProcessApplication
public class Application {

  /**
   * run the main application.
   * @param args cli args
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  public static final String JOB_TYPE = "will-fail";
  public static final String FAIL = "fail";

    static Logger log = LoggerFactory.getLogger(Application.class);

  /**
   * This is our custom batch handler. It takes a string value and fails if its equal to FAIL.
   */
  @Component
    public class WillFailBatchJobHandler extends CustomBatchJobHandler<String> {
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

  /**
   * This registers our custom batch via plugin
   *
   * @param batchJobHandler our handler
   * @return plugin that is detected by spring boot
   */
    @Bean
    public ProcessEnginePlugin customBatchHandlerPlugin(final WillFailBatchJobHandler batchJobHandler) {
        return CustomBatchHandlerPlugin.of(batchJobHandler);
    }

  /**
   * Overwrite the {@link DefaultIncidentHandler} so it can decide if it wants to handle the incident or
   * not based on the jobType.
   *
   * @return our incident handler
   */
  @Bean
    public IncidentHandler customIncidentHandler() {
        return new DefaultIncidentHandler(Incident.FAILED_JOB_HANDLER_TYPE) {

            @Override
            public void handleIncident(IncidentContext context, String message) {
                final ManagementService managementService = Context.getProcessEngineConfiguration().getProcessEngine().getManagementService();

                JobDefinition jobDefinition = managementService.createJobDefinitionQuery().jobDefinitionId(context.getJobDefinitionId()).singleResult();

                if (JOB_TYPE.equals(jobDefinition.getJobType())) {
                    log.warn("ignoring incident: " + context.getConfiguration());
                } else {
                    // create incident
                    super.handleIncident(context, message);
                }
            }
        };
    }

  /**
   * Some more engine configuration stuff like history
   *
   * @param incidentHandler our incident handler
   * @return plugin detected and applied by spring boot
   */
    @Bean
    public ProcessEnginePlugin configureEngineViaPlugin(final IncidentHandler incidentHandler) {
        return new AbstractProcessEnginePlugin() {
            @Override
            public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

                processEngineConfiguration.setDefaultNumberOfRetries(1);
                processEngineConfiguration.setCustomIncidentHandlers(Arrays.asList(incidentHandler));
            }

            @Override
            public String toString() {
                return "jobRetries=1,history=full,incidentHandler=" + incidentHandler.getIncidentHandlerType();
            }
        };
    }


    @Autowired
    private WillFailBatchJobHandler jobHandler;

  /**
   * After start of the processapplication, create a batch.
   * @param event contains process engine
   */
  @EventListener
    public void onStart(final PostDeployEvent event) {
        CustomBatchBuilder.of(Arrays.asList("foo", FAIL, "bar"))
                .configuration(event.getProcessEngine().getProcessEngineConfiguration())
                .jobHandler(jobHandler)
                .create();
    }
}
