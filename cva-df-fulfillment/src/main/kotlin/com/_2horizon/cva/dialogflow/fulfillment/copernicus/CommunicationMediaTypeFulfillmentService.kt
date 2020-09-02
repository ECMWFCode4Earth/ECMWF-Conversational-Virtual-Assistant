package com._2horizon.cva.dialogflow.fulfillment.copernicus

import com._2horizon.cva.common.dialogflow.convertToContentSource
import com._2horizon.cva.common.dialogflow.convertToTwitterUserScreenname
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.CustomPayload
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticMediaTypeSearchService
import com._2horizon.cva.dialogflow.fulfillment.elastic.ElasticTwitterSearchService
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com._2horizon.cva.dialogflow.fulfillment.extensions.convertToRichContentList
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
class CommunicationMediaTypeFulfillmentService(
    private val objectMapper: ObjectMapper,
    private val elasticMediaTypeSearchService: ElasticMediaTypeSearchService,
    private val elasticTwitterSearchService: ElasticTwitterSearchService,
) : AbstractFulfillmentService(objectMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun showLatestCommunicationMediaType(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val communicationMediaType = fulfillmentChain.dialogflowConversionStep.parameters["communication_media_type"]
            ?: return fulfillmentChain.webhookResponseBuilder

        val items = when (communicationMediaType) {
            "news" -> { // main entity id
                elasticMediaTypeSearchService.findLatestNews(fulfillmentChain.agent.convertToContentSource())
                    .map { it.convertToRichContentList() }
            }
            "press release" -> { // main entity id
                elasticMediaTypeSearchService.findLatestPressRelease(fulfillmentChain.agent.convertToContentSource())
                    .map { it.convertToRichContentList() }
            }
            "case study"-> { // main entity id
                elasticMediaTypeSearchService.findLatestCaseStudy(fulfillmentChain.agent.convertToContentSource())
                    .map { it.convertToRichContentList() }
            }
            "demonstrator project" -> { // main entity id
                elasticMediaTypeSearchService.findLatestDemonstratorProject(fulfillmentChain.agent.convertToContentSource())
                    .map { it.convertToRichContentList() }
            }
            "tweet" -> { // main entity id
                elasticTwitterSearchService.findLatestTweet(fulfillmentChain.agent.convertToTwitterUserScreenname())
                    .map { it.convertToRichContentList() }
            }
            else -> {
                TODO()
            }
        }

        return convertCustomPayloadToWebhookResponse(
            CustomPayload(items.take(1)), // only show one in result
            fulfillmentChain.dialogflowConversionStep,
            fulfillmentChain.webhookResponseBuilder,
            prefixMessages = listOf("Wow, I found so many interesting ${communicationMediaType.capitalize()} articles. Here is the most recent one.".asIntentMessage()),
        )
    }

    fun searchMediaTypeByKeyword(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val communicationMediaType = fulfillmentChain.dialogflowConversionStep.parameters["communication_media_type"]
            ?: return fulfillmentChain.webhookResponseBuilder

        val keyword = fulfillmentChain.dialogflowConversionStep.parameters["keyword"]
            ?: return fulfillmentChain.webhookResponseBuilder

        val items = when (communicationMediaType) {
            "news" -> { // main entity id
                elasticMediaTypeSearchService.findNewsByKeyword(fulfillmentChain.agent.convertToContentSource(),keyword)
                    .map { it.convertToRichContentList() }
            }
            "press release" -> { // main entity id
                elasticMediaTypeSearchService.findPressReleaseByKeyword(fulfillmentChain.agent.convertToContentSource(),keyword)
                    .map { it.convertToRichContentList() }
            }
            "case study" -> { // main entity id
                elasticMediaTypeSearchService.findCaseStudyByKeyword(fulfillmentChain.agent.convertToContentSource(),keyword)
                    .map { it.convertToRichContentList() }
            }
            "demonstrator project" -> { // main entity id
                elasticMediaTypeSearchService.findDemonstratorProjectByKeyword(fulfillmentChain.agent.convertToContentSource(),keyword)
                    .map { it.convertToRichContentList() }
            }
            "tweet" -> { // main entity id
                elasticTwitterSearchService.findTweetByKeyword(fulfillmentChain.agent.convertToTwitterUserScreenname(),keyword)
                    .map { it.convertToRichContentList() }
            }

            "event" -> { // main entity id
                TODO("event not setup yet")
            }

            else -> {
                TODO()
            }
        }

        return if (items.isEmpty()) {
            convertCustomPayloadToWebhookResponse(
                CustomPayload(listOf()), // only show one in result
                fulfillmentChain.dialogflowConversionStep,
                fulfillmentChain.webhookResponseBuilder,
                prefixMessages = listOf("Upps, currently I cannot find any relevant ${communicationMediaType.capitalize()}.".asIntentMessage()),
            )
        }   else {
            convertCustomPayloadToWebhookResponse(
                CustomPayload(items.take(1)), // only show one in result
                fulfillmentChain.dialogflowConversionStep,
                fulfillmentChain.webhookResponseBuilder,
                prefixMessages = listOf("Wow, I found so many interesting ${communicationMediaType.capitalize()}s. Here is the most recent one.".asIntentMessage()),
            )
        }




        return fulfillmentChain.webhookResponseBuilder
    }


}
