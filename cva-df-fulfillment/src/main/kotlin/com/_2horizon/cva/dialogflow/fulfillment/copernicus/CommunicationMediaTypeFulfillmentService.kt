package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.common.dialogflow.convertToContentSource
import com._2horizon.cva.common.dialogflow.convertToTwitterUserScreenname
import com._2horizon.cva.common.dialogflow.dto.CustomPayload
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticMediaTypeSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertToRichAccordionList
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertToRichContentList
import com._2horizon.cva.dialogflow.fulfillment.twitter.TwitterFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
class CommunicationMediaTypeFulfillmentService(
    objectMapper: ObjectMapper,
    private val elasticMediaTypeSearchService: ElasticMediaTypeSearchService,
    private val twitterFulfillmentService: TwitterFulfillmentService,
) : AbstractFulfillmentService(objectMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun showLatestCommunicationMediaType(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val communicationMediaType = fulfillmentChain.dialogflowConversionStep.parameters["communication_media_type"]
            ?: return Mono.just(fulfillmentChain.webhookResponseBuilder)

        val items: Mono<List<List<RichContentItem>>> = when (communicationMediaType) {
            "news" -> { // main entity id
                elasticMediaTypeSearchService.findLatestNews(fulfillmentChain.agent.convertToContentSource())
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichContentList() } }
            }
            "press release" -> { // main entity id
                elasticMediaTypeSearchService.findLatestPressRelease(fulfillmentChain.agent.convertToContentSource())
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichContentList() } }
            }
            "case study" -> { // main entity id
                elasticMediaTypeSearchService.findLatestCaseStudy(fulfillmentChain.agent.convertToContentSource())
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichContentList() } }
            }
            "demonstrator project" -> { // main entity id
                elasticMediaTypeSearchService.findLatestDemonstratorProject(fulfillmentChain.agent.convertToContentSource())
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichContentList() } }
            }
            "tweet" -> { // main entity id
                twitterFulfillmentService.findLatestTweet(fulfillmentChain.agent.convertToTwitterUserScreenname())
            }
            else -> {
                TODO()
            }
        }

        return items.map { itemsList ->
            if (itemsList.isNotEmpty()) {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(itemsList.take(1)), // only show one in result
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    prefixMessages = listOf(
                        "Wow, I found so many interesting ${
                            createCommunicationMediaTypeLabel(
                                communicationMediaType
                            )
                        }. Here is the most recent one.".asIntentMessage()
                    ),
                )
            } else {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(emptyList()), // only show one in result
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    prefixMessages = listOf("Upps, currently I cannot find any relevant ${communicationMediaType.capitalize()}  articles.".asIntentMessage()),
                )
            }
        }
    }

    private fun createCommunicationMediaTypeLabel(communicationMediaType: String): String =
        if (communicationMediaType == "news") {
            "news articles"
        } else {
            "${communicationMediaType}s"
        }

    fun searchMediaTypeByKeyword(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val communicationMediaType = fulfillmentChain.dialogflowConversionStep.parameters["communication_media_type"]
            ?: return Mono.just(fulfillmentChain.webhookResponseBuilder)

        val keyword = fulfillmentChain.dialogflowConversionStep.parameters["keyword"]
            ?: return Mono.just(fulfillmentChain.webhookResponseBuilder)

        val items: Mono<List<List<RichContentItem>>> = when (communicationMediaType) {
            "news" -> { // main entity id
                elasticMediaTypeSearchService.findNewsByKeyword(
                    fulfillmentChain.agent.convertToContentSource(),
                    keyword
                )
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichAccordionList() } }
            }
            "press release" -> { // main entity id
                elasticMediaTypeSearchService.findPressReleaseByKeyword(
                    fulfillmentChain.agent.convertToContentSource(),
                    keyword
                )
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichAccordionList() } }
            }
            "case study" -> { // main entity id
                elasticMediaTypeSearchService.findCaseStudyByKeyword(
                    fulfillmentChain.agent.convertToContentSource(),
                    keyword
                )
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichAccordionList() } }
            }
            "demonstrator project" -> { // main entity id
                elasticMediaTypeSearchService.findDemonstratorProjectByKeyword(
                    fulfillmentChain.agent.convertToContentSource(),
                    keyword
                )
                    .map { pageNodesResponse -> pageNodesResponse.pageNodes.map { pageNode -> pageNode.convertToRichAccordionList() } }
            }
            "tweet" -> { // main entity id
                twitterFulfillmentService.findTweetsByKeyword(
                    twitterUserScreenname = fulfillmentChain.agent.convertToTwitterUserScreenname(),
                    keyword = keyword
                )
            }

            else -> {
                TODO()
            }
        }

        return items.map { itemsList ->

            if (itemsList.isNotEmpty()) {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(itemsList.take(5)),
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    prefixMessages = listOf(
                        "I found so many interesting ${
                            createCommunicationMediaTypeLabel(
                                communicationMediaType
                            )
                        }. Here are the most relevant ones.".asIntentMessage()
                    ),
                )
            } else {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(emptyList()), // only show one in result
                    fulfillmentChain.dialogflowConversionStep,
                    fulfillmentChain.webhookResponseBuilder,
                    prefixMessages = listOf("Upps, currently I cannot find any relevant ${communicationMediaType.capitalize()}.".asIntentMessage()),
                )
            }

        }
    }
}
