package com._2horizon.cva.retrieval.ecmwf.publications

import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
class EcmwfPublicationsRetriever(
    private val publicationsBibEndNoteCrawlService:EcmwfPublicationsBibEndNoteCrawlService,
    @Value("\${app.feature.retrieval-pipeline.ecmwf-publications-enabled:false}") private val retrievalEcmwfPublicationsEnabled: Boolean,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalEcmwfPublicationsEnabled) {
            retrievalEcmwfPublications()
        }
    }

    private fun retrievalEcmwfPublications() {
        TODO("Not yet implemented")
    }
    
}
