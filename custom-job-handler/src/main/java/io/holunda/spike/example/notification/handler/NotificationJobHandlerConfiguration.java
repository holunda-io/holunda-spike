package io.holunda.spike.example.notification.handler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.holunda.spike.camunda.job.JacksonJobHandlerConfiguration;

public class NotificationJobHandlerConfiguration extends JacksonJobHandlerConfiguration {

    private String recipient;
    private String subject;
    private String body;

    public NotificationJobHandlerConfiguration(final ObjectMapper mapper, final String recipient, final String subject, final String body) {
        super(mapper);
    }

    /**
     * Used by Jackson during creation of the payload, please use direct constructor instead.
     * @param recipient recipient address.
     * @param subject subject of the notification.
     * @param body body of the notification.
     */
    @JsonCreator
    public static NotificationJobHandlerConfiguration fromJson(final String recipient, final String subject, final String body) {
        return new NotificationJobHandlerConfiguration(new ObjectMapper(), recipient, subject, body);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
