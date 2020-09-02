package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.copernicus.dto.CopernicusPageNode
import com._2horizon.cva.common.copernicus.dto.NodeType
import com._2horizon.cva.common.elastic.COPERNICUS_PAGE_NODES_INDEX
import com._2horizon.cva.common.elastic.ContentSource
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.rest.RestStatus
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.ScoreSortBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortOrder
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

    fun findLatestPressRelease(cs: ContentSource): List<CopernicusPageNode> {
        return findLatestMediaType(cs, NodeType.PRESS_RELEASE)
    }

    fun findLatestNews(cs: ContentSource): List<CopernicusPageNode> {
        return findLatestMediaType(cs, NodeType.NEWS)
    }

    fun findLatestCaseStudy(cs: ContentSource): List<CopernicusPageNode>{
        return findLatestMediaType(cs, NodeType.CASE_STUDY)
    }

    fun findLatestDemonstratorProject(cs: ContentSource): List<CopernicusPageNode> {
        return findLatestMediaType(cs, NodeType.DEMONSTRATOR_PROJECT)
    }

    fun findPressReleaseByKeyword(cs: ContentSource, keyword: String): List<CopernicusPageNode> {
        return findMediaTypeByKeyword(cs, NodeType.PRESS_RELEASE, keyword)
    }

    fun findNewsByKeyword(cs: ContentSource, keyword: String): List<CopernicusPageNode> {
        return findMediaTypeByKeyword(cs, NodeType.NEWS, keyword)
    }

    fun findCaseStudyByKeyword(cs: ContentSource, keyword: String): List<CopernicusPageNode> {
        return findMediaTypeByKeyword(cs, NodeType.CASE_STUDY, keyword)
    }

    fun findDemonstratorProjectByKeyword(cs: ContentSource, keyword: String): List<CopernicusPageNode> {
        return findMediaTypeByKeyword(cs, NodeType.DEMONSTRATOR_PROJECT, keyword)
    }

    private fun findMediaTypeByKeyword(
        cs: ContentSource,
        nodeType: NodeType,
        keyword: String
    ): List<CopernicusPageNode> {

        val matchQuery = QueryBuilders.matchQuery("content", keyword)
            .fuzziness(Fuzziness.AUTO)
            .prefixLength(3)
            .maxExpansions(10)

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("source", cs))
            .must(QueryBuilders.termQuery("nodeType", nodeType))
            .must(matchQuery)

        return executeSearch(query,listOf(getScoreSort(), getDateTimeSort()))
    }

    private fun findLatestMediaType(cs: ContentSource, nodeType: NodeType): List<CopernicusPageNode> {
        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("source", cs))
            .must(QueryBuilders.termQuery("nodeType", nodeType))

        return executeSearch(query, listOf(getDateTimeSort()))
    }

    private fun executeSearch(query: QueryBuilder, sortBuilders: List<SortBuilder<*>>): List<CopernicusPageNode> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(0)
            .size(3)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(COPERNICUS_PAGE_NODES_INDEX).apply {
            source(searchSourceBuilder)
        }
        val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)

        check(searchResponse.status() == RestStatus.OK) { "Wrong Elastic result ${searchResponse.status()}" }

        return convertSearchResponse(searchResponse)
    }

    private fun convertSearchResponse(searchResponse: SearchResponse): List<CopernicusPageNode> {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val results = searchResponse.hits.map { hit ->
            objectMapper.readValue(
                hit.sourceAsString,
                CopernicusPageNode::class.java
            )
        }
        return results
    }

    private fun getScoreSort(sort: SortOrder = SortOrder.DESC) = ScoreSortBuilder().order(sort)
    private fun getDateTimeSort(sort: SortOrder = SortOrder.DESC) = FieldSortBuilder("dateTime").order(sort)

}
