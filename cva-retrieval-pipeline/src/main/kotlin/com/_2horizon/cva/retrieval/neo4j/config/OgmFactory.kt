package com._2horizon.cva.retrieval.neo4j.config

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory

/**
 * Created by Frank Lieber (liefra) on 2020-04-30.
 */
@Requirements(
    Requires(classes = [SessionFactory::class]),
    Requires(property = "app.feature.ingest-pipeline.neo4j-ingest-enabled", value = "true")
)
@Factory
class OgmFactory(
    @Value("\${neo4j.uri}") val neo4jUri: String,
    @Value("\${neo4j.username}") val neo4jUsername: String,
    @Value("\${neo4j.password}") val neo4jPassword: String
) {

    private fun neo4jOgmConfiguration(): Configuration {
        return Configuration.Builder().credentials(neo4jUsername, neo4jPassword)
            .useNativeTypes()
            .uri(neo4jUri)
            .build()
    }


    @Bean(preDestroy = "close")
    fun sessionFactory(): SessionFactory {
        return SessionFactory(neo4jOgmConfiguration(), "com._2horizon.cva.retrieval.neo4j.domain")
    }
}



