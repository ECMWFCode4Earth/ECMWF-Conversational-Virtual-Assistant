package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.protobuf.util.JsonFormat
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.slf4j.LoggerFactory

/**
 * Created by Frank Lieber (liefra) on 2020-07-02.
 */
@Controller("/fulfillment")
class DialogflowFulfillmentController(
    private val dfFulfillmentDispatcher: DialogflowFulfillmentDispatcher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Post("/request")
    fun fulfillment(@Body webhookRequestString: String): String {

        log.info("Got a webhookRequestString")

        val webhookRequest =
            WebhookRequest.newBuilder().apply { JsonFormat.parser().merge(webhookRequestString, this) }.build()

        // println(webhookRequestString)

        val webhookResponse = dfFulfillmentDispatcher.handle(webhookRequest)

        log.info("Sending back webhookResponse")

        // println(webhookResponse)
        
        return JsonFormat.printer().print(webhookResponse)
    }
}
