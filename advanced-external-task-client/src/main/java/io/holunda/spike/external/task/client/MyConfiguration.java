package io.holunda.spike.external.task.client;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration.
 */
@Configuration
public class MyConfiguration {

  /**
   * Creates the task client wrapper with configured client.
   * @return task client wrapper.
   */
  @Bean
  public ExternalTaskClientWrapper externalTaskClient() {

    MyExternalTaskClientBuilder builder = MyExternalTaskClientBuilder.create();

    builder
      .workerId("my-worker-id")
      .baseUrl("http://localhost:8080/rest/")
      .asyncResponseTimeout(80_000);

    return builder.buildClientWrapper();
  }

}
