package io.holunda.camunda.concurrent.order

import mu.KLogging
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.variable.Variables.createVariables
import org.camunda.bpm.engine.variable.Variables.stringValue
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

fun main() {
  SpringApplication.run(OrderApplication::class.java)
}

@SpringBootApplication
class OrderApplication {

  companion object : KLogging()

  @Bean
  fun loggingListener() = ExecutionListener {
    logger.info { "EXEC-LOG: Executing '${it.currentActivityNamePretty()}' on ${it.eventName}, payload is ${it.variables}" }
  }

  fun DelegateExecution.currentActivityNamePretty() =
    this.currentActivityName
      .replace("\n", " ")
      .replace("\t", " ")
      .replace("  ", " ")
}


@RestController
@RequestMapping("/order")
class OrderController(val runtimeService: RuntimeService) {

  @PostMapping
  fun newOrder(): ResponseEntity<String> {
    val orderId = "ORDER-${UUID.randomUUID().toString()}"
    val instance = runtimeService
      .startProcessInstanceByMessage("order_created", orderId,
        createVariables()
          .putValueTyped("ORDER_ID", stringValue(orderId))
      )
    return ok(instance.id)
  }
}


