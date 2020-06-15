package com._2horizon.cva.retrieval.neo4j

import com._2horizon.cva.retrieval.confluence.ConfluenceExperimentalOperations
import com._2horizon.cva.retrieval.confluence.ConfluenceLinkExtractor
import com._2horizon.cva.retrieval.confluence.ConfluenceOperations
import com._2horizon.cva.retrieval.confluence.ExternalConfluenceLink
import com._2horizon.cva.retrieval.confluence.ExternalConfluenceLinkType
import com._2horizon.cva.retrieval.confluence.InternalConfluenceLink
import com._2horizon.cva.retrieval.confluence.StorageFormatUtil
import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com._2horizon.cva.retrieval.confluence.dto.space.Space
import com._2horizon.cva.retrieval.corenlp.POSTaggerService
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import com._2horizon.cva.retrieval.event.ConfluenceParentChildRelationshipEvent
import com._2horizon.cva.retrieval.event.ConfluenceSpacesEvent
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceAuthor
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceComment
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceLabel
import com._2horizon.cva.retrieval.neo4j.domain.ConfluencePage
import com._2horizon.cva.retrieval.neo4j.domain.ConfluenceSpace
import com._2horizon.cva.retrieval.neo4j.domain.QuestionAnswer
import com._2horizon.cva.retrieval.neo4j.domain.WebLink
import com._2horizon.cva.retrieval.neo4j.repo.DatasetRepository
import com._2horizon.cva.retrieval.nlp.SentencesDetector
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.neo4j.ogm.session.SessionFactory
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.streams.toList

/**
 * Created by Frank Lieber (liefra) on 2020-06-01.
 */
@Requirements(
    Requires(beans = [SessionFactory::class]),
    Requires(property = "app.feature.ingest-pipeline.neo4j-ingest-enabled", value = "true")
)
@Singleton
open class Neo4jConfluenceSpacesPersister(
    private val datasetRepository: DatasetRepository,
    private val sentencesDetector: SentencesDetector,
    private val posTaggerService: POSTaggerService,
    private val confluenceLinkExtractor: ConfluenceLinkExtractor,
    private val confluenceExperimentalOperations: ConfluenceExperimentalOperations,
    private val confluenceOperations: ConfluenceOperations
) : AbstractNeo4Persister(datasetRepository, confluenceLinkExtractor) {
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
                labels = space.metadata.labels.results.map { ConfluenceLabel(it.name) }
            )

            datasetRepository.save(confluenceSpace)

        }


        log.debug("DONE with Neo4j ConfluenceSpacesEvent received")
    }

    @EventListener
    fun confluenceContentEventReceived(confluenceContentEvent: ConfluenceContentEvent) {
        log.info("Neo4j ConfluenceContentEvent received")

        val pages = confluenceContentEvent.contentList
        val spaceKey = confluenceContentEvent.spaceKey

        val extended = spaceKey == "WIGOSWT"

        val confluenceSpace = datasetRepository.load<ConfluenceSpace>(spaceKey)

        pages.forEach { page ->

            val (updatedByAuthor, editors) = extractPageEditors(page, extended)

            val (titleQuestion, questionsInBody) = extractPageQuestions(page, extended)

            val pageComments = extractPageComments(page)

            val confluencePage = ConfluencePage(
                space = confluenceSpace,
                contentId = page.id.toString(),
                spaceKey = spaceKey,
                title = page.title,
                type = page.type,
                status = page.status,
                bodyPlain = page.body.view.valueWithoutHtml,
                titleQuestion = titleQuestion,
                contentLength = page.body.view.valueWithoutHtml.length,
                createdDate = page.history.createdDate,
                updatedDate = page.version.`when`,
                version = page.version.number,
                updatedBy = updatedByAuthor,
                labels = page.metadata.labels.results.map { ConfluenceLabel(it.name) },
                faqs = questionsInBody,
                edits = editors,
                comments = pageComments,
                childPage = null,
                internalLinks = null,
                externalLinks = null
            )

            datasetRepository.save(confluencePage)

        }

        handleLinks(pages, spaceKey)

        handleAllChildPages(pages)


        log.debug("DONE with Neo4j ConfluenceContentEvent received")
    }

    private fun handleAllChildPages(pages: List<Content>) {
        pages.forEach { parentPage ->
            retrievePageChildren(parentPage.id)
        }
    }

    private fun retrievePageChildren(parentId: Long) {
        confluenceOperations.contentWithChildPages(parentId)
            .page.results
            .forEach { result ->

                val childId = result.id

                parentChildRelationshipEventReceived(
                    ConfluenceParentChildRelationshipEvent(
                        parentId = parentId,
                        childId = childId
                    )
                )

                if (result.children != null && result.children.page.results.isNotEmpty()) {
                    retrievePageChildren(childId)
                }

            }
    }

    private fun handleLinks(
        pages: List<Content>,
        spaceKey: String
    ) {
        pages.forEach { page ->

            val (internalConfluenceLinks, externalLinks) = extractPageLinks(page, spaceKey)

            val allInteralLinks =
                setOf(
                    lookupConfluencePagesByInternalConfluenceLink(internalConfluenceLinks),
                    lookupConfluencePagesByExternalConfluenceLink(externalLinks)
                ).flatten()

            val otherLinks = externalLinks.filterNot { it.type == ExternalConfluenceLinkType.CONFLUENCE_LINK }
                .map { link ->
                    WebLink(link.href)
                }

            val confluencePageWithLinks = datasetRepository.load<ConfluencePage>(page.id.toString())
                .copy(
                    internalLinks = allInteralLinks,
                    externalLinks = otherLinks
                )

            datasetRepository.save(confluencePageWithLinks)

        }
    }

    private fun extractPageComments(page: Content): List<ConfluenceComment> {
        return confluenceOperations.contentComments(page.id)
            .get()
            .contents
            .map {

                ConfluenceComment(
                    contentId = page.id.toString(),
                    title = page.title,
                    type = page.type,
                    status = page.status,
                    bodyPlain = page.body.view.valueWithoutHtml,
                    contentLength = page.body.view.valueWithoutHtml.length,
                    createdDate = page.history.createdDate,
                    updatedDate = page.version.`when`,
                    version = page.version.number,
                    updatedBy = extractPageEditors(page).first
                )

            }
    }

    private fun extractPageLinks(
        page: Content,
        spaceKey: String
    ): Pair<List<InternalConfluenceLink>, List<ExternalConfluenceLink>> {
        val storageDocument = StorageFormatUtil.createDocumentFromStructuredStorageFormat(page.body.storage.value)
        val internalConfluenceLinks = confluenceLinkExtractor.extractInternalConfluenceLinks(storageDocument, spaceKey)
        val externalLinks = confluenceLinkExtractor.extractExternalLinks(storageDocument)
        return Pair(internalConfluenceLinks, externalLinks)
    }

    private fun extractPageQuestions(
        page: Content,
        extractQuestionsInBody: Boolean = false
    ): Pair<QuestionAnswer?, List<QuestionAnswer>> {
        val faqId = "${page.id}#${page.title}"
        val storageDocument = StorageFormatUtil.createDocumentFromStructuredStorageFormat(page.body.storage.value)
        val sentences = sentencesDetector.findCoreNlpSentences(storageDocument.text())
        val titleQuestion = if (posTaggerService.questionDetector(page.title)) QuestionAnswer(
            faqId = faqId,
            question = page.title,
            answer = null
        ) else null

        val questionsInBody = if (extractQuestionsInBody) {
            sentences
                .parallelStream()
                .filter { posTaggerService.questionDetector(it) }
                .map { QuestionAnswer(faqId = faqId, question = it, answer = null) }
                .toList()
        } else {
            emptyList()
        }

        return Pair(titleQuestion, questionsInBody)
    }

    private fun extractPageEditors(
        page: Content,
        extractVersionHistory: Boolean = false
    ): Pair<ConfluenceAuthor, List<ConfluenceAuthor>> {
        val updatedBy = page.version.user
        val updatedByAuthor =
            ConfluenceAuthor(updatedBy.userKey, updatedBy.username, updatedBy.displayName, updatedBy.type)

        val editors = if (extractVersionHistory) {
            retrieveAndSavePageVersions(page.id, page.version.number)
        } else {
            emptyList()
        }

        return Pair(updatedByAuthor, editors)
    }

    private fun retrieveAndSavePageVersions(contentId: Long, latestVersionNumber: Int): List<ConfluenceAuthor> {

        return latestVersionNumber.downTo(1)
            .toSet()
            .parallelStream()
            .map { versionNumber ->
                confluenceExperimentalOperations.contentVersion(contentId, versionNumber).get().user
            }
            .toList()
            .toSet()
            .map { user ->
                val editor = datasetRepository.loadOrNull<ConfluenceAuthor>(user.userKey)

                val persistedEditor = if (editor == null) {
                    val newEditor = ConfluenceAuthor(
                        userKey = user.userKey,
                        name = user.username,
                        displayName = user.displayName,
                        type = user.type
                    )
                    datasetRepository.save(newEditor)
                    newEditor
                } else {
                    editor
                }

                persistedEditor
            }
    }

    private fun parentChildRelationshipEventReceived(parentChildRelationshipEvent: ConfluenceParentChildRelationshipEvent) {
        log.info("Neo4j ConfluenceParentChildRelationshipEvent received")

        val parent = datasetRepository.load<ConfluencePage>(parentChildRelationshipEvent.parentId.toString())
        val child = datasetRepository.load<ConfluencePage>(parentChildRelationshipEvent.childId.toString())

        datasetRepository.save(parent.copy(childPage = child))

        log.debug("DONE with Neo4j ConfluenceParentChildRelationshipEvent received")
    }
}


