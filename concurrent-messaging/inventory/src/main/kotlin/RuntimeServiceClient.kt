package io.holunda.camunda.concurrent.inventory

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.batch.Batch
import org.camunda.bpm.engine.migration.MigrationPlan
import org.camunda.bpm.engine.migration.MigrationPlanBuilder
import org.camunda.bpm.engine.migration.MigrationPlanExecutionBuilder
import org.camunda.bpm.engine.rest.dto.VariableValueDto
import org.camunda.bpm.engine.rest.dto.message.CorrelationMessageDto
import org.camunda.bpm.engine.runtime.*
import org.camunda.bpm.engine.variable.VariableMap
import org.camunda.bpm.engine.variable.value.TypedValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

@FeignClient(name = "remoteRuntimeService", url = "\${feign.client.config.remoteRuntimeService.url}")
interface RuntimeServiceClient {

  @RequestMapping(method = [RequestMethod.POST], value = ["/message"], consumes = ["application/json"])
  fun correlateMessage(correlationMessage: CorrelationMessageDto)
}

@Qualifier("remote")
@Component
class RemoteRuntimeService(private val runtimeServiceClient: RuntimeServiceClient) : AbstractRuntimeService() {

  override fun correlateMessage(messageName: String, businessKey: String, processVariables: MutableMap<String, Any>) {
    runtimeServiceClient.correlateMessage(
      CorrelationMessageDto().apply {
        this.messageName = messageName
        this.businessKey = businessKey
        this.processVariables = processVariables.mapValues {
          VariableValueDto().apply {
            type = when (it.value) {
              is Boolean -> "Boolean"
              is ByteArray -> "Bytes"
              is Short -> "Short"
              is Integer -> "Integer"
              is Long -> "Long"
              is Double -> "Double"
              is Date -> "Date"
              is String -> "String"
              else -> "object"
            }
            value = it.value
          }
        }
      }
    )
  }
}


abstract class AbstractRuntimeService : RuntimeService {
  override fun signalEventReceived(signalName: String?) {
    TODO("not implemented")
  }

  override fun signalEventReceived(signalName: String?, processVariables: MutableMap<String, Any>?) {
    TODO("not implemented")
  }

  override fun signalEventReceived(signalName: String?, executionId: String?) {
    TODO("not implemented")
  }

  override fun signalEventReceived(signalName: String?, executionId: String?, processVariables: MutableMap<String, Any>?) {
    TODO("not implemented")
  }

  override fun createConditionEvaluation(): ConditionEvaluationBuilder {
    TODO("not implemented")
  }

  override fun getVariables(executionId: String?): MutableMap<String, Any> {
    TODO("not implemented")
  }

  override fun getVariables(executionId: String?, variableNames: MutableCollection<String>?): MutableMap<String, Any> {
    TODO("not implemented")
  }

  override fun createVariableInstanceQuery(): VariableInstanceQuery {
    TODO("not implemented")
  }

  override fun removeVariable(executionId: String?, variableName: String?) {
    TODO("not implemented")
  }

  override fun updateProcessInstanceSuspensionState(): UpdateProcessInstanceSuspensionStateSelectBuilder {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String) {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String, businessKey: String) {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String, correlationKeys: MutableMap<String, Any>) {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String, businessKey: String, processVariables: MutableMap<String, Any>) {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String, correlationKeys: MutableMap<String, Any>, processVariables: MutableMap<String, Any>) {
    TODO("not implemented")
  }

  override fun correlateMessage(messageName: String, businessKey: String, correlationKeys: MutableMap<String, Any>, processVariables: MutableMap<String, Any>) {
    TODO("not implemented")
  }

  override fun setVariable(executionId: String?, variableName: String?, value: Any?) {
    TODO("not implemented")
  }

  override fun setVariables(executionId: String?, variables: MutableMap<String, out Any>?) {
    TODO("not implemented")
  }

  override fun <T : TypedValue?> getVariableTyped(executionId: String?, variableName: String?): T {
    TODO("not implemented")
  }

  override fun <T : TypedValue?> getVariableTyped(executionId: String?, variableName: String?, deserializeValue: Boolean): T {
    TODO("not implemented")
  }

  override fun resolveIncident(incidentId: String?) {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesAsync(processInstanceIds: MutableList<String>?, processInstanceQuery: ProcessInstanceQuery?, deleteReason: String?): Batch {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesAsync(processInstanceIds: MutableList<String>?, processInstanceQuery: ProcessInstanceQuery?, deleteReason: String?, skipCustomListeners: Boolean): Batch {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesAsync(processInstanceIds: MutableList<String>?, processInstanceQuery: ProcessInstanceQuery?, deleteReason: String?, skipCustomListeners: Boolean, skipSubprocesses: Boolean): Batch {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesAsync(processInstanceQuery: ProcessInstanceQuery?, deleteReason: String?): Batch {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesAsync(processInstanceIds: MutableList<String>?, deleteReason: String?): Batch {
    TODO("not implemented")
  }

  override fun getActiveActivityIds(executionId: String?): MutableList<String> {
    TODO("not implemented")
  }

  override fun signal(executionId: String?) {
    TODO("not implemented")
  }

  override fun signal(executionId: String?, signalName: String?, signalData: Any?, processVariables: MutableMap<String, Any>?) {
    TODO("not implemented")
  }

  override fun signal(executionId: String?, processVariables: MutableMap<String, Any>?) {
    TODO("not implemented")
  }

  override fun deleteProcessInstanceIfExists(processInstanceId: String?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean, skipIoMappings: Boolean, skipSubprocesses: Boolean) {
    TODO("not implemented")
  }

  override fun getActivityInstance(processInstanceId: String?): ActivityInstance {
    TODO("not implemented")
  }

  override fun createSignalEvent(signalName: String?): SignalEventReceivedBuilder {
    TODO("not implemented")
  }

  override fun createNativeExecutionQuery(): NativeExecutionQuery {
    TODO("not implemented")
  }

  override fun newMigration(migrationPlan: MigrationPlan?): MigrationPlanExecutionBuilder {
    TODO("not implemented")
  }

  override fun getVariablesTyped(executionId: String?): VariableMap {
    TODO("not implemented")
  }

  override fun getVariablesTyped(executionId: String?, deserializeValues: Boolean): VariableMap {
    TODO("not implemented")
  }

  override fun getVariablesTyped(executionId: String?, variableNames: MutableCollection<String>?, deserializeValues: Boolean): VariableMap {
    TODO("not implemented")
  }

  override fun createMessageCorrelation(messageName: String?): MessageCorrelationBuilder {
    TODO("not implemented")
  }

  override fun suspendProcessInstanceById(processInstanceId: String?) {
    TODO("not implemented")
  }

  override fun deleteProcessInstances(processInstanceIds: MutableList<String>?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean) {
    TODO("not implemented")
  }

  override fun deleteProcessInstances(processInstanceIds: MutableList<String>?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean, skipSubprocesses: Boolean) {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessageAndProcessDefinitionId(messageName: String?, processDefinitionId: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessageAndProcessDefinitionId(messageName: String?, processDefinitionId: String?, businessKey: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessageAndProcessDefinitionId(messageName: String?, processDefinitionId: String?, processVariables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessageAndProcessDefinitionId(messageName: String?, processDefinitionId: String?, businessKey: String?, processVariables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun getVariablesLocal(executionId: String?): MutableMap<String, Any> {
    TODO("not implemented")
  }

  override fun getVariablesLocal(executionId: String?, variableNames: MutableCollection<String>?): MutableMap<String, Any> {
    TODO("not implemented")
  }

  override fun getVariableLocal(executionId: String?, variableName: String?): Any {
    TODO("not implemented")
  }

  override fun messageEventReceived(messageName: String?, executionId: String?) {
    TODO("not implemented")
  }

  override fun messageEventReceived(messageName: String?, executionId: String?, processVariables: MutableMap<String, Any>?) {
    TODO("not implemented")
  }

  override fun activateProcessInstanceById(processInstanceId: String?) {
    TODO("not implemented")
  }

  override fun createIncident(incidentType: String?, executionId: String?, configuration: String?): Incident {
    TODO("not implemented")
  }

  override fun createIncident(incidentType: String?, executionId: String?, configuration: String?, message: String?): Incident {
    TODO("not implemented")
  }

  override fun suspendProcessInstanceByProcessDefinitionKey(processDefinitionKey: String?) {
    TODO("not implemented")
  }

  override fun createProcessInstanceModification(processInstanceId: String?): ProcessInstanceModificationBuilder {
    TODO("not implemented")
  }

  override fun setVariableLocal(executionId: String?, variableName: String?, value: Any?) {
    TODO("not implemented")
  }

  override fun removeVariables(executionId: String?, variableNames: MutableCollection<String>?) {
    TODO("not implemented")
  }

  override fun createMigrationPlan(sourceProcessDefinitionId: String?, targetProcessDefinitionId: String?): MigrationPlanBuilder {
    TODO("not implemented")
  }

  override fun activateProcessInstanceByProcessDefinitionId(processDefinitionId: String?) {
    TODO("not implemented")
  }

  override fun getVariable(executionId: String?, variableName: String?): Any {
    TODO("not implemented")
  }

  override fun createNativeProcessInstanceQuery(): NativeProcessInstanceQuery {
    TODO("not implemented")
  }

  override fun suspendProcessInstanceByProcessDefinitionId(processDefinitionId: String?) {
    TODO("not implemented")
  }

  override fun createExecutionQuery(): ExecutionQuery {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessage(messageName: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessage(messageName: String?, businessKey: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessage(messageName: String?, processVariables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByMessage(messageName: String?, businessKey: String?, processVariables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun createEventSubscriptionQuery(): EventSubscriptionQuery {
    TODO("not implemented")
  }

  override fun removeVariablesLocal(executionId: String?, variableNames: MutableCollection<String>?) {
    TODO("not implemented")
  }

  override fun createProcessInstanceQuery(): ProcessInstanceQuery {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?, businessKey: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?, businessKey: String?, caseInstanceId: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?, businessKey: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceByKey(processDefinitionKey: String?, businessKey: String?, caseInstanceId: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun getVariablesLocalTyped(executionId: String?): VariableMap {
    TODO("not implemented")
  }

  override fun getVariablesLocalTyped(executionId: String?, deserializeValues: Boolean): VariableMap {
    TODO("not implemented")
  }

  override fun getVariablesLocalTyped(executionId: String?, variableNames: MutableCollection<String>?, deserializeValues: Boolean): VariableMap {
    TODO("not implemented")
  }

  override fun activateProcessInstanceByProcessDefinitionKey(processDefinitionKey: String?) {
    TODO("not implemented")
  }

  override fun createProcessInstanceById(processDefinitionId: String?): ProcessInstantiationBuilder {
    TODO("not implemented")
  }

  override fun deleteProcessInstancesIfExists(processInstanceIds: MutableList<String>?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean, skipSubprocesses: Boolean) {
    TODO("not implemented")
  }

  override fun createModification(processDefinitionId: String?): ModificationBuilder {
    TODO("not implemented")
  }

  override fun createProcessInstanceByKey(processDefinitionKey: String?): ProcessInstantiationBuilder {
    TODO("not implemented")
  }

  override fun <T : TypedValue?> getVariableLocalTyped(executionId: String?, variableName: String?): T {
    TODO("not implemented")
  }

  override fun <T : TypedValue?> getVariableLocalTyped(executionId: String?, variableName: String?, deserializeValue: Boolean): T {
    TODO("not implemented")
  }

  override fun removeVariableLocal(executionId: String?, variableName: String?) {
    TODO("not implemented")
  }

  override fun createIncidentQuery(): IncidentQuery {
    TODO("not implemented")
  }

  override fun deleteProcessInstance(processInstanceId: String?, deleteReason: String?) {
    TODO("not implemented")
  }

  override fun deleteProcessInstance(processInstanceId: String?, deleteReason: String?, skipCustomListeners: Boolean) {
    TODO("not implemented")
  }

  override fun deleteProcessInstance(processInstanceId: String?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean) {
    TODO("not implemented")
  }

  override fun deleteProcessInstance(processInstanceId: String?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean, skipIoMappings: Boolean) {
    TODO("not implemented")
  }

  override fun deleteProcessInstance(processInstanceId: String?, deleteReason: String?, skipCustomListeners: Boolean, externallyTerminated: Boolean, skipIoMappings: Boolean, skipSubprocesses: Boolean) {
    TODO("not implemented")
  }

  override fun setVariablesLocal(executionId: String?, variables: MutableMap<String, out Any>?) {
    TODO("not implemented")
  }

  override fun restartProcessInstances(processDefinitionId: String?): RestartProcessInstanceBuilder {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?, businessKey: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?, businessKey: String?, caseInstanceId: String?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?, businessKey: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

  override fun startProcessInstanceById(processDefinitionId: String?, businessKey: String?, caseInstanceId: String?, variables: MutableMap<String, Any>?): ProcessInstance {
    TODO("not implemented")
  }

}
