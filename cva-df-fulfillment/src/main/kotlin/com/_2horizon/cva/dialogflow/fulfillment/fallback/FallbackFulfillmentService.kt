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
import com._2horizon.cva.dialogflow.fulfillment.extensions.posTagging
import com._2horizon.cva.dialogflow.fulfillment.extensions.splitIntoWords
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {


        val keyword = fc.dialogflowConversionStep.queryText
        val words = keyword.splitIntoWords()
        val posTags = posTagging(words)
        log.info("$words --> $posTags")
        val tweetsMono =
            elasticTwitterSearchService.findTweetByKeyword(fc.agent.convertToTwitterUserScreenname(), keyword, size = 0)
        val newsMono = elasticMediaTypeSearchService.findNewsByKeyword(fc.agent.convertToContentSource(), keyword, size = 0)
        val pressReleasesMono =
            elasticMediaTypeSearchService.findPressReleaseByKeyword(fc.agent.convertToContentSource(), keyword, size = 0)
        val caseStudiesMono =
            elasticMediaTypeSearchService.findCaseStudyByKeyword(fc.agent.convertToContentSource(), keyword, size = 0)
        val demonstratorProjectsMono =
            elasticMediaTypeSearchService.findDemonstratorProjectByKeyword(fc.agent.convertToContentSource(), keyword, size = 0)
        val confluenceContentMono = confluenceFulfillmentService.searchByKeyword(keyword = keyword, size = 15)

        val items =
            Mono.zip(
                tweetsMono,
                newsMono,
                pressReleasesMono,
                caseStudiesMono,
                demonstratorProjectsMono,
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


                    if (news.totalHits>0) {
                        items.add(
                            RichContentListItem(
                                "News (${news.totalHits})",
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
                    if (pressReleases.totalHits>0) {
                        items.add(
                            RichContentListItem(
                                "Press Releases (${pressReleases.totalHits})",
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
                    if (caseStudies.totalHits>0) {
                        items.add(
                            RichContentListItem(
                                "Case Studies (${pressReleases.totalHits})",
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
                    if (demonstratorProects.totalHits>0) {
                        items.add(
                            RichContentListItem(
                                "Demonstrator Projects (${pressReleases.totalHits})",
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

                    if (tweets.totalHits>0) {
                        items.add(
                            RichContentListItem(
                                "Tweets (${tweets.totalHits})", subtitle = "Click to view Tweets", event = Event(
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
                prefixMessages = listOf("Hmmm, I'm not sure what exactly you are looking for, but I found the following related items:".asIntentMessage()),
            )
        }
    }
}
