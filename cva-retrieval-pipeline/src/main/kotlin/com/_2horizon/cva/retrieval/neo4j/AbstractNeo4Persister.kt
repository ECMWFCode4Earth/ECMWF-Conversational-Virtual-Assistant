package com._2horizon.cva.retrieval.neo4j

import com._2horizon.cva.retrieval.confluence.ExternalConfluenceLink
import com._2horizon.cva.retrieval.confluence.ExternalConfluenceLinkType
import com._2horizon.cva.retrieval.confluence.InternalConfluenceLink
import com._2horizon.cva.retrieval.neo4j.domain.ConfluencePage
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceSpace
import com._2horizon.cva.retrieval.neo4j.repo.DatasetRepository

/**
 * Created by Frank Lieber (liefra) on 2020-06-14.
 */
abstract class AbstractNeo4Persister(
    private val datasetRepository: DatasetRepository
) {

    fun lookupConfluencePagesByInternalConfluenceLink(
        internalConfluenceLinks: List<InternalConfluenceLink>
    ): List<ConfluencePage> {
        return internalConfluenceLinks.mapNotNull { link ->
            datasetRepository.findConfluencePageByTitleAndSpaceKey(link.contentTitle, link.spaceKey)
        }
    }

    fun lookupConfluencePagesByExternalConfluenceLink(
        externalLinks: List<ExternalConfluenceLink>
    ): List<ConfluencePage> {

        val moreInternalLinks = externalLinks.filter { it.type == ExternalConfluenceLinkType.CONFLUENCE_LINK }
            .mapNotNull {
                datasetRepository.findConfluencePageByTitleAndSpaceKey(
                    it.properties["contentTitle"].toString(),
                    it.properties["spaceKey"].toString()
                )
            }

        val moreInternalDirectLinks =
            externalLinks.filter { it.type == ExternalConfluenceLinkType.CONFLUENCE_DIRECT_LINK }
                .mapNotNull {
                    datasetRepository.loadOrNull<ConfluencePage>(it.properties["pageID"].toString())
                }

        return setOf(moreInternalLinks, moreInternalDirectLinks).flatten()
    }

    fun lookupConfluenceSpacesByExternalConfluenceLink(
        externalLinks: List<ExternalConfluenceLink>
    ): List<ConfluenceSpace> {

        return externalLinks.filter { it.type == ExternalConfluenceLinkType.CONFLUENCE_SPACE_LINK }
            .mapNotNull {
                datasetRepository.loadOrNull<ConfluenceSpace>(it.properties["spaceKey"].toString())
            }
    }
}
