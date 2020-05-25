package com._2horizon.cva.retrieval.airtable

import com._2horizon.cva.retrieval.event.EcmwfPublicationEvent
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableRecord
import dev.fuxing.airtable.AirtableTable
import dev.fuxing.airtable.exceptions.AirtableApiException
import dev.fuxing.airtable.formula.AirtableFormula
import dev.fuxing.airtable.formula.LogicalOperator
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
@Requires(property = "app.airtable.retrieval.ecmwf.publications")
open class AirtableEcmwfPublicationRepository(
    @Value("\${app.airtable.retrieval.ecmwf.publications}") private val publicationsBase: String,
    api: AirtableApi
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val publicationsTable = api.base(publicationsBase).table("Publications")
    private val keywordsTable = api.base(publicationsBase).table("Keywords")
    private val authorsTable = api.base(publicationsBase).table("Authors")

    @EventListener
    @Async
    open fun ecmwfPublicationEventReceived(ecmwfPublicationEvent: EcmwfPublicationEvent) {
        log.debug("EcmwfPublicationEvent received")

        val pubDTO = ecmwfPublicationEvent.ecmwfPublicationDTO

        // only process unknown publications
        if (lookupPublication(pubDTO.nodeId) != null) {
            return
        }

        saveUnknownKeywords(pubDTO.keywords)
        saveUnknownAuthors(pubDTO.contributors)

        val record = AirtableRecord().apply {
            putField("NodeId", pubDTO.nodeId)

            putField("Title", pubDTO.title)
            putField("SecondaryTitle", pubDTO.secondaryTitle)
            putField("TertiaryTitle", pubDTO.tertiaryTitle)
            putField("Authors", pubDTO.contributors?.map { c -> lookupAuthor(c)!!.id }?.toSet())
            putField("Keywords", pubDTO.keywords?.map { c -> lookupKeyword(c)!!.id }?.toSet())
            putField("PublicationType", pubDTO.publicationType)
            putField("Abstract", pubDTO.abstract)
            putField("Number", pubDTO.number)
            putField("Year", pubDTO.year)
            putField("Language", pubDTO.language)
            putField("Pages", pubDTO.pages)
            putField("Section", pubDTO.section)
            putField("Custom1", pubDTO.custom1)
            putField("Custom2", pubDTO.custom2)
            putField("Custom3", pubDTO.custom3)
            putField("Custom4", pubDTO.custom4)
            putField("Custom5", pubDTO.custom5)
        }

        try {
            log.info("Going to save ${pubDTO.nodeId}")
            publicationsTable.post(record)
        } catch (ex: AirtableApiException) {
            log.warn("Couldn't save because ${ex.type}: ${ex.message}")
        }
    }

    private fun saveUnknownKeywords(keywords: List<String>) {
        val keywordRecords = keywords.mapNotNull { keyword ->
            if (lookupKeyword(keyword) == null) {
                AirtableRecord().apply {
                    putField("Name", keyword)
                }
            } else {
                null
            }
        }
        if (keywordRecords.isNotEmpty()) {
            // Airtable batch allows max 10 per batch
            keywordRecords.chunked(10).forEach { ls -> keywordsTable.post(ls) }
        }
    }

    private fun saveUnknownAuthors(authors: List<String>) {
        val authorsRecords = authors.mapNotNull { keyword ->
            if (lookupAuthor(keyword) == null) {
                AirtableRecord().apply {
                    putField("Name", keyword)
                }
            } else {
                null
            }
        }
        if (authorsRecords.isNotEmpty()) {
            // Airtable batch allows max 10 per batch
            authorsRecords.chunked(10).forEach { ls -> authorsTable.post(ls) }
        }
    }

    private fun lookupPublication(nodeId: Int) = publicationsTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("NodeId"),
            AirtableFormula.Object.value(nodeId)
        )
    }.firstOrNull()

    private fun lookupKeyword(keyword: String) = keywordsTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("Name"),
            AirtableFormula.Object.value(keyword)
        )
    }.firstOrNull()

    private fun lookupAuthor(author: String) = authorsTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("Name"),
            AirtableFormula.Object.value(author)
        )
    }.firstOrNull()
}
