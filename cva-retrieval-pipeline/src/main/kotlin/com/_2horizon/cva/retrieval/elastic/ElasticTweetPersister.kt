package com._2horizon.cva.retrieval.elastic

import com._2horizon.cva.common.elastic.TWITTER_INDEX
import com._2horizon.cva.common.twitter.dto.Tweet
import com._2horizon.cva.retrieval.event.TwitterBulkStatusEvent
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
open class ElasticTweetPersister(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    open fun twitterBulkStatusEventReceived(twitterBulkStatusEvent: TwitterBulkStatusEvent) {
        log.info("TwitterBulkStatusEvent received")

        val tweets = twitterBulkStatusEvent.tweets
        val bulkRequest = BulkRequest()
        tweets.forEach { tweet: Tweet ->
            val request = IndexRequest(TWITTER_INDEX).id(tweet.id.toString())
            request.source(objectMapper.writeValueAsString(tweet), XContentType.JSON)
            bulkRequest.add(request)

        }

        val bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT)
        log.info("Got bulkResponse with item size ${bulkResponse.items.size}. HasFailures ${bulkResponse.hasFailures()}")
    }
}
