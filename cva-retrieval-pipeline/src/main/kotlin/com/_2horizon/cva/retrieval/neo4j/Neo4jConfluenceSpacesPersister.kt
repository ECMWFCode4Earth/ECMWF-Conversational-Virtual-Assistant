package com._2horizon.cva.retrieval.neo4j

import com._2horizon.cva.retrieval.confluence.dto.space.Space
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import com._2horizon.cva.retrieval.event.ConfluenceParentChildRelationshipEvent
import com._2horizon.cva.retrieval.event.ConfluenceSpacesEvent
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceAuthor
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceLabel
import com._2horizon.cva.retrieval.neo4j.domain.ConfluencePage
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceSpace
import com._2horizon.cva.retrieval.neo4j.repo.DatasetRepository
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-01.
 */
@Requirements(
    Requires(beans = [SessionFactory::class]),
    Requires(property = "app.feature.ingest-pipeline.neo4j-ingest-enabled", value = "true")
)
@Singleton
open class Neo4jConfluenceSpacesPersister(
    private val datasetRepository: DatasetRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Async
    open fun confluenceSpacesEventReceived(confluenceSpacesEvent: ConfluenceSpacesEvent) {
        log.info("Neo4j ConfluenceSpacesEvent received")

        val spaces = confluenceSpacesEvent.spacesList

        spaces.forEach { space: Space ->
            val confluenceSpace = ConfluenceSpace(
                spaceKey = space.key,
                spaceId = space.id,
                name = space.name,
                type = space.type,
                description = space.description.plain.value,
                labels = space.metadata.labels.results.map { ConfluenceLabel(it.name) }.toSet()
            )

            datasetRepository.save(confluenceSpace)

        }


        log.debug("DONE with Neo4j ConfluenceSpacesEvent received")
    }

    @EventListener
    @Async
    open fun confluenceContentEventReceived(confluenceContentEvent: ConfluenceContentEvent) {
        log.info("Neo4j ConfluenceContentEvent received")

        val pages = confluenceContentEvent.contentList
        val spaceKey = confluenceContentEvent.spaceKey

        pages.forEach { page ->

            val updatedBy = page.version.user
            val updatedByAuthor =
                ConfluenceAuthor(updatedBy.userKey, updatedBy.username, updatedBy.displayName, updatedBy.type)

            val createdBy = page.history.createdBy
            val createdByAuthor =
                ConfluenceAuthor(createdBy.userKey, createdBy.username, createdBy.displayName, createdBy.type)

            val confluencePage = ConfluencePage(
                contentId = page.id.toString(),
                spaceKey = spaceKey,
                title = page.title,
                type = page.type,
                status = page.status,
                createdDate = page.history.createdDate,
                updatedDate = page.version.`when`,
                version = page.version.number,
                updatedBy = updatedByAuthor,
                authors = setOf(createdByAuthor, createdByAuthor),
                labels = page.metadata.labels.results.map { ConfluenceLabel(it.name) }.toSet(),
                childPage = null
            )

            datasetRepository.save(confluencePage)

        }


        log.debug("DONE with Neo4j ConfluenceContentEvent received")
    }

    @EventListener
    // @Async
    open fun parentChildRelationshipEventReceived(parentChildRelationshipEvent: ConfluenceParentChildRelationshipEvent) {
        log.info("Neo4j ConfluenceParentChildRelationshipEvent received")

        val parent = datasetRepository.load<ConfluencePage>(parentChildRelationshipEvent.parentId.toString())
        val child = datasetRepository.load<ConfluencePage>(parentChildRelationshipEvent.childId.toString())

        datasetRepository.save(parent.copy(childPage = child))

        log.debug("DONE with Neo4j ConfluenceParentChildRelationshipEvent received")
    }
}


