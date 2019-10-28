package io.holunda.spike.external.task.client;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.impl.ExternalTaskClientBuilderImpl;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.client.task.impl.ExternalTaskServiceImpl;

/**
 * Overriding builder to be able to setup client-related stuff.
 */
public class MyExternalTaskClientBuilder extends ExternalTaskClientBuilderImpl {

  /**
   * Creates working instance.
   */
  public static MyExternalTaskClientBuilder create() {
    return new MyExternalTaskClientBuilder();
  }

  /**
   * Retrieves the external task wrapper, containing the external task client and the external task service.
   */
  public ExternalTaskClientWrapper buildClientWrapper() {
    /*
     * This creates the external client and initializes the engine client
     */
    final ExternalTaskClient client = super.build();
    /*
     * It is ok to create our own external task service instance, since it is stateless
     * and only provides methods to operate on tasks, which delegate to the engine client.
     */
    final ExternalTaskService service = new ExternalTaskServiceImpl(engineClient);
    /*
     * Return wrapped instance.
     */
    return new ExternalTaskClientWrapper(client, service);
  }
}
