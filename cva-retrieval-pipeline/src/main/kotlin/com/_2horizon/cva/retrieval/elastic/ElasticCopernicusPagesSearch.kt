package com._2horizon.cva.retrieval.elastic

import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.COPERNICUS_PAGE_NODES_INDEX
import com._2horizon.cva.common.elastic.ContentSource
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortOrder
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-05.
 */
@Singleton
@Requirements(
    Requires(beans = [RestHighLevelClient::class]),
    Requires(property = "app.feature.ingest-pipeline.elastic-ingest-enabled", value = "true")
)
open class ElasticCopernicusPagesSearch(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun pageNodeNotExists(url: String, nodeType: NodeType, source: ContentSource): Mono<Boolean> {
        return searchPageNodes(url, nodeType,source)
            .map { it.totalHits == 0L }
    }

    fun searchPageNodes(url: String, nodeType: NodeType, source: ContentSource): Mono<CopernicusPageNodeSearchResponse> {

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("url", url))
            .must(QueryBuilders.termQuery("nodeType", nodeType))
            .must(QueryBuilders.termQuery("source", source))

        return executeSearch(query, size = 0, from = 0, listOf(FieldSortBuilder("dateTime").order(SortOrder.DESC)))
    }

    private fun executeSearch(
        query: QueryBuilder,
        size: Int = 3,
        from: Int = 0,
        sortBuilders: List<SortBuilder<*>>
    ): Mono<CopernicusPageNodeSearchResponse> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(from)
            .size(size)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(COPERNICUS_PAGE_NODES_INDEX).apply {
            source(searchSourceBuilder)
        }
        return doSearch(searchRequest)
            .map(::convertSearchResponse)
    }

    private fun doSearch(searchRequest: SearchRequest): Mono<SearchResponse> {
        return Mono.create { sink ->
            try {
                searchAsync(searchRequest, listenerToSink(sink))
            } catch (e: JsonProcessingException) {
                sink.error(e)
            }
        }
    }

    private fun searchAsync(searchRequest: SearchRequest, listener: ActionListener<SearchResponse>) {
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener)
    }

    private fun <T> listenerToSink(sink: MonoSink<T>): ActionListener<T> {
        return object : ActionListener<T> {
            override fun onResponse(response: T) {
                sink.success(response)
            }

            override fun onFailure(e: Exception) {
                sink.error(e)
            }
        }
    }

    private fun convertSearchResponse(searchResponse: SearchResponse): CopernicusPageNodeSearchResponse {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val pageNodes = searchResponse.hits.map { hit ->
            objectMapper.readValue(
                hit.sourceAsString,
                CopernicusPageNode::class.java
            )
        }.toMutableList()
        return CopernicusPageNodeSearchResponse(totalHits = totalHits, pageNodes = pageNodes)
    }
}

data class CopernicusPageNodeSearchResponse(val totalHits: Long, val pageNodes: List<CopernicusPageNode>)
