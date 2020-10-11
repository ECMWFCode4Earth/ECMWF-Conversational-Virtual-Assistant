package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.C3sFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.FulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.c3sActionAsFulfillmentState
import com._2horizon.cva.dialogflow.fulfillment.confluence.ConfluenceFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CommunicationMediaTypeFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusFallbackFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.CopernicusStatusService
import com._2horizon.cva.dialogflow.fulfillment.copernicus.EventsFulfillmentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.context.event.ApplicationEventPublisher
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class C3sDialogflowFulfillmentDispatcher(
    objectMapper: ObjectMapper,
    applicationEventPublisher: ApplicationEventPublisher,
    private val copernicusFS: CopernicusFulfillmentService,
    private val copernicusFallbackFS: CopernicusFallbackFulfillmentService,
    private val copernicusStatusService: CopernicusStatusService,
    private val commsFS: CommunicationMediaTypeFulfillmentService,
    private val confluenceFS: ConfluenceFulfillmentService,
    private val eventsFS: EventsFulfillmentService,
) : AbstractDialogflowFulfillmentDispatcher(applicationEventPublisher, objectMapper) {

    override fun dispatch(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val monoWebhookResponseBuilderResponse: Mono<WebhookResponse.Builder> =
            when (fulfillmentChain.fulfillmentState) {

                C3sFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET -> copernicusFS.datasetSelected(
                    fulfillmentChain
                )
                C3sFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_APPLICATION -> copernicusFS.applicationSelected(
                    fulfillmentChain
                )

                C3sFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD -> confluenceFS.handleSearchByKeyword(
                    fulfillmentChain,
                    "CKB",
                    100
                )

                C3sFulfillmentState.CDS_SHOW_LIVE_STATUS -> copernicusStatusService.showLiveStatus(fulfillmentChain)

                C3sFulfillmentState.PORTAL_SHOW_LATEST_COMMUNICATION_MEDIA_TYPE -> commsFS.showLatestCommunicationMediaType(
                    fulfillmentChain
                )
                C3sFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD -> commsFS.searchMediaTypeByKeyword(
                    fulfillmentChain
                )
                C3sFulfillmentState.PORTAL_LIST_EVENTS -> eventsFS.listEvents(fulfillmentChain)

                C3sFulfillmentState.FALLBACK_GLOBAL -> copernicusFallbackFS.handle(fulfillmentChain)
                C3sFulfillmentState.WELCOME -> Mono.just(fulfillmentChain.webhookResponseBuilder)
                C3sFulfillmentState.NOTHING -> Mono.just(fulfillmentChain.webhookResponseBuilder)
                else -> Mono.error(IllegalStateException("FulfillmentState not implemented ${fulfillmentChain.fulfillmentState}"))
            }
        return monoWebhookResponseBuilderResponse
    }

    override fun actionToStateLookup(action: String): FulfillmentState {

        return if (action.isNotBlank()) {
            c3sActionAsFulfillmentState(action)
        } else {
            C3sFulfillmentState.NOTHING
        }
    }
}


