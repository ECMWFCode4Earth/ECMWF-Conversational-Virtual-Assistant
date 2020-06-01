package com._2horizon.cva.retrieval.elastic.config

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.health.HealthStatus
import io.micronaut.health.HealthStatus.DOWN
import io.micronaut.health.HealthStatus.UP
import io.micronaut.management.endpoint.health.HealthEndpoint
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.cluster.health.ClusterHealthStatus.GREEN
import org.elasticsearch.cluster.health.ClusterHealthStatus.YELLOW
import org.elasticsearch.common.Strings
import org.elasticsearch.common.xcontent.ToXContent.MapParams
import org.elasticsearch.common.xcontent.XContentFactory
import org.reactivestreams.Publisher
import java.io.IOException
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-04-29.
 */
@Requirements(
    Requires(beans = [HealthEndpoint::class]),
    Requires(property = HealthEndpoint.PREFIX + ".elasticsearch.rest.high.level.enabled", notEquals = "false")
)
@Singleton
class ElasticsearchHealthIndicator(
    private val esClient: RestHighLevelClient
) : HealthIndicator {

    private val NAME = "elasticsearch"

    override fun getResult(): Publisher<HealthResult> {
        return Publisher<HealthResult> { subscriber ->
            esClient.cluster().healthAsync(
                ClusterHealthRequest(),
                RequestOptions.DEFAULT,
                object : ActionListener<ClusterHealthResponse> {

                    private val resultBuilder = HealthResult.builder(NAME)

                    override fun onFailure(e: java.lang.Exception?) {
                        subscriber.onNext(resultBuilder.status(DOWN).exception(e).build())
                        subscriber.onComplete()
                    }

                    override fun onResponse(response: ClusterHealthResponse) {
                        val result = try {
                            resultBuilder
                                .status(healthResultStatus(response))
                                .details(healthResultDetails(response))
                                .build()
                        } catch (e: IOException) {
                            resultBuilder.status(DOWN).exception(e).build()
                        }
                        subscriber.onNext(result)
                        subscriber.onComplete()
                    }
                }
            )

        }
    }

    private fun healthResultDetails(response: ClusterHealthResponse): String {
        val xContentBuilder = XContentFactory.jsonBuilder()
        response.toXContent(xContentBuilder, MapParams(emptyMap()))
        return Strings.toString(xContentBuilder)
    }

    private fun healthResultStatus(response: ClusterHealthResponse): HealthStatus? {
        return if (response.status == GREEN || response.status == YELLOW) UP else DOWN
    }
}
