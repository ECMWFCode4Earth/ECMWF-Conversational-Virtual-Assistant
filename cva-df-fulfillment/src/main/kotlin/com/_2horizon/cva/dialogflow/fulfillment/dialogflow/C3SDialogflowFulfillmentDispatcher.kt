package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.C3SFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.actionAsFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.analytics.DialogflowConversionStep
import com._2horizon.cva.dialogflow.fulfillment.confluence.ConfluenceFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusStatusService
import com._2horizon.cva.dialogflow.fulfillment.event.DialogflowConversionStepEvent
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertObjectToStruct
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertStructToObject
import com._2horizon.cva.dialogflow.fulfillment.fallback.FallbackFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.Context
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.context.event.ApplicationEventPublisher
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class C3SDialogflowFulfillmentDispatcher(
    googleCredentials: GoogleCredentials,
    private val objectMapper: ObjectMapper,
    // private val mediaTypeFulfillmentService: MediaTypeFulfillmentService,
    private val copernicusFulfillmentService: CopernicusFulfillmentService,
    private val fallbackFulfillmentService: FallbackFulfillmentService,
    private val copernicusStatusService: CopernicusStatusService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val confluenceFulfillmentService: ConfluenceFulfillmentService,
) {

    // long running context names
    private val intentStack = "intentStack"

    fun handle(webhookRequest: WebhookRequest): WebhookResponse {

        val dialogflowConversionStep = createDialogflowConversionStep(webhookRequest)

        val intentStack = createOrUpdateIntentStack(webhookRequest)
        val webhookResponseBuilder = createDefaultWebhookResponseBuilder(intentStack, webhookRequest.session)
        val fulfillmentState = actionToStateLookup(webhookRequest.queryResult.action)

        // send log to elastic event
        applicationEventPublisher.publishEvent(DialogflowConversionStepEvent(dialogflowConversionStep))

        val fulfillmentChain = FulfillmentChain(
            dialogflowConversionStep, fulfillmentState, webhookResponseBuilder, intentStack
        )

        val webhookResponseBuilderResponse: WebhookResponse.Builder = when (fulfillmentState) {
            C3SFulfillmentState.FALLBACK_GLOBAL -> fallbackFulfillmentService.handle(
                fulfillmentChain
            )
            C3SFulfillmentState.NOTHING -> webhookResponseBuilder
            C3SFulfillmentState.CDS_DATASET_EXECUTE_DATASET_SEARCH,
            C3SFulfillmentState.CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK -> copernicusFulfillmentService.retrieveDatasetsAsRichContent(
                fulfillmentChain
            )

            C3SFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET -> copernicusFulfillmentService.datasetSelected(
                fulfillmentChain
            )
            C3SFulfillmentState.CDS_DATASET_SHOW_CDS_API_REQUEST_OF_SELECTED_DATASET -> copernicusFulfillmentService.showCdsApiRequestOfSelectedDataset(
                fulfillmentChain
            )
            C3SFulfillmentState.CKB_SEARCH_BY_KEYWORD -> confluenceFulfillmentService.handleSearchByKeyword(fulfillmentChain)
            C3SFulfillmentState.CDS_SHOW_LIVE_STATUS -> copernicusStatusService.showLiveStatus(fulfillmentChain)
        }
        return webhookResponseBuilderResponse.build()

    }

    private fun actionToStateLookup(action: String): C3SFulfillmentState {
        return if (action.isNotBlank()) {
            actionAsFulfillmentState(action)
        } else {
            C3SFulfillmentState.NOTHING
        }
    }

    private fun createDialogflowConversionStep(webhookRequest: WebhookRequest): DialogflowConversionStep {
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
            responseId = responseId,
            queryText = queryText,
            alternativeQueryResultsCount = alternativeQueryResultsCount,
            intentName = intentName,
            intentDisplayName = intentDisplayName,
            intentDetectionConfidence = intentDetectionConfidence,
            outputContextsList=  outputContextsList,
            outputContexts = outputContextsList.map { it.name },
            parameters = parameters,
        )
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
}

data class IntentStack(val flows: MutableList<String>)

data class FulfillmentChain(
    val dialogflowConversionStep: DialogflowConversionStep,
    val c3SFulfillmentState: C3SFulfillmentState,
    val webhookResponseBuilder: WebhookResponse.Builder,
    val intentStack: IntentStack,
)
