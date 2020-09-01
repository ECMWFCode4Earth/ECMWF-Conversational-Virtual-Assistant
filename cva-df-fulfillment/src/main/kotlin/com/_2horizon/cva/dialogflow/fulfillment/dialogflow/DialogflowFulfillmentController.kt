package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.common.dialogflow.Agent
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
    private val dfFulfillmentDispatcherC3S: C3SDialogflowFulfillmentDispatcher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Post("/c3s-request")
    fun c3sFulfillment(@Body webhookRequestString: String): String = fulfillment(webhookRequestString, Agent.C3S_CVA)

    @Post("/cams-request")
    fun camsFulfillment(@Body webhookRequestString: String): String = fulfillment(webhookRequestString, Agent.CAMS_CVA)

    @Post("/ecmwf-request")
    fun ecmwfFulfillment(@Body webhookRequestString: String): String =
        fulfillment(webhookRequestString, Agent.ECMWF_CVA)

    private fun fulfillment(webhookRequestString: String, agent: Agent): String {
        val webhookRequest = convertJsonToWebhookRequest(webhookRequestString)

        val webhookResponse = if (agent == Agent.C3S_CVA) {
            dfFulfillmentDispatcherC3S.handle(webhookRequest)
        } else {
            TODO()
        }

        return JsonFormat.printer().print(webhookResponse)
    }

    private fun convertJsonToWebhookRequest(webhookRequestString: String): WebhookRequest =
        WebhookRequest.newBuilder().apply { JsonFormat.parser().merge(webhookRequestString, this) }.build()
}


