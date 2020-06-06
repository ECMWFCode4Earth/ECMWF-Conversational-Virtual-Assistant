package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.event.ConfluenceSpacesEvent
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
@Requires(property = "app.feature.retrieval-pipeline.confluence.spaces.enabled", value = "true")
class ConfluenceSpacesRetriever(
    private val confluenceOperations: ConfluenceOperations,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
            retrieveSpaces()
    }

    internal fun retrieveSpaces(): ConfluenceSpacesEvent {
        log.info("going to retrieve Confluence Spaces")

        val spacesResponse = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon()
            .orElseThrow { error("Couldn't retrieve spaces") }

        val spaces = spacesResponse.spaces

        log.debug("Got ${spaces.size} spaces")

        val confluenceSpacesEvent = ConfluenceSpacesEvent(spaces)

        applicationEventPublisher.publishEvent(confluenceSpacesEvent)

        return confluenceSpacesEvent
    }
}

