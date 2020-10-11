package com._2horizon.cva.retrieval.elastic

import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.COPERNICUS_EVENT_NODES_INDEX
import com._2horizon.cva.common.elastic.COPERNICUS_PAGE_NODES_INDEX
import com._2horizon.cva.retrieval.event.ContentPageNodesEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-05.
 */
@Singleton
@Requirements(
    Requires(beans = [RestHighLevelClient::class]),
    Requires(property = "app.feature.ingest-pipeline.elastic-ingest-enabled", value = "true")
)
open class ElasticCopernicusPagesPersister(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    open fun contentPageNodesEventReceived(contentPageNodesEvent: ContentPageNodesEvent) {
        log.info("ContentPageNodesEvent received")

        val bulkRequest = if (contentPageNodesEvent.nodeType == NodeType.EVENT) {
            createBulkRequest(contentPageNodesEvent.itemCopernicuses, COPERNICUS_EVENT_NODES_INDEX)
        } else {
            createBulkRequest(contentPageNodesEvent.itemCopernicuses, COPERNICUS_PAGE_NODES_INDEX)
        }
        val bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT)
        log.info("Got bulkResponse with item size ${bulkResponse.items.size}. HasFailures ${bulkResponse.hasFailures()}")
    }

    private fun createBulkRequest(items: List<CopernicusPageNode>, indexName: String): BulkRequest {

        val bulkRequest = BulkRequest()
        items.forEach { node: CopernicusPageNode ->
            val request = IndexRequest(indexName).id(node.id)
            request.source(objectMapper.writeValueAsString(node), XContentType.JSON)
            bulkRequest.add(request)
        }
        return bulkRequest
    }
}
