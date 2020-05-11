package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.space.Space
import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import com._2horizon.cva.retrieval.event.ConfluenceSpacesEvent
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
    @Value("\${app.feature.retrieval-pipeline.spaces-enabled:false}") private val retrievalPipelineSpacesEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalPipelineSpacesEnabled) {
            retrieveSpaces()
        }
    }

    private fun retrieveSpaces(): ConfluenceSpacesEvent {
        val spacesResponse = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon()
            .orElseThrow { error("Couldn't retrieve spaces") }

        val spaces = spacesResponse.spaces

        log.debug("Got ${spaces.size} spaces")

        val confluenceSpacesEvent = ConfluenceSpacesEvent(spaces)

        applicationEventPublisher.publishEvent(confluenceSpacesEvent)

        return confluenceSpacesEvent
    }
}

