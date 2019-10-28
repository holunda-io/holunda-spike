package io.holunda.camunda.orchestration.callactivity

import mu.KLogging
import org.apache.ibatis.ognl.OgnlOps.booleanValue
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.delegate.TaskListener
import org.camunda.bpm.engine.variable.Variables.createVariables
import org.camunda.bpm.engine.variable.Variables.stringValue
import org.camunda.bpm.engine.variable.value.StringValue
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.*
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.*

fun main() {
  SpringApplication.run(CallActivityExampleApplication::class.java)
}

@SpringBootApplication
class CallActivityExampleApplication {

  companion object : KLogging()

  @Bean
  fun loggingListener() = ExecutionListener {
    logger.info { "EXEC-LOG: Executing '${it.currentActivityNamePretty()}' on ${it.eventName}, payload is ${it.variables}" }
  }

  /**
   * Simulating different inventory availability, deducting it from order id.
   */
  @Bean
  fun checkInventoryDelegate() = JavaDelegate {
    it.parentId
    val orderId = it.getVariableTyped<StringValue>("ORDER_ID").value
    it.setVariable("GOODS_AVAILABLE", booleanValue(orderId.contains("1")))
  }

  @Bean
  fun deliverGoodsDelegate() = JavaDelegate {
    it.setVariable("ORDER_DELIVERED", booleanValue(true))
  }

  @Bean
  fun executePaymentDelegate() = JavaDelegate {
    val paymentType = it.getVariableTyped<StringValue>("PAYMENT_TYPE").value
    logger.info { "Selected payment type is $paymentType" }
    it.setVariable("PAYED", booleanValue(true))
  }

  @Bean
  fun paymentStartListener() = ExecutionListener {
    val paymentTimeout = now().plus(10, ChronoUnit.MINUTES).atOffset(ZoneOffset.UTC).toString()
    logger.info { "Setting payment timeout to $paymentTimeout" }
    it.setVariable("PAYMENT_TIMEOUT", paymentTimeout)
  }

  @Bean
  fun taskIdRevealingTaskListener() = TaskListener {
    logger.info { "Task with id ${it.id} is waiting to completed." }
  }


  @RestController
  @RequestMapping("/call-activities")
  class Controller(val runtimeService: RuntimeService, val taskService: TaskService) {

    @PostMapping("/start/{id}")
    fun start(@PathVariable("id") orderId: String): ResponseEntity<String> {
      val vars = createVariables()
        .putValueTyped("ORDER_ID", stringValue(orderId))

      val instance = runtimeService
        .createProcessInstanceByKey("process_order_management")
        .businessKey("ORDER-$orderId")
        .setVariables(vars)
        .executeWithVariablesInReturn()

      return ResponseEntity.ok(instance.id)
    }

    @PostMapping("/payment/{taskId}")
    fun selectPayment(@PathVariable("taskId") taskId: String) {
      val task = taskService.createTaskQuery().taskId(taskId).singleResult()
      val vars = createVariables()
        .putValueTyped("PAYMENT_TYPE", stringValue("CC"))
      taskService.complete(task.id, vars)
    }
  }
}


fun DelegateExecution.currentActivityNamePretty() =
  this.currentActivityName
    .replace("\n", " ")
    .replace("\t", " ")
    .replace("  ", " ")
