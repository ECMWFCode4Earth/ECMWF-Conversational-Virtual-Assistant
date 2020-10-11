package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.common.dialogflow.convertToContentSource
import com._2horizon.cva.common.dialogflow.dto.CustomPayload
import com._2horizon.cva.common.dialogflow.dto.RichContentInfoItem
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticEventSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertToRichContentList
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
class EventsFulfillmentService(
    objectMapper: ObjectMapper,
    private val elasticEventSearchService: ElasticEventSearchService,
) : AbstractFulfillmentService(objectMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun listEvents(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val eventsWithViewMoreLink: Mono<List<List<RichContentItem>>> =
            elasticEventSearchService.findUpcomingEvents(fulfillmentChain.agent.convertToContentSource())
                .map { pageNodesResponse -> pageNodesResponse.eventNodes.map { eventNode -> eventNode.convertToRichContentList() } }
                .map { events ->
                    val updatedEventsList = events
                        .toMutableList()

                    updatedEventsList.add(
                        mutableListOf(
                            RichContentInfoItem(
                                title = "Click here to see all events",
                                actionLink = "https://climate.copernicus.eu/events"
                            )
                        )
                    )

                    updatedEventsList
                }

        return eventsWithViewMoreLink
            .map { e ->

                convertCustomPayloadToWebhookResponse(
                    CustomPayload(e),
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    prefixMessages = listOf("I found many interesting, upcoming events:".asIntentMessage()),
                )
            }
    }
}
