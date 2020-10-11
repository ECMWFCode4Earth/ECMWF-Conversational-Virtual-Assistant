package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.elastic.TWITTER_INDEX
import com._2horizon.cva.common.twitter.dto.Tweet
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
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
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

    private fun convertSearchResponse(searchResponse: SearchResponse): TweetResponse {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val tweets = searchResponse.hits.map { hit -> objectMapper.readValue(hit.sourceAsString, Tweet::class.java) }
        return TweetResponse(totalHits = totalHits, tweets = tweets)
    }

    fun findLatestTweet(userScreenName: String): Mono<TweetResponse> {
        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("userScreenName", userScreenName))

        return executeSearch(query, size = 3, from = 0, listOf(getDateTimeSort()))
    }

    fun findTweetByKeyword(userScreenName: String, keyword: String, size: Int = 3, from: Int = 0): Mono<TweetResponse> {

        val matchQuery = QueryBuilders.matchQuery("content", keyword)
            .fuzziness(Fuzziness.AUTO)
            .prefixLength(3)
            .maxExpansions(10)

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("userScreenName", userScreenName))
            .must(matchQuery)

        return executeSearch(query, size = size, from = from, listOf(getDateTimeSort()))
    }

    private fun executeSearch(
        query: QueryBuilder,
        size: Int,
        from: Int,
        sortBuilders: List<SortBuilder<*>>
    ): Mono<TweetResponse> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(from)
            .size(size)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(TWITTER_INDEX).apply {
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

    private fun getScoreSort(sort: SortOrder = SortOrder.DESC) = ScoreSortBuilder().order(sort)
    private fun getDateTimeSort(sort: SortOrder = SortOrder.DESC) = FieldSortBuilder("dateTime").order(sort)
}

data class TweetResponse(val totalHits: Long, val tweets: List<Tweet>)
