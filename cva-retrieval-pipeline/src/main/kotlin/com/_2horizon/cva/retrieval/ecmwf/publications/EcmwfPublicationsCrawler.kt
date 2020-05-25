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
class EcmwfPublicationsCrawler(
    private val publicationsBibEndNoteDownloadAndExtractService: EcmwfPublicationsBibEndNoteDownloadAndExtractService,
    private val publicationsHtmlDownloadAndExtractService: EcmwfPublicationsHtmlDownloadAndExtractService,
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
            .map { sitemap -> Pair(extractNodeIdFromSitemapLoc(sitemap.loc), sitemap.loc) }
            .sortedByDescending { it.first }

        // just in case the sitemap format changed
        check(publicationSitemaps.size > 7000) { "Wrong publicationSitemaps size with ${publicationSitemaps.size}" }

        return publicationSitemaps
            .filter { it.first < 13729 }
            .map { sitemapPair ->

                val nodeId = sitemapPair.first
                val loc = sitemapPair.second
                log.debug("going to process $loc")

                val ecmwfPublicationDTO =
                    publicationsBibEndNoteDownloadAndExtractService.downloadAndExtractBibEndNote(nodeId)

                val publicationType = publicationsHtmlDownloadAndExtractService.downloadAndExtractPublicationType(nodeId)

                val pubDTO = ecmwfPublicationDTO.copy(publicationType = publicationType)
                applicationEventPublisher.publishEvent(EcmwfPublicationEvent(pubDTO))
                pubDTO
            }
    }

    private fun extractNodeIdFromSitemapLoc(loc: String): Int {
        val urlParts = loc.replace("http://www.ecmwf.int/en/elibrary/", "").split("/")
        val nodeIdPart = urlParts.filter { part -> part.matches("^[0-9]{4,7}.*".toRegex()) }
        check(nodeIdPart.size == 1) { "Multiple nodeIdParts found $nodeIdPart" }
        return nodeIdPart.first().split("-").first().toInt()
    }

    private fun filterEcmwfPublications(sitemap: Sitemap): Boolean =
        sitemap.loc.startsWith("http://www.ecmwf.int/en/elibrary/")
}
