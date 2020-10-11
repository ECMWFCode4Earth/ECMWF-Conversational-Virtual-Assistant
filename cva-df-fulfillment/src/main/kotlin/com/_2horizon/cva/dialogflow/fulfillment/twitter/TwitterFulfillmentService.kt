package com._2horizon.cva.dialogflow.fulfillment.twitter

import com._2horizon.cva.common.dialogflow.convertToTwitterUserScreenname
import com._2horizon.cva.common.dialogflow.dto.CustomPayload
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticTwitterSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertToRichContentList
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-10-03.
 */
class TwitterFulfillmentService(
    objectMapper: ObjectMapper,
    private val elasticTwitterSearchService: ElasticTwitterSearchService,
) : AbstractFulfillmentService(objectMapper) {

    fun findLatestTweet(twitterUserScreenname: String): Mono<List<List<RichContentItem>>> {
        return elasticTwitterSearchService.findLatestTweet(twitterUserScreenname)
            .map { tweetResponse -> tweetResponse.tweets.map { tweet -> tweet.convertToRichContentList() } }
    }

    fun findTweetsByKeyword(twitterUserScreenname: String, keyword: String): Mono<List<List<RichContentItem>>> {
        return elasticTwitterSearchService.findTweetByKeyword(twitterUserScreenname, keyword)
            .map { tweetResponse -> tweetResponse.tweets.map { tweet -> tweet.convertToRichContentList() } }
    }

    fun searchTweetsByKeyword(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val keyword = fc.dialogflowConversionStep.parameters["keyword"]
            ?: return Mono.just(fc.webhookResponseBuilder)
        val items: Mono<List<List<RichContentItem>>> =
            findTweetsByKeyword(fc.agent.convertToTwitterUserScreenname(), keyword = keyword)

        return convertToTweetPayloadResponse(fc, items)
    }

    fun showLatestTweets(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {
        val items: Mono<List<List<RichContentItem>>> =
            findLatestTweet(fc.agent.convertToTwitterUserScreenname())

        return convertToTweetPayloadResponse(fc, items)
    }

    private fun convertToTweetPayloadResponse(
        fc: FulfillmentChain,
        items: Mono<List<List<RichContentItem>>>
    ): Mono<WebhookResponse.Builder> {
        return items.map { itemsList ->

            if (itemsList.isNotEmpty()) {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(itemsList.take(5)),
                    fc.dialogflowConversionStep,
                    fc.webhookResponseBuilder,
                    prefixMessages = listOf(
                        "I found so many interesting tweets. Here are the most relevant ones.".asIntentMessage()
                    ),
                )
            } else {
                convertCustomPayloadToWebhookResponse(
                    CustomPayload(emptyList()), // only show one in result
                    fc.dialogflowConversionStep,
                    fc.webhookResponseBuilder,
                    prefixMessages = listOf("Upps, currently I cannot find any relevant tweets.".asIntentMessage()),
                )
            }

        }
    }
}
