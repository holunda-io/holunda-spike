package io.holunda.spike.cf5285;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cmd.DecrementJobRetriesCmd;
import org.camunda.bpm.engine.impl.cmd.JobRetryCmd;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.FailedJobCommandFactory;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * For sake of simplicity, we use a Spring Boot Application to run the process-engine. No web-context involved!
 */
@SpringBootApplication
@EnableProcessApplication
public class Application {

  /**
   * run the main application.
   *
   * @param args cli args
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  public static final String JOB_TYPE = "will-fail";
  public static final String FAIL = "fail";

  static Logger log = LoggerFactory.getLogger(Application.class);

  public static class IgnoreFailedJobCmd extends JobRetryCmd {

    private final Set<String> ignoredJobTypes;

    public IgnoreFailedJobCmd(Set<String> ignoredJobTypes, String jobId, Throwable exception) {
      super(jobId, exception);
      this.ignoredJobTypes = ignoredJobTypes;
    }

    @Override
    public Object execute(CommandContext commandContext) {
      JobEntity job = getJob();

      if (ignoredJobTypes.contains(job.getJobDefinition().getJobType())) {
        job.delete(true);
        return null;
      }

      return new DecrementJobRetriesCmd(jobId, exception).execute(commandContext);
    }
  }

  public static class IgnoringFailedJobCommandFactory implements FailedJobCommandFactory {

    private final Set<String> ignoredJobTypes;

    public IgnoringFailedJobCommandFactory(Set<String> ignoredJobTypes) {
      this.ignoredJobTypes = ignoredJobTypes;
    }

    @Override
    public Command<Object> getCommand(String jobId, Throwable exception) {
      return new IgnoreFailedJobCmd(ignoredJobTypes, jobId, exception);
    }
  }


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
   * Some more engine configuration stuff like history
   *
   * @return plugin detected and applied by spring boot
   */
  @Bean
  public ProcessEnginePlugin configureEngineViaPlugin() {

    final Set<String> ignoredJobs = Collections.singleton(JOB_TYPE);

    return new AbstractProcessEnginePlugin() {
      @Override
      public void preInit(ProcessEngineConfigurationImpl configuration) {

        configuration.setDefaultNumberOfRetries(1);
        configuration.setFailedJobCommandFactory(new IgnoringFailedJobCommandFactory(ignoredJobs));

      }

      @Override
      public String toString() {
        return "jobRetries=1,history=full,ignoredJobs=" + ignoredJobs;
      }
    };
  }


  @Autowired
  private WillFailBatchJobHandler jobHandler;

  /**
   * After start of the processapplication, create a batch.
   *
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
