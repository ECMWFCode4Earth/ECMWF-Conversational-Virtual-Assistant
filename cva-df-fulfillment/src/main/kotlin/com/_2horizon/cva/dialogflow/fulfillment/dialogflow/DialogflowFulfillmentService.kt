package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.Event
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.ListResponse
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContent
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-03.
 */
@Singleton
class DialogflowFulfillmentService(
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(webhookRequestString: String): WebhookResponse {
        val wb = WebhookRequest.newBuilder()
        val w = JsonFormat.parser()
            // .ignoringUnknownFields()
            .merge(webhookRequestString, wb)
        wb.build()
        log.debug(webhookRequestString)

        val textMessage = createTextMessage("Great idea at ${LocalDateTime.now()}")

        val listReply = createCustomReply()

        val webhookResponse = WebhookResponse.newBuilder()
            .addAllFulfillmentMessages(
                listOf(
                    textMessage,
                    listReply
                )
            )
            .build()

        return webhookResponse
    }

    private fun createCustomReply(): Intent.Message {

        val w = RichContent(Event("WELCOME"), title = "Go to Welcome")
        val s = RichContent(Event("ECMWF_STAFF"), title = "Go to Staff")

        val l = ListResponse(
            listOf(
                listOf(
                    w,
                    s
                )
            )
        )

        val json = objectMapper.writeValueAsString(l)

        val struct = Struct.newBuilder().apply { JsonFormat.parser().merge(json, this) }.build()


        return Intent.Message.newBuilder()
            .setPayload(
                struct
            ).build()
    }

    private fun createQuickReply(): Intent.Message {

        val allQuickReplies = Intent.Message.QuickReplies.newBuilder()
            .addAllQuickReplies(listOf("YES", "NO"))

        val message = Intent.Message.newBuilder()
            .setQuickReplies(allQuickReplies).build()

        return message
    }

    private fun createTextMessage(text: String): Intent.Message {
        val textMessage = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(listOf(text)).build()
            ).build()
        return textMessage
    }
}


