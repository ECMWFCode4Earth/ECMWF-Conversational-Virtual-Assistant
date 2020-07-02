package com._2horizon.cva.retrieval.neo4j

import com._2horizon.cva.retrieval.confluence.ConfluenceLinkExtractor
import com._2horizon.cva.retrieval.confluence.isConfluencePageLink
import com._2horizon.cva.retrieval.confluence.isConfluenceSpaceLink
import com._2horizon.cva.retrieval.confluence.isNotConfluenceLink
import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationsEvent
import com._2horizon.cva.retrieval.neo4j.domain.Publication
import com._2horizon.cva.retrieval.neo4j.domain.PublicationContributor
import com._2horizon.cva.retrieval.neo4j.domain.PublicationKeyword
import com._2horizon.cva.retrieval.neo4j.domain.WebLink
import com._2horizon.cva.retrieval.neo4j.repo.DatasetRepository
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-14.
 */
@Requirements(
    Requires(beans = [SessionFactory::class]),
    Requires(property = "app.feature.ingest-pipeline.neo4j-ingest-enabled", value = "true")
)
@Singleton
class Neo4jEcmwfPublicationsPersister(
    private val datasetRepository: DatasetRepository,
    confluenceLinkExtractor: ConfluenceLinkExtractor
) : AbstractNeo4Persister(datasetRepository,confluenceLinkExtractor) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun cdsCatalogueReceivedEvent(ecmwfPublicationsEvent: EcmwfPublicationsEvent) {
        log.info("Neo4j EcmwfPublicationsEvent received")

        val ecmwfPublicationDTOs = ecmwfPublicationsEvent.ecmwfPublicationDTOs

        ecmwfPublicationDTOs.forEach { pubDTO: EcmwfPublicationDTO ->

            val confluencePageLinks = if (pubDTO.publicationLink.isConfluencePageLink()) {
                lookupConfluencePageLink(pubDTO.publicationLink!!)
            } else {
                null
            }

            val confluenceSpacesLinks = if (pubDTO.publicationLink.isConfluenceSpaceLink()) {
                lookupConfluenceSpaceLink(pubDTO.publicationLink!!)
            } else {
                null
            }

            val weblinks = if (pubDTO.publicationLink != null && pubDTO.publicationLink.isNotConfluenceLink()) {
                listOf(WebLink(pubDTO.publicationLink))
            } else {
                null
            }

            val publication = Publication(
                nodeId = pubDTO.nodeId.toString(),
                title = pubDTO.title,

                abstract = pubDTO.abstractWithoutHtml,
                number = pubDTO.number,
                secondaryTitle = pubDTO.secondaryTitle,
                tertiaryTitle = pubDTO.tertiaryTitle,
                year = pubDTO.year,
                pubDate = pubDTO.pubDate,
                language = pubDTO.language,
                pages = pubDTO.pages,
                issue = pubDTO.issue,
                section = pubDTO.section,
                custom1 = pubDTO.custom1,
                custom2 = pubDTO.custom2,
                publicationType = pubDTO.publicationType,
                keywords = pubDTO.keywords.map { PublicationKeyword(it) },
                publicationContributors = pubDTO.contributors.map { PublicationContributor(it) },
                confluencePages = confluencePageLinks,
                confluenceSpaces = confluenceSpacesLinks,
                externalLinks = weblinks
            )

            datasetRepository.save(publication)
        }
    }


}
