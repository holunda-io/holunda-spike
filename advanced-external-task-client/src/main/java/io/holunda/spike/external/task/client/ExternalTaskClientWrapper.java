package io.holunda.spike.external.task.client;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTaskService;

/**
 * Wraps client and the service.
 */
public class ExternalTaskClientWrapper {

  private final ExternalTaskClient client;
  private final ExternalTaskService service;

  /**
   * Constructs the POJO.
   * @param client external task client.
   * @param service external task service.
   */
  public ExternalTaskClientWrapper(ExternalTaskClient client, ExternalTaskService service) {
    this.client = client;
    this.service = service;
  }

  /**
   * Retrieves the service.
   * @return service instance.
   */
  public ExternalTaskService getExternalTaskService() {
    return service;
  }

  /**
   * Retrieves the client.
   * @return the client.
   */
  public ExternalTaskClient getExternalTaskClient() {
    return client;
  }
}
