package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationsEvent
import com._2horizon.cva.retrieval.sitemap.Sitemap
import com._2horizon.cva.retrieval.sitemap.SitemapRetrievalService
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.streams.toList

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
@Requires(property = "app.feature.retrieval-pipeline.ecmwf.publications.enabled", value = "true")
class EcmwfPublicationsRetrievalService(
    private val publicationsBibEndNoteDownloadAndExtractService: EcmwfPublicationsBibEndNoteDownloadAndExtractService,
    private val publicationsHtmlDownloadAndExtractService: EcmwfPublicationsHtmlDownloadAndExtractService,
    private val sitemapRetrievalService: SitemapRetrievalService,
    private val ecmwfPublicationsMetadataToFileSaver: EcmwfPublicationsMetadataToFileSaver,
    @Value("\${app.feature.retrieval-pipeline.ecmwf.publications.strategy}") private val retrievalEcmwfPublicationsStrategy: EcmwfPublicationsStrategy,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        retrievalEcmwfPublications()
    }

    private fun retrievalEcmwfPublications(): List<EcmwfPublicationDTO> {

        val publicationSitemaps = retrieveEcmwfSitemapsSortedByDescending()

        val publications = publicationSitemaps
            // .filter { it.first >= 10000 }
            // .take(100)
            .parallelStream()
            .map { sitemapPair ->

                val nodeId = sitemapPair.first
                val loc = sitemapPair.second
                log.debug("going to process $loc")

                if (retrievalEcmwfPublicationsStrategy == EcmwfPublicationsStrategy.LOCAL) {
                    ecmwfPublicationsMetadataToFileSaver.readInLocalEcmwfPublicationDTO(nodeId)
                } else {
                    fetchRemoteEcmwfPublicationAsDTO(nodeId)
                }

            }
            .filter(::filterAll)
            .toList()
            .sortedByDescending { it.pubDate }

        val links = publications.mapNotNull { it.publicationLink }.toSortedSet()

        applicationEventPublisher.publishEvent(EcmwfPublicationsEvent(publications))

        return publications
    }

    private fun filterAll(pubDTO: EcmwfPublicationDTO) = true

    private fun filterNewsletters(pubDTO: EcmwfPublicationDTO) =
        pubDTO.publicationType != null && pubDTO.publicationType == "Newsletter"

    private fun filterECMWFAnnualReport(pubDTO: EcmwfPublicationDTO) =
        pubDTO.publicationType != null && pubDTO.secondaryTitle == "ECMWF Annual Report"

    private fun fetchRemoteEcmwfPublicationAsDTO(nodeId: Int): EcmwfPublicationDTO {
        val ecmwfPublicationDTO =
            publicationsBibEndNoteDownloadAndExtractService.downloadAndExtractBibEndNote(nodeId)

        val extraMetadata = publicationsHtmlDownloadAndExtractService
            .downloadAndExtractPublicationTypeAndPDF(nodeId)

        return ecmwfPublicationDTO.copy(
            publicationType = extraMetadata.publicationType,
            publicationPDF = extraMetadata.publicationPDF,
            publicationLink = extraMetadata.publicationLink
        )
    }

    private fun retrieveEcmwfSitemapsSortedByDescending(): List<Pair<Int, String>> {
        val publicationSitemaps = sitemapRetrievalService.retrieveEcmwfSitemaps()
            .filter(::filterEcmwfPublications)
            .map { sitemap -> Pair(extractNodeIdFromSitemapLoc(sitemap.loc), sitemap.loc) }
            .sortedByDescending { it.first }
        // just in case the sitemap format changed
        check(publicationSitemaps.size > 7000) { "Wrong publicationSitemaps size with ${publicationSitemaps.size}" }
        return publicationSitemaps
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

enum class EcmwfPublicationsStrategy {
    LOCAL, REMOTE
}
