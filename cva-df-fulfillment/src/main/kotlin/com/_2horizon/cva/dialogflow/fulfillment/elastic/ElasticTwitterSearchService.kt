package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.elastic.TWITTER_INDEX
import com._2horizon.cva.common.twitter.dto.Tweet
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
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-28.
 */
@Singleton
class ElasticTwitterSearchService(
    private val objectMapper: ObjectMapper,
    private val client: RestHighLevelClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private fun convertSearchResponse(searchResponse: SearchResponse): List<Tweet> {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val tweets = searchResponse.hits.map { hit -> objectMapper.readValue(hit.sourceAsString, Tweet::class.java) }
        return tweets
    }

    fun findLatestTweet(userScreenName: String): List<Tweet> {
        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("userScreenName", userScreenName))

        return executeSearch(query, listOf(getDateTimeSort()))
    }

    fun findTweetByKeyword(userScreenName: String, keyword: String): List<Tweet> {

        val matchQuery = QueryBuilders.matchQuery("content", keyword)
            .fuzziness(Fuzziness.AUTO)
            .prefixLength(3)
            .maxExpansions(10)

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("userScreenName", userScreenName))
            .must(matchQuery)


        return executeSearch(query, listOf(getScoreSort(), getDateTimeSort()))
    }

    private fun executeSearch(query: QueryBuilder, sortBuilders: List<SortBuilder<*>>): List<Tweet> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(0)
            .size(10)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(TWITTER_INDEX).apply {
            source(searchSourceBuilder)
        }
        val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)

        check(searchResponse.status() == RestStatus.OK) { "Wrong Elastic result ${searchResponse.status()}" }

        return convertSearchResponse(searchResponse)
    }

    private fun getScoreSort(sort: SortOrder = SortOrder.DESC) = ScoreSortBuilder().order(sort)
    private fun getDateTimeSort(sort: SortOrder = SortOrder.DESC) = FieldSortBuilder("dateTime").order(sort)
}
