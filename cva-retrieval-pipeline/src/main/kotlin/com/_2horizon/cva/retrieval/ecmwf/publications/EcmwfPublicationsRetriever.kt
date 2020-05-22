package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationEvent
import com._2horizon.cva.retrieval.sitemap.Sitemap
import com._2horizon.cva.retrieval.sitemap.SitemapRetrievalService
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
    private val publicationsBibEndNoteCrawlService: EcmwfPublicationsBibEndNoteCrawlService,
    private val publicationsHtmlCrawlService: EcmwfPublicationsHtmlCrawlService,
    private val sitemapRetrievalService: SitemapRetrievalService,
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

    private fun retrievalEcmwfPublications(): List<EcmwfPublicationDTO> {

        val publicationSitemaps = sitemapRetrievalService.retrieveEcmwfSitemaps()
            .filter(::filterEcmwfPublications)

        // just in case the sitemap format changed
        check(publicationSitemaps.size > 7000) { "Wrong publicationSitemaps size with ${publicationSitemaps.size}" }

        return publicationSitemaps.map { sitemap ->

            val loc = sitemap.loc
            log.debug("going to process $loc")

            val nodeId = extractNodeIdFromSitemapLoc(sitemap.loc)

            val ecmwfPublicationDTO =
                publicationsBibEndNoteCrawlService.downloadAndExtractBibEndNote(nodeId)

            val publicationType = publicationsHtmlCrawlService.downloadAndExtractPublicationType(nodeId)

            val pubDTO = ecmwfPublicationDTO.copy(publicationType = publicationType)
            applicationEventPublisher.publishEvent(EcmwfPublicationEvent(pubDTO))
            pubDTO
        }
    }

    private fun extractNodeIdFromSitemapLoc(loc: String): Int =
        loc.replace("http://www.ecmwf.int/en/elibrary/", "").split("-").first().toInt()

    private fun filterEcmwfPublications(sitemap: Sitemap): Boolean =
        sitemap.loc.startsWith("http://www.ecmwf.int/en/elibrary/")
}
