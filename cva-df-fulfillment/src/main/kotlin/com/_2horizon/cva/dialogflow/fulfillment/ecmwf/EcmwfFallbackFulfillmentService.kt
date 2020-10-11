package com._2horizon.cva.dialogflow.fulfillment.ecmwf

import com._2horizon.cva.common.dialogflow.convertToTwitterUserScreenname
import com._2horizon.cva.common.dialogflow.dto.CustomPayload
import com._2horizon.cva.common.dialogflow.dto.Event
import com._2horizon.cva.common.dialogflow.dto.RichContentDividerItem
import com._2horizon.cva.common.dialogflow.dto.RichContentInfoItem
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.common.dialogflow.dto.RichContentListItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.EcmwfFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.confluence.ConfluenceFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications.EcmwfPublicationsSearchService
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticTwitterSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com._2horizon.cva.dialogflow.fulfillment.extensions.posTagging
import com._2horizon.cva.dialogflow.fulfillment.extensions.splitIntoWords
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-10-03.
 */
class EcmwfFallbackFulfillmentService(
    objectMapper: ObjectMapper,
    private val ecmwfPublicationsSearchService: EcmwfPublicationsSearchService,
    private val elasticTwitterSearchService: ElasticTwitterSearchService,
    private val confluenceFulfillmentService: ConfluenceFulfillmentService,
) : AbstractFulfillmentService(objectMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val keyword = fc.dialogflowConversionStep.queryText
        val words = keyword.splitIntoWords()
        val posTags = posTagging(words)
        log.info("$words --> $posTags")

        // TODO: Check if and how the fallback search can be improved once we have more analytics data
        return if (words.size < 4) {
            executeFallbackSearch(fc, keyword)
        } else {
            Mono.just(fc.webhookResponseBuilder)
        }
    }

    private fun executeFallbackSearch(fc: FulfillmentChain, keyword: String): Mono<WebhookResponse.Builder> {
        val tweetsMono =
            elasticTwitterSearchService.findTweetByKeyword(fc.agent.convertToTwitterUserScreenname(), keyword, size = 0)

        val publicationsSearchMono =
            ecmwfPublicationsSearchService.findNumberOfResultsByKeyword(keyword)

        val confluenceContentMono =
            confluenceFulfillmentService.searchByKeyword(keyword = keyword, size = 100, space = "UDOC")

        val items =
            Mono.zip(
                tweetsMono,
                publicationsSearchMono,
                confluenceContentMono,
            )
                .map {
                    val tweets = it.t1
                    val publications = it.t2
                    val confluenceContent = it.t3

                    val items = mutableListOf<RichContentItem>()

                    if (confluenceContent.contents.isNotEmpty()) {
                        items.add(
                            RichContentListItem(
                                "UDOC wiki pages (${confluenceContent.contents.size})",
                                subtitle = "Click to view UDOC wiki pages",
                                event = Event(
                                    name = EcmwfFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD.toString().toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                        Pair("confluence", "confluence"),
                                    )
                                )
                            )
                        )
                    }

                    if (publications > 0) {
                        items.add(
                            RichContentInfoItem(
                                "Publications (${publications})",
                                subtitle = "Click to view Publications",
                                actionLink = "https://www.ecmwf.int/en/publications/search/$keyword"
                            )
                        )
                        items.add(RichContentDividerItem())
                    }

                    if (tweets.totalHits > 0) {
                        items.add(
                            RichContentListItem(
                                "Tweets (${tweets.totalHits})", subtitle = "Click to view Tweets", event = Event(
                                    name = EcmwfFulfillmentState.SEARCH_TWEETS_BY_KEYWORD.toString()
                                        .toLowerCase(),
                                    parameters = mapOf(
                                        Pair("keyword", keyword),
                                    )
                                )
                            )
                        )
                    }

                    items

                }

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
