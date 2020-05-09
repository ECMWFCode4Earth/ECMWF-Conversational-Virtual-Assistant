package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import java.util.Optional
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
class ConfluenceSpacesRetriever(
    private val confluenceOperations: ConfluenceOperations,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.enabled:false}") private val retrievalPipelineEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalPipelineEnabled) {
            retrieveSpaces()
        }
    }

    private fun retrieveSpaces(): SpacesResponse {
        val spacesResponse = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon()
            .orElseThrow { error("Couldn't retrieve spaces") }
        log.debug("Got ${spacesResponse.size} spaces")

        applicationEventPublisher.publishEvent(spacesResponse)

        return spacesResponse
    }
}

@Client("https://confluence.ecmwf.int/rest/api")
@Retryable
interface ConfluenceOperations {

    @Get(
        "/space?limit={limit}&expand=metadata.labels,description.view,description.plain,icon"
    )
    fun spacesWithMetadataLabelsAndDescriptionAndIcon(
        limit: Int = 100
    ): Optional<SpacesResponse>
}
