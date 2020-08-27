package com._2horizon.cva.dialogflow.fulfillment.fallback

import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
@Singleton
class FallbackFulfillmentService(
    objectMapper: ObjectMapper,
) : AbstractFulfillmentService(objectMapper) {

    fun handle(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {
        //TODO: improve
        return fulfillmentChain.webhookResponseBuilder
    }
}
