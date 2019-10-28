package io.holunda.camunda.concurrent.inventory

import mu.KLogging
import org.camunda.bpm.client.ExternalTaskClient
import org.camunda.bpm.client.task.ExternalTask
import org.camunda.bpm.client.task.ExternalTaskService
import org.camunda.bpm.client.task.impl.ExternalTaskImpl
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.variable.Variables
import org.camunda.bpm.engine.variable.Variables.createVariables
import org.camunda.bpm.engine.variable.value.StringValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
@Component
class ShipGoodsWorker(
  val externalTaskClient: ExternalTaskClient,
  /*@Qualifier("local")*/ val runtimeService: RuntimeService,
  val shipGoodsWorkerProperties: ShipGoodsWorkerProperties
) {

  companion object : KLogging()

  private lateinit var externalTaskService: ExternalTaskService

  @PostConstruct
  fun shipGoods() {
    logger.info { "Starting goods shipping." }
    externalTaskClient
      .subscribe(shipGoodsWorkerProperties.externalTaskTopic)
      .lockDuration(shipGoodsWorkerProperties.timeout)
      .handler { externalTask, externalTaskService ->

        if (!this::externalTaskService.isInitialized) {
          this.externalTaskService = externalTaskService
        }

        try {
          val businessKey = externalTask.businessKey
          val orderId: String = externalTask.getVariableTyped<StringValue>("ORDER_ID").value

          logger.info { "Processing order $orderId." }
          val running = runtimeService.createProcessInstanceQuery().processInstanceBusinessKeyLike(businessKey).list()
          if (running.isEmpty()) {
            val instance = runtimeService.startProcessInstanceByMessage("ship_goods", businessKey,
              createVariables()
                .putValueTyped("ORDER_ID", Variables.stringValue(orderId))
                .putValueTyped("EXTERNAL_TASK_ID", Variables.stringValue(externalTask.id))
            )
          }
        } catch (e: Exception) {
          handleFailure(externalTask, e)
        }
      }
      .open()
  }

  fun complete() = JavaDelegate {
    logger.info { "Process started, completing external task for order ${it.processBusinessKey}." }

    // create an empty task POJO holding the id for completion.
    val externalTask: ExternalTask = ExternalTaskImpl().apply {
      id = it.getVariableTyped<StringValue>("EXTERNAL_TASK_ID").value
    }
    try {
      externalTaskService.complete(externalTask)
    } catch (e: Exception) {
      handleFailure(externalTask, e)
    }
  }

  fun handleFailure(externalTask: ExternalTask, e: Exception) {
    logger.error { "Error occurred handling external task ${externalTask.businessKey}: $e" }
    externalTaskService.handleFailure(
      externalTask,
      "Error shipping goods.",
      e.message,
      shipGoodsWorkerProperties.retriesCount,
      shipGoodsWorkerProperties.retryTimeout)

  }
}


class ShipGoodsWorkerProperties(
  var timeout: Long = 60L * 1000L, // 1 Minute
  var retriesCount: Int = 3,
  var retryTimeout: Long = 10L * 60L * 60L,
  var externalTaskTopic: String = "ship_goods",
  var workerId: String = "io.holunda.camunda.concurrent.inventory.Inventory",
  var baseUrl: String = "http://localhost:8082/rest/engine/default/",
  var asyncResponseTimeout: Long = 1000L
)
