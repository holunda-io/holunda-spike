package io.holunda.spike.example.notification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final ObjectMapper mapper;
  private final ProcessEngineConfigurationImpl config;

  public NotificationService(final ObjectMapper mapper, final ProcessEngineConfigurationImpl config) {
    this.mapper = mapper;
    this.config = config;
  }

  public void sendNotificationInaAJob(String recipient, String subject, String body) {
    final CommandExecutor executor = config.getCommandExecutorTxRequired();
    executor.execute(new SendNotificationCommand(mapper, recipient, subject, body));
  }
}
