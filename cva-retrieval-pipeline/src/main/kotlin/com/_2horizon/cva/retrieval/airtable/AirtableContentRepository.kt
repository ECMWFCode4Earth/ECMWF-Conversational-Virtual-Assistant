package com._2horizon.cva.retrieval.airtable

import com._2horizon.cva.common.confluence.dto.space.MetadataLabels
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableRecord
import dev.fuxing.airtable.AirtableTable
import dev.fuxing.airtable.exceptions.AirtableApiException
import dev.fuxing.airtable.formula.AirtableFormula
import dev.fuxing.airtable.formula.LogicalOperator
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
@Requires(property = "app.airtable.retrieval.confluence.content")
class AirtableContentRepository(
    @Value("\${app.airtable.retrieval.confluence.content}") private val contentBase: String,
    api: AirtableApi
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val contentTable = api.base(contentBase).table("Content")
    private val labelsTable = api.base(contentBase).table("Labels")

    @EventListener
    fun spacesReceived(contentEvent: ConfluenceContentEvent) {
        log.debug("ConfluenceContentEvent received")

        val labels = contentEvent.contentList.map { it.metadata.labels.results }.flatten().toSet()
        saveUnknownLabels(labels)

        contentEvent.contentList.forEach { content ->

            // only process unknown pagess
            if (lookupContent(content.id) != null) {
                return@forEach
            }

            val record = AirtableRecord().apply {
                putField("ID", content.id)
                putField("SpaceKey", contentEvent.spaceKey)
                putField("Title", content.title)
                putField("CreatedDate", content.history!!.createdDate.format(DateTimeFormatter.ISO_DATE_TIME))
                putField("CreatedBy", content.history!!.createdBy.displayName)
                putField("UpdatedDate", content.version.`when`.format(DateTimeFormatter.ISO_DATE_TIME))
                putField("UpdatedBy", content.version.user.displayName)
                putField("Version", content.version.number)
                putField("Type", content.type)
                putField("Status", content.status)
                putField("Link", "https://confluence.ecmwf.int/pages/viewpage.action?pageId=${content.id}")
                putField("BodyStorage", content.body!!.storage.value)
                putField("BodyView", content.body!!.view.value)
                putField("BodyCount", Jsoup.parse(content.body!!.view.value).text().count())
                putField("Labels", content.metadata.labels.results.map { label -> lookupLabel(label)!!.id }.toSet())

            }

            try {
                log.info("Going to save ${content.id}")
                contentTable.post(record)
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

    private fun lookupContent(spaceID: Long) = contentTable.list { querySpec: AirtableTable.QuerySpec ->
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
