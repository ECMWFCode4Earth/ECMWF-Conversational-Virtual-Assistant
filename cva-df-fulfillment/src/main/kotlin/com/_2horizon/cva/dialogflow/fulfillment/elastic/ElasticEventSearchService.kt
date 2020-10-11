package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.copernicus.dto.CopernicusEventNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.COPERNICUS_EVENT_NODES_INDEX
import com._2horizon.cva.common.elastic.ContentSource
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
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
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
class ElasticEventSearchService(
    private val objectMapper: ObjectMapper,
    private val client: RestHighLevelClient,
) {

    fun findUpcomingEvents(cs: ContentSource): Mono<CopernicusEventNodeResponse> {
        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("source", cs))
            .must(QueryBuilders.termQuery("nodeType", NodeType.EVENT))
            .must(
                QueryBuilders.rangeQuery("startDate")
                    .gte(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            )

        return executeSearch(query, size = 3, from = 0, listOf(FieldSortBuilder("dateTime").order(SortOrder.ASC)))
    }

    private fun executeSearch(
        query: QueryBuilder,
        size: Int = 3,
        from: Int = 0,
        sortBuilders: List<SortBuilder<*>>
    ): Mono<CopernicusEventNodeResponse> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(from)
            .size(size)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(COPERNICUS_EVENT_NODES_INDEX).apply {
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

    private fun convertSearchResponse(searchResponse: SearchResponse): CopernicusEventNodeResponse {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val eventNodes = searchResponse.hits.map { hit ->
            objectMapper.readValue(
                hit.sourceAsString,
                CopernicusEventNode::class.java
            )
        }.toMutableList()
        return CopernicusEventNodeResponse(totalHits = totalHits, eventNodes = eventNodes)
    }
}

data class CopernicusEventNodeResponse(val totalHits: Long, val eventNodes: MutableList<CopernicusEventNode>)
