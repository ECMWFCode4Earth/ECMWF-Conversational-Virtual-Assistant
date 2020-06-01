package com._2horizon.cva.retrieval.elastic.config

import fr.pilato.elasticsearch.tools.ElasticsearchBeyonder
import fr.pilato.elasticsearch.tools.SettingsFinder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.ssl.SSLContexts
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient

/**
 * Created by Frank Lieber (liefra) on 2019-03-28.
 */
@Requirements(
    Requires(beans = [RestHighLevelClient::class]),
    Requires(property = "app.feature.ingest-pipeline.elastic-ingest-enabled", notEquals = "false")
)
@Factory
class ElasticFactory(
    @param:Value("\${app.elastic.host}") val elasticHost: String,
    @param:Value("\${app.elastic.port}") val elasticPort: Int,
    @param:Value("\${app.elastic.scheme}") val elasticScheme: String,
    @param:Value("\${app.elastic.username}") val elasticUsername: String?,
    @param:Value("\${app.elastic.password}") val elasticPassword: String?,
    @param:Value("\${app.elastic.create-schema}") val createSchema: Boolean
) {

    @Bean(preDestroy = "close")
    fun elasticClient(): RestHighLevelClient {

        val restClient = if (elasticUsername != null && elasticPassword != null) {
            RestHighLevelClient(
                RestClient.builder(
                    HttpHost(elasticHost, elasticPort, elasticScheme)
                ).setHttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(BasicCredentialsProvider().apply {
                        setCredentials(AuthScope.ANY, UsernamePasswordCredentials(elasticUsername, elasticPassword))
                    })

                    if (elasticScheme == "https") {
                        //TODO: don't use this in prod
                        val acceptingTrustStrategy = TrustStrategy { cert, authType -> true }
                        val sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build()
                        httpClientBuilder.setSSLContext(sslContext)
                        httpClientBuilder.setSSLHostnameVerifier { _, _ -> true }
                    }

                    httpClientBuilder
                }
            )
        } else {
            RestHighLevelClient(
                RestClient.builder(
                    HttpHost(elasticHost, elasticPort, elasticScheme)
                )
            )
        }

        if (createSchema) {
            ElasticsearchBeyonder.start(
                restClient.lowLevelClient,
                SettingsFinder.Defaults.ConfigDir,
                SettingsFinder.Defaults.MergeMappings,
                SettingsFinder.Defaults.ForceCreation
            )
        }

        return restClient
    }
}
