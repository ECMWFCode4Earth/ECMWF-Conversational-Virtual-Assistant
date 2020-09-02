package com._2horizon.cva.dialogflow.fulfillment.fallback

import com._2horizon.cva.common.dialogflow.convertToContentSource
import com._2horizon.cva.common.dialogflow.convertToTwitterUserScreenname
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.C3SFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.confluence.ConfluenceFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.CustomPayload
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.Event
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentListItem
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticMediaTypeSearchService
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticTwitterSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
@Singleton
class FallbackFulfillmentService(
    objectMapper: ObjectMapper,
    private val elasticMediaTypeSearchService: ElasticMediaTypeSearchService,
    private val elasticTwitterSearchService: ElasticTwitterSearchService,
    private val confluenceFulfillmentService: ConfluenceFulfillmentService,
) : AbstractFulfillmentService(objectMapper) {

    fun handle(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val keyword = fc.dialogflowConversionStep.queryText
        val tweetsMono =
            elasticTwitterSearchService.findTweetByKeyword(fc.agent.convertToTwitterUserScreenname(), keyword)
        val newsMono = elasticMediaTypeSearchService.findNewsByKeyword(fc.agent.convertToContentSource(), keyword)
        val pressReleasesMono =
            elasticMediaTypeSearchService.findPressReleaseByKeyword(fc.agent.convertToContentSource(), keyword)
        val caseStudiesMono =
            elasticMediaTypeSearchService.findCaseStudyByKeyword(fc.agent.convertToContentSource(), keyword)
        val demonstratorProectsMono =
            elasticMediaTypeSearchService.findDemonstratorProjectByKeyword(fc.agent.convertToContentSource(), keyword)
        val confluenceContentMono = confluenceFulfillmentService.searchByKeyword(keyword = keyword)

        val items =
            Mono.zip(
                tweetsMono,
                newsMono,
                pressReleasesMono,
                caseStudiesMono,
                demonstratorProectsMono,
                confluenceContentMono
            )
                .map {
                    val tweets = it.t1
                    val news = it.t2
                    val pressReleases = it.t3
                    val caseStudies = it.t4
                    val demonstratorProects = it.t5
                    val confluenceContent = it.t6

                    val items = mutableListOf<RichContentItem>()


                    if (news.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "News ${news.size}",
                                subtitle = "Click to view News",
                                event = Event(
                                    name = C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("communication_media_type", "news"),
                                    )
                                )
                            )
                        )
                    }
                    if (pressReleases.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "Press Releases ${pressReleases.size}",
                                subtitle = "Click to view Press Releases",
                                event = Event(
                                    name = C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("communication_media_type", "press release"),
                                    )
                                )
                            )
                        )
                    }
                    if (caseStudies.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "Case Studies ${pressReleases.size}",
                                subtitle = "Click to view Case Studies",
                                event = Event(
                                    name = C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("communication_media_type", "case study"),
                                    )
                                )
                            )
                        )
                    }
                    if (demonstratorProects.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "Demonstrator Projects ${pressReleases.size}",
                                subtitle = "Click to view Demonstrator Projects",
                                event = Event(
                                    name = C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("communication_media_type", "demonstrator project"),
                                    )
                                )
                            )
                        )
                    }
                    if (confluenceContent.contents.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "CKB pages ${confluenceContent.contents.size}",
                                subtitle = "Click to view CKB pages",
                                event = Event(
                                    name = C3SFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD.toString().toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("CKB", "CKB"),
                                    )
                                )
                            )
                        )
                    }

                    if (tweets.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "Tweets ${tweets.size}", subtitle = "Click to view Tweets", event = Event(
                                    name = C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("communication_media_type", "tweet"),
                                        Pair("keyword", keyword),
                                    )
                                )
                            )
                        )
                    }

                    items

                }

        // return  Mono.just(fc.webhookResponseBuilder)

        return items.map { itemList ->
            convertCustomPayloadToWebhookResponse(
                CustomPayload(listOf(itemList)),
                fc.dialogflowConversionStep,
                fc.webhookResponseBuilder,
                prefixMessages = listOf("Found the following messages".asIntentMessage()),
            )
        }
    }
}
