package com._2horizon.cva.retrieval.doccano

import com._2horizon.cva.retrieval.confluence.StorageFormatUtil
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import com._2horizon.cva.retrieval.event.EcmwfPublicationsWithPdfContentEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-13.
 */
@Singleton
@Requires(property = "app.feature.doccano.enabled", value = "true")
open class DoccanoDatasetService(
    @Value("\${app.retrieval.ecmwf.doccano-path}") private val localDoccanoPath: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun storePublicationsDoccanoDataset(ecmwfPublicationsWithPdfContentEvent: EcmwfPublicationsWithPdfContentEvent){
        ecmwfPublicationsWithPdfContentEvent.ecmwfPublicationDTOs
            .chunked(1)
            .forEachIndexed { index, list ->
                val jsonl =  list.map { pubDTO ->
                     val text =
                         "${pubDTO.title} | ${pubDTO.publicationPDFContent}"
                     DoccanoDataset(id = pubDTO.nodeId, text = text, annotations = emptyList())
                 }.joinToString(System.lineSeparator()) {                objectMapper.writeValueAsString(it)                   }
                File("$localDoccanoPath/publications/newsletter-${index.toString().padStart(4,'0')}.json").writeText(jsonl)
            }  

    }

    @EventListener
    fun storeConfluenceDoccanoDataset(confluenceContentEvent: ConfluenceContentEvent) {
        val pages = confluenceContentEvent.contentList
        val spaceKey = confluenceContentEvent.spaceKey

        val jsonl = pages.map {  page ->

            val text =
                "${page.title} | ${StorageFormatUtil.createDocumentFromStructuredStorageFormat(page.body.storage.value)
                    .text()}"
            DoccanoDataset(id = page.id.toInt(), text = text, annotations = emptyList())
        }.joinToString(System.lineSeparator()) {
            objectMapper.writeValueAsString(it)
        }

        File("$localDoccanoPath/confluence/$spaceKey.json").writeText(jsonl)
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
