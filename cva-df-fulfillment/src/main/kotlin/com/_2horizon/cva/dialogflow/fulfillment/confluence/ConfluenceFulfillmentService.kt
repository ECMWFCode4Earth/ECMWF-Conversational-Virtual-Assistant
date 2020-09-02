package com._2horizon.cva.dialogflow.fulfillment.confluence

import com._2horizon.cva.common.confluence.dto.content.ContentResponse
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentDividerItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentInfoItem
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import io.micronaut.reactor.http.client.ReactorHttpClient
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(javaClass)

    fun searchByKeyword(keyword: String): Mono<ContentResponse> {

        val uri =
            UriBuilder.of("/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=CKB&expand=history,version,metadata.labels&start=0&limit=8")
                .build()

        return httpClient.retrieve(GET<ContentResponse>(uri), ContentResponse::class.java).single()
    }

    fun handleSearchByKeyword(fulfillmentChain: FulfillmentChain): Mono<WebhookResponse.Builder> {

        val parameters = fulfillmentChain.dialogflowConversionStep.parameters

        val keyword = parameters["keyword"] ?: return Mono.just(fulfillmentChain.webhookResponseBuilder)

        val responseItems = searchByKeyword(keyword)
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
                listOf("I found the following results in the CKB".asIntentMessage())
            )
        }
    }

    // fun searchByKeyword(keyword: String): ContentResponse {
    //
    //     val json =
    //         URL("https://confluence.ecmwf.int/rest/api/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=CKB&expand=history,version,metadata.labels&start=0&limit=8").readText()
    //     val contentResponse = objectMapper.readValue(json, ContentResponse::class.java)
    //     return contentResponse
    // }
    //
    // fun searchByKeywordWithBody(keyword: String): ContentResponse {
    //
    //     val json =
    //         URL("https://confluence.ecmwf.int/rest/api/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=CKB&expand=history,version,metadata.labels,body.view,body.storage&start=0&limit=8").readText()
    //     val contentResponse = objectMapper.readValue(json, ContentResponse::class.java)
    //     return contentResponse
    // }
}
