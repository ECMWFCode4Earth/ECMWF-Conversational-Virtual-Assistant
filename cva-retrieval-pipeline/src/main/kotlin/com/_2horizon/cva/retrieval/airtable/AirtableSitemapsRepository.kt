package com._2horizon.cva.retrieval.airtable

import com._2horizon.cva.retrieval.event.SitemapEvent
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableRecord
import dev.fuxing.airtable.AirtableTable
import dev.fuxing.airtable.exceptions.AirtableApiException
import dev.fuxing.airtable.formula.AirtableFormula
import dev.fuxing.airtable.formula.LogicalOperator
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@Singleton
@Requires(property = "app.airtable.retrieval.ecmwf.sitemaps")
class AirtableSitemapsRepository(
    @Value("\${app.airtable.retrieval.ecmwf.sitemaps}") private val sitemapsBase: String,
    api: AirtableApi
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val sitemapsTable = api.base(sitemapsBase).table("Sitemaps")

    @EventListener
    fun spacesReceived(sitemapsEvent: SitemapEvent) {
        log.debug("SitemapEvent received")

        val sitemapRecords = sitemapsEvent.sitemapsList
            .map { sitemap ->    

                val urlSegments = URI(sitemap.loc).path.split("/").filter { it.isNotEmpty() }

                AirtableRecord().apply {
                    putField("Loc", sitemap.loc)
                    putField("Seg0", urlSegments.getOrElse(0) { "" })
                    putField("Seg1", urlSegments.getOrElse(1) { "" })
                    putField("Seg2", urlSegments.getOrElse(2) { "" })
                    putField("Seg3", urlSegments.getOrElse(3) { "" })
                    putField("Seg4", urlSegments.getOrElse(4) { "" })
                    putField("Seg5", urlSegments.getOrElse(5) { "" })
                    putField("Seg6", urlSegments.getOrElse(6) { "" })
                    putField("Seg7", urlSegments.getOrElse(7) { "" })
                    putField("Lastmod", sitemap.lastmod?.format(DateTimeFormatter.ISO_DATE_TIME))
                    putField("Changefreq", sitemap.changefreq)
                }

            }

        sitemapRecords.chunked(10).forEachIndexed { index, recordBatch ->
            try {
                log.info("Going to save recordBatch $index")
                sitemapsTable.post(recordBatch)
            } catch (ex: AirtableApiException) {
                log.warn("Couldn't save because ${ex.type}: ${ex.message}")
            }
        }
    }

    private fun lookupSitemap(loc: String) = sitemapsTable.list { querySpec: AirtableTable.QuerySpec ->
        querySpec.filterByFormula(
            LogicalOperator.EQ,
            AirtableFormula.Object.field("Loc"),
            AirtableFormula.Object.value(loc)
        )
    }.firstOrNull()
}
