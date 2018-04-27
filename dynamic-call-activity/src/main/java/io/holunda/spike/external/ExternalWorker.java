package io.holunda.spike.external;

import io.holunda.spike.Messaging;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class ExternalWorker implements BeanNameAware {

  private String workerId;
  private final Messaging messaging;

  public ExternalWorker(Messaging messaging) {
    this.messaging = messaging;
  }


  @EventListener
  public void accept(final Messaging.StartWork cmd) {
    log.info("{} does magic: {}", workerId, cmd.getCorrelationId());

    messaging.complete(new Messaging.CompleteWork(cmd.getCorrelationId(), Variables.putValue("result", "external")));
  }

  @Override
  public void setBeanName(final String name) {
    this.workerId = name;
  }
}
