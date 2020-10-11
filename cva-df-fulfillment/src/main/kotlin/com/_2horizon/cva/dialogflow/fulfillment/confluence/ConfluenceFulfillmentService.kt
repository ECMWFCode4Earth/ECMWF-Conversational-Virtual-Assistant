package com._2horizon.cva.dialogflow.fulfillment.confluence

import com._2horizon.cva.common.confluence.dto.content.ContentResponse
import com._2horizon.cva.common.dialogflow.dto.RichContentDividerItem
import com._2horizon.cva.common.dialogflow.dto.RichContentInfoItem
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import io.micronaut.reactor.http.client.ReactorHttpClient
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-25.
 */
@Singleton
class ConfluenceFulfillmentService(
    @Client("https://confluence.ecmwf.int/rest/api") private val httpClient: ReactorHttpClient,
    private val objectMapper: ObjectMapper
) : AbstractFulfillmentService(objectMapper) {

    fun handleSearchByFallbackKeyword(
        fc: FulfillmentChain,
        space: String,
        size: Int = 15,
        from: Int = 0
    ): Mono<WebhookResponse.Builder> {

        val keyword = fc.dialogflowConversionStep.queryText

        val responseItems = searchByKeyword(keyword = keyword, space = space, size = size, from = from)
            .map { contentResponse ->
                contentResponse.contents.map { content ->
                    listOf(
                        RichContentInfoItem(
                            title = content.title,
                            actionLink = "https://confluence.ecmwf.int/pages/viewpage.action?pageId=${content.id}"
                        ),
                        RichContentDividerItem()
                    )
                }.flatten()
            }


        return responseItems.map { richContentItems ->
            convertRichContentItemToWebhookResponse(
                richContentItems,
                fc.dialogflowConversionStep,
                fc.webhookResponseBuilder,
                listOf("I found the following results in the $space wiki".asIntentMessage())
            )
        }
    }

    fun searchByKeyword(keyword: String, space: String, size: Int = 15, from: Int = 0): Mono<ContentResponse> {

        val uri =
            UriBuilder.of("/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=$space&expand=history,version,metadata.labels&start=${from}&limit=${size}")
                .build()

        return httpClient.retrieve(GET<ContentResponse>(uri), ContentResponse::class.java).single()
    }

    fun handleSearchByKeyword(
        fulfillmentChain: FulfillmentChain,
        space: String,
        size: Int = 15,
        from: Int = 0
    ): Mono<WebhookResponse.Builder> {

        val parameters = fulfillmentChain.dialogflowConversionStep.parameters

        val keyword = parameters["keyword"] ?: return Mono.just(fulfillmentChain.webhookResponseBuilder)

        val responseItems = searchByKeyword(keyword = keyword, space = space, size = size, from = from)
            .map { contentResponse ->
                contentResponse.contents.map { content ->
                    listOf(
                        RichContentInfoItem(
                            title = content.title,
                            actionLink = "https://confluence.ecmwf.int/pages/viewpage.action?pageId=${content.id}"
                        ),
                        RichContentDividerItem()
                    )
                }.flatten()
            }


        return responseItems.map { richContentItems ->
            convertRichContentItemToWebhookResponse(
                richContentItems,
                fulfillmentChain.dialogflowConversionStep,
                fulfillmentChain.webhookResponseBuilder,
                listOf("I found the following results in the $space wiki".asIntentMessage())
            )
        }
    }
}
