package io.holunda.spike.cughh.externaltask;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.IntegerValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.camunda.bpm.engine.variable.Variables.integerValue;
import static org.camunda.bpm.engine.variable.type.ValueType.INTEGER;

@SpringBootApplication
@Slf4j
public class ExternalTaskClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExternalTaskClientApplication.class, args);
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean
  SmartLifecycle workOnTasks(ExternalTaskHandler handler) {
    return new DefaultLifecycle(1000, () -> {
      ExternalTaskClient.create()
        .baseUrl("http://localhost:8080/rest")
        .asyncResponseTimeout(1000)
        .build()
        .subscribe("topic")
        .handler(handler)
        .open();
    });
  }

  @Bean
  public ExternalTaskHandler externalTaskHandler() {



    return (task, service) -> {
      int square = Double.valueOf(pow(Integer.valueOf(task.getBusinessKey()).doubleValue(), 2)).intValue();

      final VariableMap variables = Variables.putValueTyped("square", integerValue(square));

      log.info("completing task: {} {} {}", task.getId(), task.getBusinessKey(), variables);
      service.complete(task, variables);
      sleep(1500L);
    };
  }

  @SneakyThrows
  private final void sleep(long millis) {
    Thread.sleep(millis);
  }



  @RequiredArgsConstructor
  public static class DefaultLifecycle implements SmartLifecycle {
    private final int phase;
    private final Runnable onStart;

    private boolean running = false;

    @Override
    public boolean isAutoStartup() {
      return true;
    }

    @Override
    public void stop(Runnable runnable) {
      runnable.run();
      running = false;
    }

    @Override
    public void start() {
      onStart.run();
      running = true;
    }

    @Override
    public void stop() {
      this.stop(() -> {});
    }

    @Override
    public boolean isRunning() {
      return running;
    }

    @Override
    public int getPhase() {
      return phase;
    }
  }
}
