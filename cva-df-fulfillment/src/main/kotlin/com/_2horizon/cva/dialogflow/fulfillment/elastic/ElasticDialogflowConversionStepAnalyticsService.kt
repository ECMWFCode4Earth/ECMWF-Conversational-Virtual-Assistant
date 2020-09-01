package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.elastic.DIALOGFLOW_CONVERSION_STEP_INDEX
import com._2horizon.cva.dialogflow.fulfillment.analytics.DialogflowConversionStep
import com._2horizon.cva.dialogflow.fulfillment.event.DialogflowConversionStepEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
@Requirements(
    Requires(beans = [RestHighLevelClient::class]),
    Requires(property = "app.feature.fulfillment.analytics.dialogflow-conversion-step", value = "true")
)
class ElasticDialogflowConversionStepAnalyticsService(
    private val objectMapper: ObjectMapper,
    private val client: RestHighLevelClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onDialogflowConversionStepEvent(event: DialogflowConversionStepEvent) {
        val dialogflowConversionStep: DialogflowConversionStep = event.dialogflowConversionStep

        val request = IndexRequest(DIALOGFLOW_CONVERSION_STEP_INDEX).id(dialogflowConversionStep.responseId)
        request.source(objectMapper.writeValueAsString(dialogflowConversionStep), XContentType.JSON)

        client.indexAsync(request, RequestOptions.DEFAULT, object : ActionListener<IndexResponse> {
            override fun onResponse(response: IndexResponse) {
                //do nothing
            }

            override fun onFailure(e: Exception) {
                log.warn("Couldn't index DIALOGFLOW_CONVERSION_STEP because ${e.message}")
            }
        })
    }
}
