package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.EcmwfFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.confluence.ConfluenceFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.ecmwf.EcmwfFallbackFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications.EcmwfPublicationsFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.ecmwfActionAsFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.twitter.TwitterFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.context.event.ApplicationEventPublisher
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class EcmwfDialogflowFulfillmentDispatcher(
    objectMapper: ObjectMapper,
    applicationEventPublisher: ApplicationEventPublisher,
    private val ecmwfFallbackFS: EcmwfFallbackFulfillmentService,
    private val twitterFS: TwitterFulfillmentService,
    private val confluenceFS: ConfluenceFulfillmentService,
    private val ecmwfPublicationsFS: EcmwfPublicationsFulfillmentService,
) : AbstractDialogflowFulfillmentDispatcher(applicationEventPublisher, objectMapper) {

    override fun dispatch(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {
        return when (fc.fulfillmentState) {
            EcmwfFulfillmentState.FALLBACK_GLOBAL -> ecmwfFallbackFS.handle(fc)
            EcmwfFulfillmentState.NOTHING -> Mono.just(fc.webhookResponseBuilder)
            EcmwfFulfillmentState.WELCOME -> Mono.just(fc.webhookResponseBuilder)
            EcmwfFulfillmentState.SEARCH_TWEETS_BY_KEYWORD -> twitterFS.searchTweetsByKeyword(fc)
            EcmwfFulfillmentState.SHOW_LATEST_TWEET -> twitterFS.showLatestTweets(fc)
            EcmwfFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD -> confluenceFS.handleSearchByKeyword(fc, "UDOC", 15)
            EcmwfFulfillmentState.SEARCH_PUBLICATIONS_BY_PUBLICATION_TYPE -> ecmwfPublicationsFS.handle(fc)
            EcmwfFulfillmentState.QUERY_USER_INPUT_FOR_UDOC_SEARCH_KEYWORD -> confluenceFS.handleSearchByFallbackKeyword(
                fc,
                "UDOC",
                15
            )

            else -> Mono.error(IllegalStateException("FulfillmentState not implemented ${fc.fulfillmentState}"))
        }
    }

    override fun actionToStateLookup(action: String): FulfillmentState {
        return if (action.isNotBlank()) {
            ecmwfActionAsFulfillmentState(action)
        } else {
            EcmwfFulfillmentState.NOTHING
        }
    }
}

