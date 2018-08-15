package io.holunda.spike.example.notification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.holunda.spike.example.notification.handler.NotificationJobHandlerConfiguration;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;

public class SendNotificationCommand implements Command<String> {

  public static final String TYPE = "SendNotification";
  private final NotificationJobHandlerConfiguration payload;

  public SendNotificationCommand(final ObjectMapper mapper, final String recipient, final String subject, final String body) {
    this.payload = new NotificationJobHandlerConfiguration(mapper, recipient, subject, body);
  }

  @Override
  public String execute(final CommandContext commandContext) {

    final MessageEntity entity = new MessageEntity();

    entity.init(commandContext);
    entity.setJobHandlerType(TYPE);
    entity.setJobHandlerConfiguration(payload);

    commandContext.getJobManager().send(entity);

    return entity.getId();
  }
}
