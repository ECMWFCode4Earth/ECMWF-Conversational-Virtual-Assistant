package com._2horizon.cva.dialogflow.fulfillment

import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.CustomPayload
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertObjectToStruct
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Context
import com.google.cloud.dialogflow.v2beta1.EventInput
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import java.time.LocalDateTime
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Singleton
abstract class AbstractFulfillmentService(
    private val objectMapper: ObjectMapper
) : Fulfillmentable {

    fun convertRichContentItemToWebhookResponse(
        item: RichContentItem,
        webhookRequest: WebhookRequest,
        webhookResponseBuilder: WebhookResponse.Builder,
    ): WebhookResponse.Builder {
        val customPayload = CustomPayload(
            listOf(
                listOf(
                    item
                )
            )
        )

        return convertCustomPayloadToWebhookResponse(customPayload, webhookRequest,webhookResponseBuilder)
    }

    fun convertCustomPayloadToWebhookResponse(
        customPayload: CustomPayload,
        webhookRequest: WebhookRequest,
        webhookResponseBuilder: WebhookResponse.Builder,
        prefixMessages: List<Intent.Message>? = null,
        postfixMessages: List<Intent.Message>? = null

    ): WebhookResponse.Builder {
        val struct = objectMapper.convertObjectToStruct(customPayload)

        val message: Intent.Message = Intent.Message.newBuilder()
            .setPayload(
                struct
            ).build()

        val messages = mutableListOf<Intent.Message>()

        if (prefixMessages != null) {
            messages.addAll(prefixMessages)
        }
        messages.add(message)

        if (postfixMessages != null) {
            messages.addAll(postfixMessages)
        }

        val existingContext = webhookRequest.queryResult.outputContextsList
        val session = webhookRequest.session

        val context = Context.newBuilder()
            .setName("$session/contexts/labels")
            .setParameters(objectMapper.convertObjectToStruct(Label("some-label ${LocalDateTime.now()}")))
            .setLifespanCount(100)
            .build()
        // existingContext.add(context)

        val event = EventInput.newBuilder().setName("Welcome")



        return webhookResponseBuilder

            // .addAllOutputContexts(listOf(context))
            // .setFollowupEventInput(event)
            .addAllFulfillmentMessages(messages)

    }

    // private fun objectToStruct(customPayload: Any): Struct {
    //     val json = objectMapper.writeValueAsString(customPayload)
    //
    //     val struct = Struct.newBuilder().apply { JsonFormat.parser().merge(json, this) }.build()
    //     return struct
    // }

    // fun fallbackResponse(): WebhookResponse {
    //     val textMessage = createTextMessage("Great idea at ${LocalDateTime.now()}")
    //
    //     val webhookResponse = WebhookResponse.newBuilder()
    //         .addAllFulfillmentMessages(
    //             listOf(
    //                 textMessage
    //             )
    //         )
    //         .build()
    //
    //     return webhookResponse
    // }

    internal fun createTextMessage(text: String): Intent.Message {
        val textMessage = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(listOf(text)).build()
            ).build()
        return textMessage
    }
}

data class Label(val label: String)
