package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.common.dialogflow.dto.RichContentDescriptionItem
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.dto.CopernicusDataStoreStatus
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@Singleton
class CopernicusStatusService(
    private val objectMapper: ObjectMapper,
    private val cdsStatusOperations: CopernicusDataStoreStatusOperations,
) : AbstractFulfillmentService(objectMapper) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun showLiveStatus(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {
        return cdsStatusOperations.liveActivityStatus()
            .map(::mapStatusAsRichContent)
            .map {
                convertRichContentItemToWebhookResponse(
                    listOf(it),
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    listOf("I checked the CDS status queue. Here is the current live status.".asIntentMessage())
                )
            }
    }

    private fun mapStatusAsRichContent(status: CopernicusDataStoreStatus): RichContentItem {
        return RichContentDescriptionItem(
            title = "CDS status at ${status.timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME)}",
            text = listOf(
                "Running requests: ${status.running}",
                "Queued requests: ${status.queued}",
                "Total requests: ${status.running + status.queued}",
                "Running users: ${status.runningUsers}",
                "Queued users: ${status.queuedUsers}",
                "Total users: ${status.totalUsers}"
            )
        )
    }
}
