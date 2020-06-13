package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-13.
 */
@Singleton
@Requires(property = "app.feature.retrieval-pipeline.confluence.pages.local-storage", value = "true")
class LocalConfluenceContentStore(
    @Value("\${app.retrieval.ecmwf.confluence-path}") private val localConfluenceContentPath: String,
    @Value("\${app.retrieval.ecmwf.doccano-path}") private val localDoccanoPath: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun confluenceContentEventReceived(confluenceContentEvent: ConfluenceContentEvent) {
        log.info("LocalConfluenceContentStore ConfluenceContentEvent received")

        val pages = confluenceContentEvent.contentList
        val spaceKey = confluenceContentEvent.spaceKey

        val pathToSpace = "$localConfluenceContentPath/$spaceKey"

        Files.createDirectories(Paths.get(pathToSpace))

        pages.forEach { page ->
            storeLocalConfluenceContentDTO(pathToSpace, page)
        }

        storeDoccanoDataset(confluenceContentEvent)
    }

    private fun storeDoccanoDataset(confluenceContentEvent: ConfluenceContentEvent) {
        val pages = confluenceContentEvent.contentList
        val spaceKey = confluenceContentEvent.spaceKey

       val jsonl = pages.mapIndexed { index: Int, page ->

           val text =
               "${page.title} | ${StorageFormatUtil.createDocumentFromStructuredStorageFormat(page.body.storage.value)
                   .text()}"
           DoccanoDataset(id = index, text = text, annotations = emptyList())
       }.joinToString(System.lineSeparator()) {
           objectMapper.writeValueAsString(it)
       }

        File("$localDoccanoPath/$spaceKey.json").writeText(jsonl)
    }

    private fun storeLocalConfluenceContentDTO(pathToSpace: String, page: Content) {
        File("$pathToSpace/${page.id}.json").writeText(
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(page)
        )
    }

    fun readInLocalConfluenceContentDTO(spaceKey: String, contentId: Long): Content {
        return readInLocalConfluenceContentDTO(File("$localConfluenceContentPath/$spaceKey/$${contentId}.json"))
    }

    fun readInLocalConfluenceContentDTO(nodeIdJsonFile: File): Content {
        return objectMapper.readValue(nodeIdJsonFile, Content::class.java)
    }
}

data class DoccanoDataset(
    val id: Int,
    val text: String,
    val annotations: List<DoccanoAnnotation>
) {
    data class DoccanoAnnotation(
        val id: Int,
        val label: Int,
        val start_offset: Int,
        val end_offset: Int,
        val user: Int
    )
}
