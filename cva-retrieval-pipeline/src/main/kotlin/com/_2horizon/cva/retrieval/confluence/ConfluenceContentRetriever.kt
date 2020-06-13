package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
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
class ConfluenceContentRetriever(
    private val confluenceOperations: ConfluenceOperations,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val confluenceSpacesRetriever: ConfluenceSpacesRetriever,
    @Value("\${app.feature.retrieval-pipeline.confluence.pages.enabled:false}") private val retrievalPipelinePagesEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalPipelinePagesEnabled) {
            confluenceSpacesRetriever.retrieveSpaces().spacesList
                // .filter { it.key != "ECC" }
                // .filter { it.key == "CUSF" }
                .filter { it.key == "WIGOSWT" }
                // .filter { it.key == "CKB" }
                .forEach { space -> retrievePages(space.key) }
        }
    }

    private fun retrievePages(spaceKey: String): ConfluenceContentEvent {
        val limit = 25
        var start = 0
        var morePagesAvailable = true
        val pages = mutableListOf<Content>()
        while (morePagesAvailable) {
            log.debug("Going to retrieve pages content for $spaceKey with start at $start and limit $limit")
            val contentResponse =
                confluenceOperations.contentWithMetadataLabelsAndDescriptionAndIcon(spaceKey, limit, start)
                    .orElseThrow { error("Couldn't retrieve content of $spaceKey") }
            log.debug("Got ${contentResponse.size} content pages at $start and limit $limit")
            pages.addAll(contentResponse.contents)

            if (contentResponse.contents.size < limit) {
                morePagesAvailable = false
            } else {
                start += limit
            }
        }

        val confluenceContentEvent = ConfluenceContentEvent(spaceKey, pages)
        applicationEventPublisher.publishEvent(confluenceContentEvent)

        return confluenceContentEvent
    }
}


