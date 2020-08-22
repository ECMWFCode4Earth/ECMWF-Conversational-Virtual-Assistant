package com._2horizon.cva.dialogflow.fulfillment

import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
interface Fulfillmentable {

    fun handle(webhookRequest: WebhookRequest): WebhookResponse
}
