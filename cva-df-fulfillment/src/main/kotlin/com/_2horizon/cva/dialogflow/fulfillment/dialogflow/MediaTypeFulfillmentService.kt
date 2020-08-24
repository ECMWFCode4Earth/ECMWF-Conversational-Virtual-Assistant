package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.CustomPayload
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentDividerItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentImageItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentInfoItem
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.WebhookRequest
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import com.google.protobuf.util.JsonFormat
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@Singleton
class MediaTypeFulfillmentService(
    private val objectMapper: ObjectMapper,
    private val dfFulfillmentService: DialogflowFulfillmentService

) : AbstractFulfillmentService(objectMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    val pressReleases = listOf(
        MediaTypeItem(
            title = "Copernicus comments on unusual temperatures in Siberia",
            subtitle = "PRESS RELEASE: 17TH JUNE 2020",
            link = "https://climate.copernicus.eu/copernicus-comments-unusual-temperatures-siberia",
            image = "https://climate.copernicus.eu/sites/default/files/styles/preview_fixed_height/public/2020-06/map_ERA5_05_2020_2t_anom_Eurasia.1.png"
        ),
        MediaTypeItem(
            title = "Copernicus tracks no clear sign yet of increase of pollutants across Europe, but this is good news",
            subtitle = "PRESS RELEASE: 11TH JUNE 2020",
            link = "https://climate.copernicus.eu/copernicus-tracks-no-clear-sign-yet-increase-pollutants-across-europe-good-news",
            image = "https://climate.copernicus.eu/sites/default/files/styles/preview_fixed_height/public/2020-06/CAMS%20NO2%20Surface%20Analyses_0.png"
        )
    )

    val newsArticles = listOf(
        MediaTypeItem(
            title = "Lending a hand: climate data for sustainable energy",
            subtitle = "NEWS: 24TH JUNE 2020",
            link = "https://climate.copernicus.eu/index.php/lending-hand-climate-data-sustainable-energy",
            image = "https://climate.copernicus.eu/sites/default/files/styles/preview_fixed_height/public/2020-06/windsolarpower_1_0.jpg"
        ),
        MediaTypeItem(
            title = "Piecing together the puzzle of Europe’s parched spring",
            subtitle = "NEWS: 17TH JUNE 2020",
            link = "https://climate.copernicus.eu/piecing-together-puzzle-europes-parched-spring",
            image = "https://climate.copernicus.eu/sites/default/files/styles/preview_fixed_height/public/2020-06/hero_image_0.jpg"
        )
    )

    override fun handle(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val session = fulfillmentChain.webhookRequest.session
        val action = fulfillmentChain.webhookRequest.queryResult.action
        val webhookRequest = fulfillmentChain.webhookRequest

        return when (action) {
            "tenders.list" -> {
                tendersResponse(webhookRequest,fulfillmentChain.webhookResponseBuilder)
            }
            "show.communication_media_type.list" -> {

                val parametersJson = JsonFormat.printer().print(webhookRequest.queryResult.parameters)
                val mediaTypeParameter = objectMapper.readValue(parametersJson, MediaTypeParameter::class.java)

                when (mediaTypeParameter.communicationMediaType) {
                    "news" -> mediaTypeItemResponse(newsArticles[0],webhookRequest,fulfillmentChain.webhookResponseBuilder)
                    "event" -> nextEventsResponse(webhookRequest,fulfillmentChain.webhookResponseBuilder)
                    "press release" -> mediaTypeItemResponse(pressReleases[0], webhookRequest,fulfillmentChain.webhookResponseBuilder)
                    else -> mediaTypeItemResponse(newsArticles[0], webhookRequest,fulfillmentChain.webhookResponseBuilder)
                }
            }
            "communication_media_typelist.communication_media_typelist-next" -> {

                val parametersJson =
                    JsonFormat.printer().print(webhookRequest.queryResult.getOutputContexts(0).parameters)
                val mediaTypeParameter = objectMapper.readValue(parametersJson, MediaTypeParameter::class.java)


                when (mediaTypeParameter.communicationMediaType) {
                    "news" -> mediaTypeItemResponse(newsArticles[1], webhookRequest,fulfillmentChain.webhookResponseBuilder)
                    "press release" -> mediaTypeItemResponse(pressReleases[1], webhookRequest,fulfillmentChain.webhookResponseBuilder)
                    else -> mediaTypeItemResponse(newsArticles[1], webhookRequest,fulfillmentChain.webhookResponseBuilder)
                }
            }
            else -> {
                error("error in media")
            }
        }
    }

    private fun tendersResponse(webhookRequest: WebhookRequest,webhookResponseBuilder: WebhookResponse.Builder): WebhookResponse.Builder {
        val tenders = listOf(
            RichContentInfoItem(
                title = "COP_057 Prior information notice: Provision of a Contract Lifecycle Management Tool",
                subtitle = "DEADLINE 15th September 2020",
                actionLink = "https://climate.copernicus.eu/cop057-prior-information-notice-provision-contract-lifecycle-management-tool"
            )
        )

        val customPayload = CustomPayload(
            listOf(
                tenders
            )
        )

        val prefixMessages =   Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(listOf("Sure. Here are is the list of currently open tenders")).build()
            ).build()

        return convertCustomPayloadToWebhookResponse(customPayload =customPayload, prefixMessages = listOf(prefixMessages) ,webhookRequest = webhookRequest, webhookResponseBuilder = webhookResponseBuilder)
    }

    private fun nextEventsResponse(webhookRequest: WebhookRequest,webhookResponseBuilder: WebhookResponse.Builder): WebhookResponse.Builder {

        val events = listOf(

            RichContentInfoItem(
                title = "C3S User Learning Services online training event, Finland",
                subtitle = "EVENT: 19th AUG 2020 - 16th SEP 2020",
                actionLink = "https://climate.copernicus.eu/c3s-user-learning-services-online-training-event-finland"
            ),
            RichContentDividerItem(),
            RichContentInfoItem(
                title = "User workshop on Copernicus regional reanalysis for Europe and the European Arctic",
                subtitle = "EVENT: 24th SEP 2020",
                actionLink = "https://climate.copernicus.eu/index.php/user-workshop-copernicus-regional-reanalysis-europe-and-european-arctic"
            ),
            RichContentDividerItem(),
            RichContentInfoItem(
                title = "C3S User Learning Services training event, France",
                subtitle = "EVENT: 25th SEP 2020 - 23th OCT 2020",
                actionLink = "https://climate.copernicus.eu/c3s-user-learning-services-training-event-france"
            )
        )

        val customPayload = CustomPayload(
            listOf(
                events
            )
        )

        val prefixMessages =   Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(listOf("Sure. Here are the upcoming events")).build()
            ).build()

        return convertCustomPayloadToWebhookResponse(customPayload =customPayload, prefixMessages = listOf(prefixMessages),webhookRequest = webhookRequest, webhookResponseBuilder = webhookResponseBuilder )
    }

    private fun mediaTypeItemResponse(
        mediaTypeItem: MediaTypeItem,
        webhookRequest: WebhookRequest,
        webhookResponseBuilder: WebhookResponse.Builder
    ): WebhookResponse.Builder {

        val imageItem = RichContentImageItem(rawUrl = mediaTypeItem.image!!, accessibilityText = mediaTypeItem.title)
        val infoItem = RichContentInfoItem(
            title = mediaTypeItem.title,
            subtitle = mediaTypeItem.subtitle,
            actionLink = mediaTypeItem.link
        )

        val customPayload = CustomPayload(
            listOf(
                listOf(
                    imageItem,
                    infoItem
                )
            )
        )

        return convertCustomPayloadToWebhookResponse(customPayload,webhookRequest,webhookResponseBuilder)
    }


}

data class MediaTypeParameter(
    @JsonProperty("communication_media_type")
    val communicationMediaType: String
)

data class MediaTypeItem(
    val title: String,
    val subtitle: String,
    val link: String,
    val image: String?
)
