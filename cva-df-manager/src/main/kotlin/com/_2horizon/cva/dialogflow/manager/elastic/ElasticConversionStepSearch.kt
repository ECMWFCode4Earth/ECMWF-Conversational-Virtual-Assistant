package com._2horizon.cva.dialogflow.manager.elastic

import com._2horizon.cva.common.elastic.DIALOGFLOW_CONVERSION_STEP_INDEX
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
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
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-20.
 */
@Singleton
class ElasticConversionStepSearch(
    private val objectMapper: ObjectMapper,
    private val client: RestHighLevelClient,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun retrieveC3sConversionSessionStats(days: Long): Mono<List<ConversionStep>> {
        return retrieveConversionSessionStats("C3S_CVA", days)
    }

    fun retrieveEcmwfConversionSessionStats(days: Long): Mono<List<ConversionStep>> {
        return retrieveConversionSessionStats("ECMWF_CVA", days)
    }

    fun retrieveConversionSessionStats(agent: String, days: Long): Mono<List<ConversionStep>> {

        return findRecentDialogflowConversionSteps(agent, days)
            .map { r ->

                r.steps.groupBy { it.dateTime.toLocalDate() }
                    .map { dayStepMap ->

                        ConversionStep(
                            localDate = dayStepMap.key,
                            sessions = dayStepMap.value.size,
                            fallbacks = dayStepMap.value.filter { it.intentDisplayName == "Default_Fallback_Intent" }.size
                        )

                    }

            }
    }

    fun findRecentDialogflowConversionSteps(agent: String, days: Long): Mono<DialogflowConversionStepResponse> {

        val startDate = LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val query: QueryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("agent", agent))
            .must(QueryBuilders.rangeQuery("dateTime").gte(startDate))

        return executeSearch(query, size = 10000, from = 0, listOf(FieldSortBuilder("dateTime").order(SortOrder.ASC)))
    }

    private fun executeSearch(
        query: QueryBuilder,
        size: Int = 3,
        from: Int = 0,
        sortBuilders: List<SortBuilder<*>>
    ): Mono<DialogflowConversionStepResponse> {
        val searchSourceBuilder = SearchSourceBuilder()
            .query(query)
            .trackTotalHits(true)
            .from(from)
            .size(size)
            .timeout(TimeValue(3, TimeUnit.SECONDS))

        sortBuilders.forEach { fsb -> searchSourceBuilder.sort(fsb) }

        val searchRequest = SearchRequest(DIALOGFLOW_CONVERSION_STEP_INDEX).apply {
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

    private fun convertSearchResponse(searchResponse: SearchResponse): DialogflowConversionStepResponse {
        val totalHits = searchResponse.hits.totalHits?.value ?: error("totalHits not enabled in query")
        val hits = searchResponse.hits
        val steps = searchResponse.hits.map { hit ->
            objectMapper.readValue(
                hit.sourceAsString,
                DialogflowConversionStep::class.java
            )
        }.toMutableList()
        return DialogflowConversionStepResponse(totalHits = totalHits, steps = steps)
    }
}

data class DialogflowConversionStepResponse(val totalHits: Long, val steps: List<DialogflowConversionStep>)

data class DialogflowConversionStep(
    @JsonProperty("dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val dateTime: LocalDateTime,
    val session: String,
    val action: String?,
    val agent: String,
    val responseId: String,
    val queryText: String?,
    val alternativeQueryResultsCount: Int,
    val intentName: String,
    val intentDisplayName: String,
    val intentDetectionConfidence: Float,

    val outputContexts: List<String> = emptyList(),
    val parameterKeys: List<String> = emptyList(),
    val parameterValues: List<String> = emptyList(),
)

data class ConversionStep(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val localDate: LocalDate,
    val sessions: Int,
    val fallbacks: Int
)
