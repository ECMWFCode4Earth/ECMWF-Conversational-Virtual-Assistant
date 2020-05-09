package com._2horizon.cva.retrieval.airtable

import com._2horizon.cva.retrieval.confluence.dto.space.MetadataLabels
import com._2horizon.cva.retrieval.confluence.dto.space.Space
import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableRecord
import dev.fuxing.airtable.AirtableTable
import dev.fuxing.airtable.exceptions.AirtableApiException
import dev.fuxing.airtable.fields.AttachmentField
import dev.fuxing.airtable.formula.AirtableFormula
import dev.fuxing.airtable.formula.LogicalOperator
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
@Requires(property = "app.airtable.retrieval.spaces.base")
class AirtableSpacesRepository(
    @Value("\${app.airtable.retrieval.spaces.base}") private val spacesBase: String,
    api: AirtableApi
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val spacesTable = api.base(spacesBase).table("Spaces")
    private val labelsTable = api.base(spacesBase).table("Labels")

    @EventListener
    fun spacesReceived(spacesResponse: SpacesResponse) {
        log.debug("SpacesResponse received")

        val spaces = spacesResponse.spaces
        val labels = spaces.map { it.metadata.labels.results }.flatten().toSet()
        saveUnknownLabels(labels)

        spaces.forEach { space: Space ->

            // only process unknown tweets
            if (lookupSpace(space.id) != null) {
                return@forEach
            }

            val record = AirtableRecord().apply {
                putField("Key", space.key)
                putField("ID", space.id)
                putField("Name", space.name)
                putField("Description", space.description.plain.value)
                putField("Type", space.type)
                putField("Link", space.links.self)
                putField("Homepage", "https://confluence.ecmwf.int${space.expandable.homepage}")
                putField("Labels", space.metadata.labels.results.map { label -> lookupLabel(label)!!.id }.toSet())
                putFieldAttachments(
                    "Icon",
                    listOf(AttachmentField().apply { url = "https://confluence.ecmwf.int${space.icon.path}" })
                )
            }

            try {
                log.info("Going to save ${space.key}")
                spacesTable.post(record)
            } catch (ex: AirtableApiException) {
                log.warn("Couldn't save because ${ex.type}: ${ex.message}")
            }

        }
    }

    private fun saveUnknownLabels(labels: Set<MetadataLabels>) {
        val labelRecords = labels.mapNotNull { label ->
            if (lookupLabel(label) == null) {
                AirtableRecord().apply {
                    putField("Name", label.name)
                    putField("ID", label.id)
                    putField("Prefix", label.prefix)
                }
            } else {
                null
            }
        }
        if (labelRecords.isNotEmpty()) {
            // Airtable batch allows max 10 per batch
            labelRecords.chunked(10).forEach { ls -> labelsTable.post(ls) }
        }
    }

    private fun lookupSpace(spaceID: Int) = spacesTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("ID"),
            AirtableFormula.Object.value(spaceID)
        )
    }.firstOrNull()

    private fun lookupLabel(label: MetadataLabels) = labelsTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("ID"),
            AirtableFormula.Object.value(label.id)
        )
    }.firstOrNull()
}
