package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.event.ConfluenceSpacesEvent
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
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

    internal fun retrieveSpaces(): ConfluenceSpacesEvent {
        val spacesResponse = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon()
            .orElseThrow { error("Couldn't retrieve spaces") }

        val spaces = spacesResponse.spaces

        log.debug("Got ${spaces.size} spaces")

        val confluenceSpacesEvent = ConfluenceSpacesEvent(spaces)

        applicationEventPublisher.publishEvent(confluenceSpacesEvent)

        return confluenceSpacesEvent
    }
}

