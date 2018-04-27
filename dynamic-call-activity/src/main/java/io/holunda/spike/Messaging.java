package io.holunda.spike;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * This resembles some remote messaging like JMS ...
 */
@Component
@Slf4j
public class Messaging {

  @Value
  public static class StartWork {
    private String correlationId;
    private VariableMap payload;
  }

  @Value
  public static class CompleteWork {
    private String correlationId;
    private VariableMap payload;
  }


  private final ApplicationEventPublisher publisher;
  private final RuntimeService runtimeService;

  public Messaging(ApplicationEventPublisher publisher, RuntimeService runtimeService) {
    this.publisher = publisher;
    this.runtimeService = runtimeService;
  }


  public void start(final StartWork cmd) {
    log.info("send message: {}", cmd);
    publisher.publishEvent(cmd);
  }

  public void complete(CompleteWork cmd) {
    log.info("receive message: {}", cmd);
    runtimeService.signal(cmd.correlationId, cmd.payload);
  }
}
