package io.holunda.spike.example.notification.handler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.holunda.spike.camunda.job.JacksonJobHandlerConfiguration;

public class NotificationJobHandlerConfiguration extends JacksonJobHandlerConfiguration {

  private String recipient;
  private String subject;
  private String body;

  /**
   * Constructs the bean.
   *
   * @param mapper    object mapper.
   * @param recipient recipient address.
   * @param subject   subject of the notification.
   * @param body      body of the notification.
   */
  public NotificationJobHandlerConfiguration(final ObjectMapper mapper, final String recipient, final String subject, final String body) {
    super(mapper);
    this.body = body;
    this.subject = subject;
    this.recipient = recipient;
  }

  /**
   * Used by Jackson during creation of the payload, please use direct constructor instead.
   */
  @JsonCreator
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  static NotificationJobHandlerConfiguration fromJson(
    @JsonProperty("recipient") final String recipient,
    @JsonProperty("subject") final String subject,
    @JsonProperty("body") final String body) {
    return new NotificationJobHandlerConfiguration(new ObjectMapper(), recipient, subject, body);
  }

  @JsonProperty("recipient")
  String getRecipient() {
    return recipient;
  }

  public void setRecipient(final String recipient) {
    this.recipient = recipient;
  }

  @JsonProperty("subject")
  String getSubject() {
    return subject;
  }

  public void setSubject(final String subject) {
    this.subject = subject;
  }

  @JsonProperty("body")
  String getBody() {
    return body;
  }

  public void setBody(final String body) {
    this.body = body;
  }
}
