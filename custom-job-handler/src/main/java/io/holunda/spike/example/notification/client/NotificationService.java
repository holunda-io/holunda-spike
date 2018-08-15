package io.holunda.spike.example.notification.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final ObjectMapper mapper;
    private final CommandExecutor executor;

    public NotificationService(final ObjectMapper mapper, final CommandExecutor executor) {
        this.mapper = mapper;
        this.executor = executor;
    }

    public void sendNotificationInaAJob(String recipient, String subject, String body) {
        executor.execute(new SendNotificationCommand(mapper, recipient, subject, body));
    }
}
