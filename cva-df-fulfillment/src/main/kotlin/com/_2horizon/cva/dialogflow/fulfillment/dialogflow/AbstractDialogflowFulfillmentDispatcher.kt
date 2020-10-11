package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.common.dialogflow.Agent
import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.analytics.DialogflowConversionStep
import com._2horizon.cva.dialogflow.fulfillment.event.DialogflowConversionStepEvent
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertObjectToStruct
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertStructToObject
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Context
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.context.event.ApplicationEventPublisher
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * Created by Frank Lieber (liefra) on 2020-10-03.
 */
abstract class AbstractDialogflowFulfillmentDispatcher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val objectMapper: ObjectMapper,
) : DialogflowDispatchable {

    // long running context names
    private val intentStack = "intentStack"

    override fun handle(webhookRequest: WebhookRequest, agent: Agent): Mono<WebhookResponse.Builder> {

        val fulfillmentChain: FulfillmentChain = assembleFulfillmentChain(webhookRequest, agent)

        return dispatch(fulfillmentChain)
    }

    protected fun assembleFulfillmentChain(
        webhookRequest: WebhookRequest,
        agent: Agent
    ): FulfillmentChain {
        val dialogflowConversionStep = createDialogflowConversionStep(webhookRequest, agent)

        val intentStack = createOrUpdateIntentStack(webhookRequest)
        val webhookResponseBuilder = createDefaultWebhookResponseBuilder(intentStack, webhookRequest.session)
        val fulfillmentState = actionToStateLookup(webhookRequest.queryResult.action)

        // send log to elastic event
        applicationEventPublisher.publishEvent(DialogflowConversionStepEvent(dialogflowConversionStep))

        val fulfillmentChain = FulfillmentChain(
            agent, dialogflowConversionStep, fulfillmentState, webhookResponseBuilder, intentStack
        )
        return fulfillmentChain
    }

    private fun createDialogflowConversionStep(webhookRequest: WebhookRequest, agent: Agent): DialogflowConversionStep {
        val now = OffsetDateTime.now(ZoneId.of("UTC")).toLocalDateTime()
        val session = webhookRequest.session
        val queryResult = webhookRequest.queryResult
        val action = queryResult.action
        val responseId = webhookRequest.responseId
        val queryText = queryResult.queryText
        val alternativeQueryResultsCount = webhookRequest.alternativeQueryResultsCount
        val intentName = queryResult.intent.name
        val intentDisplayName = queryResult.intent.displayName
        val intentDetectionConfidence = queryResult.intentDetectionConfidence
        val outputContextsList = queryResult.outputContextsList
        val parameters = objectMapper.convertStructToObject<Map<String, String>>(queryResult.parameters)

        return DialogflowConversionStep(
            dateTime = now,
            session = session,
            action = action,
            agent = agent.name,
            responseId = responseId,
            queryText = queryText,
            alternativeQueryResultsCount = alternativeQueryResultsCount,
            intentName = intentName,
            intentDisplayName = intentDisplayName,
            intentDetectionConfidence = intentDetectionConfidence,
            outputContextsList = outputContextsList,
            outputContexts = outputContextsList.map { it.name },
            parameters = parameters,
            parameterKeys = parameters.keys.toList(),
            parameterValues = parameters.values.toList(),
        )
    }

    private fun createOrUpdateIntentStackContext(
        intentStack: IntentStack,
        session: String
    ): Context {
        //  add intent to intent stack
        return Context.newBuilder().setName(
            "$session/contexts/$intentStack"
        ).setLifespanCount(100).setParameters(objectMapper.convertObjectToStruct(intentStack)).build()
    }

    private fun createDefaultWebhookResponseBuilder(
        intentStack: IntentStack,
        session: String
    ): WebhookResponse.Builder {
        val intentStackContext = createOrUpdateIntentStackContext(intentStack, session)
        return WebhookResponse.newBuilder()
            .addOutputContexts(intentStackContext)
    }

    private fun createOrUpdateIntentStack(
        webhookRequest: WebhookRequest
    ): IntentStack {
        //  add intent to intent stack
        val intentStackName = "${webhookRequest.session}/contexts/$intentStack"
        val intentStackOpt =
            webhookRequest.queryResult.outputContextsList.firstOrNull { it.name == intentStackName }?.parameters?.let { struct ->
                objectMapper.convertStructToObject<IntentStack>(struct)
            }

        return if (intentStackOpt != null) {
            intentStackOpt.flows.add(intentStack)
            intentStackOpt
        } else {
            IntentStack(mutableListOf(intentStack))
        }
    }
}

data class IntentStack(val flows: MutableList<String>)

data class FulfillmentChain(
    val agent: Agent,
    val dialogflowConversionStep: DialogflowConversionStep,
    val fulfillmentState: FulfillmentState,
    val webhookResponseBuilder: WebhookResponse.Builder,
    val intentStack: IntentStack,
)
