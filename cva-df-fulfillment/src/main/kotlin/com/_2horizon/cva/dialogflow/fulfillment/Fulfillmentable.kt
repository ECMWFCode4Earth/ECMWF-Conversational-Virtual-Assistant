package com._2horizon.cva.dialogflow.fulfillment

import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com.google.cloud.dialogflow.v2beta1.WebhookResponse

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
interface Fulfillmentable {

    fun handle(
        fulfillmentChain: FulfillmentChain
    ): WebhookResponse.Builder
}
