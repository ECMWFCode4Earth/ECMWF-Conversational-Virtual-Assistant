package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.common.dialogflow.Agent
import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-10-03.
 */
interface DialogflowDispatchable {

    fun handle(webhookRequest: WebhookRequest, agent: Agent): Mono<WebhookResponse.Builder>

    fun dispatch(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder>

    fun actionToStateLookup(action: String): FulfillmentState
}
