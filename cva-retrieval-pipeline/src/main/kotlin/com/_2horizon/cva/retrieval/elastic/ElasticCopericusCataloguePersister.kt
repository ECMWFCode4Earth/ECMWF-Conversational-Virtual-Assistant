package com._2horizon.cva.retrieval.elastic

import com._2horizon.cva.retrieval.event.CdsCatalogueReceivedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
@Singleton
@Requires(property = "app.feature.ingest-pipeline.elastic-ingest-enabled", value = "true")
open class ElasticCopericusCataloguePersister(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    val copericusCatalogue = "copernicus-catalogue"

    @EventListener
    @Async
    open fun cdsCatalogueReceivedEvent(cdsCatalogueReceivedEvent: CdsCatalogueReceivedEvent) {
        log.info("CdsCatalogueReceivedEvent received")

        val uiResources = cdsCatalogueReceivedEvent.uiResources
        val bulkRequest = BulkRequest()
        uiResources.forEach { uiResource ->
            val request = IndexRequest(copericusCatalogue).id(uiResource.id)
            request.source(objectMapper.writeValueAsString(uiResource), XContentType.JSON)
            bulkRequest.add(request)
        }
        
        val bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT)
        log.info("Got bulkResponse with item size ${bulkResponse.items.size}")
    }
}
