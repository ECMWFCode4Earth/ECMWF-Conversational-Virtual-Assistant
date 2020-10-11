package com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications

import com._2horizon.cva.common.dialogflow.dto.RichContentButtonItem
import com._2horizon.cva.common.dialogflow.dto.RichContentIcon
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-10-03.
 */
@Singleton
class EcmwfPublicationsFulfillmentService(
    objectMapper: ObjectMapper,
) : AbstractFulfillmentService(objectMapper) {

    fun handle(fc: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val publicationType = fc.dialogflowConversionStep.parameters["publication_type"]
            ?: return Mono.just(fc.webhookResponseBuilder)

        val url = if (publicationType == "IFS documentation") {
            "https://www.ecmwf.int/en/publications/ifs-documentation"
        } else {
            "https://www.ecmwf.int/en/publications/search/?f%5B0%5D=sm_biblio_type%3A$publicationType"
        }

        return publicationTypeForwardPage(fc, publicationType = publicationType, url = url)
    }

    private fun publicationTypeForwardPage(
        fulfillmentChain: FulfillmentChain,
        publicationType: String,
        url: String
    ): Mono<WebhookResponse.Builder> {
        val button = RichContentButtonItem(
            text = "Click to visit the $publicationType publications page",
            link = url,
            RichContentIcon(type = "chevron_right", color = "#FF9800")
        )
        val r = convertRichContentItemToWebhookResponse(
            listOf(button),
            fulfillmentChain.dialogflowConversionStep,
            fulfillmentChain.webhookResponseBuilder,
        )
        return Mono.just(r)
    }
}
