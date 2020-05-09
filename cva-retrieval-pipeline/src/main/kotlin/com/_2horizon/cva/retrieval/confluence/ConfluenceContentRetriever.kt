package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.content.ContentResponse
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
class ConfluenceContentRetriever(
    private val confluenceOperations: ConfluenceOperations,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.pages-enabled:false}") private val retrievalPipelinePagesEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalPipelinePagesEnabled) {
            retrievePages("CKB")
        }
    }

    private fun retrievePages(spaceKey:String): ContentResponse {
        val spacesResponse = confluenceOperations.contentWithMetadataLabelsAndDescriptionAndIcon(spaceKey)
            .orElseThrow { error("Couldn't retrieve content of $spaceKey") }
        log.debug("Got ${spacesResponse.size} spaces")

        applicationEventPublisher.publishEvent(spacesResponse)

        return spacesResponse
    }
}


