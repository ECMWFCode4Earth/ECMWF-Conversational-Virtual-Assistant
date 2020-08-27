package com._2horizon.cva.dialogflow.fulfillment.confluence

import com._2horizon.cva.common.confluence.dto.content.ContentResponse
import com._2horizon.cva.dialogflow.fulfillment.AbstractFulfillmentService
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.FulfillmentChain
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentAccordionItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentDividerItem
import com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto.RichContentInfoItem
import com._2horizon.cva.dialogflow.fulfillment.extensions.asIntentMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.WebhookResponse
import org.slf4j.LoggerFactory
import java.net.URL
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-25.
 */
@Singleton
class ConfluenceFulfillmentService(
    private val objectMapper: ObjectMapper
) : AbstractFulfillmentService(objectMapper) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun handleSearchByKeyword(fulfillmentChain: FulfillmentChain): WebhookResponse.Builder {

        val parameters = fulfillmentChain.dialogflowConversionStep.parameters

        val keyword = parameters["any"] ?: error("any parameter couldn't be found in parameters")

        val richContentItems = if (keyword == "macos") {
            val contentResponse = searchByKeywordWithBody(keyword)
            contentResponse.contents.map { content ->
                listOf(
                    RichContentAccordionItem(
                        title = content.title, text = """
                    ${content.body!!.view.value}
                    <br><br> 
                    <a href="https://confluence.ecmwf.int/pages/viewpage.action?pageId=${content.id}">Visit the CKB page ${content.title}</a>
                """.trimIndent()
                    ),
                    RichContentDividerItem()
                )
            }.flatten()

        } else {
            val contentResponse = searchByKeyword(keyword)
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

        return convertRichContentItemToWebhookResponse(

            richContentItems,
            fulfillmentChain.dialogflowConversionStep,
            fulfillmentChain.webhookResponseBuilder,
            listOf("I found the following results in the CKB".asIntentMessage())
        )
    }

    fun searchByKeyword(keyword: String): ContentResponse {

        val json =
            URL("https://confluence.ecmwf.int/rest/api/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=CKB&expand=history,version,metadata.labels&start=0&limit=8").readText()
        val contentResponse = objectMapper.readValue(json, ContentResponse::class.java)
        return contentResponse
    }

    fun searchByKeywordWithBody(keyword: String): ContentResponse {

        val json =
            URL("https://confluence.ecmwf.int/rest/api/content/search?cql=text~{%22$keyword%22}+and+type=page+and+space=CKB&expand=history,version,metadata.labels,body.view,body.storage&start=0&limit=8").readText()
        val contentResponse = objectMapper.readValue(json, ContentResponse::class.java)
        return contentResponse
    }
}
