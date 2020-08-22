package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.AgentsClient
import com.google.cloud.dialogflow.v2beta1.AgentsSettings
import com.google.cloud.dialogflow.v2beta1.Context
import com.google.cloud.dialogflow.v2beta1.ContextsClient
import com.google.cloud.dialogflow.v2beta1.ContextsSettings
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesClient
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.SessionsClient
import com.google.cloud.dialogflow.v2beta1.SessionsSettings
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat
import io.micronaut.gcp.GoogleCloudConfiguration
import java.time.LocalDateTime
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class DialogflowFulfillmentDispatcher(
    googleCredentials: GoogleCredentials,
    private val objectMapper: ObjectMapper,
    private val googleCloudConfiguration: GoogleCloudConfiguration,
    private val mediaTypeFulfillmentService: MediaTypeFulfillmentService,
    private val copernicusFulfillmentService: CopernicusFulfillmentService,
    private val dfFulfillmentService: DialogflowFulfillmentService
) {

    private val credentialsProvider: FixedCredentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun handle(webhookRequestString: String): WebhookResponse {
        val webhookRequest =
            WebhookRequest.newBuilder().apply { JsonFormat.parser().merge(webhookRequestString, this) }.build()


        println(webhookRequestString)

        val session = webhookRequest.session
        val cvaFlowName = "$session/contexts/cvaflow"

        val queryResult = webhookRequest.queryResult
        val action = queryResult.action
        val cvaFlow = queryResult.outputContextsList.firstOrNull { it.name == cvaFlowName }

        // val flowContext = getContextsClient().getContext(ContextName.ofProjectSessionContextName(googleCloudConfiguration.projectId,session,"flow"))

        return when (action) {
            "show.communication_media_type.list" -> mediaTypeFulfillmentService.handle(webhookRequest)

            "communication_media_typelist.communication_media_typelist-next" -> mediaTypeFulfillmentService.handle(
                webhookRequest
            )

            "cds.status" -> copernicusFulfillmentService.handle(webhookRequest)

            "tenders.list" -> mediaTypeFulfillmentService.handle(webhookRequest)

            "application.list" -> copernicusFulfillmentService.handle(webhookRequest)

            "cds_data_access-dataset-by-name-fallback" -> copernicusFulfillmentService.handle(webhookRequest)

            else -> {
                fallbackResponse(session, cvaFlow)
            }
        }
    }

    internal fun getContextsClient() =
        ContextsClient.create(
            ContextsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    internal fun getSessionsClient() =
        SessionsClient.create(
            SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    internal fun getAgentsClient() =
        AgentsClient.create(
            AgentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    internal fun getSessionEntityTypesClient() =
        SessionEntityTypesClient.create(
            SessionEntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun fallbackResponse(session: String, cvaFlow: Context?): WebhookResponse {
        val now = "Great idea at ${LocalDateTime.now()}"

        val intentFlow =if (cvaFlow != null) {
            val parametersJson = JsonFormat.printer().print(cvaFlow.parameters)
            val intentFlow = objectMapper.readValue(parametersJson, IntentFlow::class.java)
            intentFlow.flows.add(now)
            intentFlow
        } else {
            IntentFlow(mutableListOf(now))
        }
        val flowContext =Context.newBuilder().setName(
            "$session/contexts/cvaflow"
        ).setLifespanCount(100).setParameters( objectToStruct(intentFlow)).build()

        val textMessage = dfFulfillmentService.createTextMessage(now)

        val listReply = dfFulfillmentService.createCustomReply()

        val webhookResponse = WebhookResponse.newBuilder()
            .addOutputContexts(flowContext)
            // .addAllOutputContexts(listOf(flowContext))
            .addAllFulfillmentMessages(
                listOf(
                    textMessage,
                    listReply
                )
            )
            .build()

        return webhookResponse
    }

    private fun objectToStruct(customPayload: Any): Struct {
        val json = objectMapper.writeValueAsString(customPayload)

        val struct = Struct.newBuilder().apply { JsonFormat.parser().merge(json, this) }.build()
        return struct
    }
}

data class IntentFlow(val flows: MutableList<String>)
