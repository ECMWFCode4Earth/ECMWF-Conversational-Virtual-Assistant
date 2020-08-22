package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.CustomPayload
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.Event
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentListItem
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-03.
 */
@Singleton
class DialogflowFulfillmentService(
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    internal fun createCustomReply(): Intent.Message {

       

        val w = RichContentListItem(event = Event("WELCOME"), title = "Go to Welcome")
        val s = RichContentListItem(event = Event("ECMWF_STAFF"), title = "Go to Staff")

        val l = CustomPayload(
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

    internal fun createTextMessage(text: String): Intent.Message {
        val textMessage = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(listOf(text)).build()
            ).build()
        return textMessage
    }
}


