package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.COPERNICUS_PAGE_NODES_INDEX
import com._2horizon.cva.common.elastic.ContentSource
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.ScoreSortBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortOrder
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-01.
 */
@Singleton
class ElasticMediaTypeSearchService(
    private val objectMapper: ObjectMapper,
    private val client: RestHighLevelClient,
) {

    fun findLatestPressRelease(cs: ContentSource): Mono<CopernicusPageNodeResponse> {
        return findLatestMediaType(cs, NodeType.PRESS_RELEASE)
    }

    fun findLatestNews(cs: ContentSource): Mono<CopernicusPageNodeResponse> {
        return findLatestMediaType(cs, NodeType.NEWS)
    }

    fun findLatestCaseStudy(cs: ContentSource): Mono<CopernicusPageNodeResponse> {
        return findLatestMediaType(cs, NodeType.CASE_STUDY)
    }

    fun findLatestDemonstratorProject(cs: ContentSource): Mono<CopernicusPageNodeResponse> {
        return findLatestMediaType(cs, NodeType.DEMONSTRATOR_PROJECT)
    }

    fun findPressReleaseByKeyword(
        cs: ContentSource,
        keyword: String,
        size: Int = 3,
        from: Int = 0
    ): Mono<CopernicusPageNodeResponse> {
        return findMediaTypeByKeyword(cs, NodeType.PRESS_RELEASE, keyword, size = size, from = from)
    }

    fun findNewsByKeyword(
        cs: ContentSource,
        keyword: String,
        size: Int = 3,
        from: Int = 0
    ): Mono<CopernicusPageNodeResponse> {
        return findMediaTypeByKeyword(cs, NodeType.NEWS, keyword, size = size, from = from)
    }

    fun findCaseStudyByKeyword(
        cs: ContentSource,
        keyword: String,
        size: Int = 3,
        from: Int = 0
    ): Mono<CopernicusPageNodeResponse> {
        return findMediaTypeByKeyword(cs, NodeType.CASE_STUDY, keyword, size = size, from = from)
    }

    fun findDemonstratorProjectByKeyword(
        cs: ContentSource,
        keyword: String,
        size: Int = 3,
        from: Int = 0
    ): Mono<CopernicusPageNodeResponse> {
        return findMediaTypeByKeyword(cs, NodeType.DEMONSTRATOR_PROJECT, keyword, size = size, from = from)
    }

    private fun findMediaTypeByKeyword(
        cs: ContentSource,
        nodeType: NodeType,
        keyword: String, size: Int = 3, from: Int = 0
    ): Mono<CopernicusPageNodeResponse> {

        val matchQuery = QueryBuilders.matchQuery("content", keyword)
            .fuzziness(Fuzziness.AUTO)
            .prefixLength(3)
            .maxExpansions(10)

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("source", cs))
            .must(QueryBuilders.termQuery("nodeType", nodeType))
            .must(matchQuery)

        return executeSearch(query, size = size, from = from, listOf(getScoreSort(), getDateTimeSort()))
    }

    private fun findLatestMediaType(cs: ContentSource, nodeType: NodeType): Mono<CopernicusPageNodeResponse> {
        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("source", cs))
            .must(QueryBuilders.termQuery("nodeType", nodeType))

        return executeSearch(query, size = 1, from = 0, listOf(getDateTimeSort()))
    }

    private fun executeSearch(
        query: QueryBuilder,
        size: Int = 3,
        from: Int = 0,
        sortBuilders: List<SortBuilder<*>>
    ): Mono<CopernicusPageNodeResponse> {
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

    private fun convertSearchResponse(searchResponse: SearchResponse): CopernicusPageNodeResponse {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val pageNodes = searchResponse.hits.map { hit ->
            objectMapper.readValue(
                hit.sourceAsString,
                CopernicusPageNode::class.java
            )
        }
        return CopernicusPageNodeResponse(totalHits = totalHits, pageNodes = pageNodes)
    }

    private fun getScoreSort(sort: SortOrder = SortOrder.DESC) = ScoreSortBuilder().order(sort)
    private fun getDateTimeSort(sort: SortOrder = SortOrder.DESC) = FieldSortBuilder("dateTime").order(sort)
}

data class CopernicusPageNodeResponse(val totalHits: Long, val pageNodes: List<CopernicusPageNode>)
