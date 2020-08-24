package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.actionAsFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.analytics.DialogflowConversionStep
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertObjectToStruct
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertStructToObject
import com._2horizon.cva.dialogflow.fulfillment.fallback.FallbackFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.Context
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import java.time.OffsetDateTime
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class DialogflowFulfillmentDispatcher(
    googleCredentials: GoogleCredentials,
    private val objectMapper: ObjectMapper,
    private val mediaTypeFulfillmentService: MediaTypeFulfillmentService,
    private val copernicusFulfillmentService: CopernicusFulfillmentService,
    private val fallbackFulfillmentService: FallbackFulfillmentService,
    private val dfFulfillmentService: DialogflowFulfillmentService,
) {
        

    // long running context names
    private val intentStack = "intentStack"

    fun handle(webhookRequest: WebhookRequest): WebhookResponse {

        val dialogflowConversionStep = createDialogflowConversionStep(webhookRequest)

        val intentStack = createOrUpdateIntentStack(webhookRequest)
        val webhookResponseBuilder = createDefaultWebhookResponseBuilder(intentStack, webhookRequest.session)
        val fulfillmentState = actionToStateLookup(webhookRequest.queryResult.action)

        // TODO: send log to elastic event
        println(dialogflowConversionStep)

        val fulfillmentChain = FulfillmentChain(
            webhookRequest,fulfillmentState,webhookResponseBuilder,intentStack
        )

        val webhookResponseBuilderResponse: WebhookResponse.Builder =  when (fulfillmentState) {
            FulfillmentState.FALLBACK_GLOBAL -> fallbackFulfillmentService.handle(
                fulfillmentChain
            )
            FulfillmentState.NOTHING -> webhookResponseBuilder
            FulfillmentState.CF_CDS_DATASET_EXECUTE_DATASET_SEARCH,
            FulfillmentState.CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK -> copernicusFulfillmentService.handle(
                fulfillmentChain
            )
        }
        return webhookResponseBuilderResponse.build()

        //  when (action) {
        //
        //     "cf_cds_show_list_of_datasets"
        //
        //     // "show.communication_media_type.list" -> mediaTypeFulfillmentService.handle(webhookRequest)
        //     //
        //     // "communication_media_typelist.communication_media_typelist-next" -> mediaTypeFulfillmentService.handle(
        //     //     webhookRequest
        //     // )
        //     //
        //     // "cds.status" -> copernicusFulfillmentService.handle(webhookRequest)
        //     //
        //     // "tenders.list" -> mediaTypeFulfillmentService.handle(webhookRequest)
        //     //
        //     // "application.list" -> copernicusFulfillmentService.handle(webhookRequest)
        //     //
        //     // "cds_data_access-dataset-by-name-fallback" -> copernicusFulfillmentService.handle(webhookRequest)
        //
        //     else -> {
        //
        //     }
        // }
    }

    private fun actionToStateLookup(action: String): FulfillmentState {
        return if (action.isNotBlank()) {
            actionAsFulfillmentState(action)
        } else {
            FulfillmentState.NOTHING
        }
    }

    private fun createDialogflowConversionStep(webhookRequest: WebhookRequest): DialogflowConversionStep {
        val now = OffsetDateTime.now()
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

        return DialogflowConversionStep(
            datetime = now,
            session = session,
            action = action,
            responseId = responseId,
            queryText = queryText,
            alternativeQueryResultsCount = alternativeQueryResultsCount,
            intentName = intentName,
            intentDisplayName = intentDisplayName,
            intentDetectionConfidence = intentDetectionConfidence,
            outputContexts = outputContextsList.map { it.name }
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
    val webhookRequest: WebhookRequest,
    val fulfillmentState: FulfillmentState,
    val webhookResponseBuilder: WebhookResponse.Builder,
    val intentStack: IntentStack,
)
