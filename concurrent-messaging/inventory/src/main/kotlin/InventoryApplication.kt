package io.holunda.camunda.concurrent.inventory

import feign.Logger
import mu.KLogging
import org.camunda.bpm.client.ExternalTaskClient
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.variable.Variables.*
import org.camunda.bpm.engine.variable.value.StringValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.util.*

fun main() {
  SpringApplication.run(InventoryApplication::class.java)
}

@SpringBootApplication
@EnableFeignClients
class InventoryApplication {

  companion object : KLogging()

  @Bean
  fun externalTaskClient(shipGoodsWorkerProperties: ShipGoodsWorkerProperties) =
    ExternalTaskClient
      .create()
      .workerId(shipGoodsWorkerProperties.workerId)
      .baseUrl(shipGoodsWorkerProperties.baseUrl)
      .asyncResponseTimeout(shipGoodsWorkerProperties.asyncResponseTimeout)
      .build()

  @Bean
  fun loggingListener() = ExecutionListener {
    logger.info { "EXEC-LOG: Executing '${it.currentActivityNamePretty()}' on ${it.eventName}, payload is ${it.variables}" }
  }

  fun DelegateExecution.currentActivityNamePretty() =
    this.currentActivityName
      .replace("\n", " ")
      .replace("\t", " ")
      .replace("  ", " ")


  @Bean
  fun shipGoodsWorkerProperties() = ShipGoodsWorkerProperties()

  @Bean
  fun passToTransportPartnerDelegate(runtimeService: RuntimeService) = JavaDelegate {
    val shipmentId = "SHIP-${UUID.randomUUID()}"
    logger.info { "Shipment passed to transport partner, tracking id is: $shipmentId." }
    it.setVariable("SHIPMENT_ID", stringValue(shipmentId))
  }

  @Bean("checkInventoryDelegate")
  fun checkInventoryDelegate() = JavaDelegate {
    logger.info { "Shipment checked in inventory." }
  }

  @Bean
  fun messageSender(@Qualifier("remote") runtimeService: RuntimeService) = JavaDelegate {
    logger.info { "Message 'goods_shipped' sent." }
    runtimeService.correlateMessage(
      "goods_shipped",
      it.processBusinessKey,
      createVariables()
        .putValueTyped("SHIPMENT_ID", it.getVariableTyped<StringValue>("SHIPMENT_ID"))
        .putValueTyped("BYTE_ARRAY", byteArrayValue("Hello".toCharArray().map { it.toByte() }.toByteArray()))
        .putValueTyped("INTEGER", integerValue(8923746))
        .putValueTyped("DATE", dateValue(Date.from(Instant.now())))
    )
  }

/*
  @Bean
  @Qualifier("local")
  fun runtimeServiceLocal(processEngine: ProcessEngine) = processEngine.runtimeService

*/
  @Bean
  fun feignLoggerLevel(): Logger.Level = Logger.Level.FULL
}

