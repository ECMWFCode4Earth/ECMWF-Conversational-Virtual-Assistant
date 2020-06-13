package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.time.LocalDate
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
class EcmwfPublicationsBibEndNoteDownloadAndExtractService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadAndExtractBibEndNote(nodeId: Int): EcmwfPublicationDTO {

        val url = "https://www.ecmwf.int/en/elibrary/export/xml/$nodeId"

        log.debug("Going to request BibEndNote $url")

        val xml = Jsoup.connect(url).get()

        return extractEcmwfPublicationDTO(xml, nodeId)
    }

    private fun extractEcmwfPublicationDTO(xml: Document, nodeId: Int): EcmwfPublicationDTO {
        val contributors = xml.getElementsByTag("author").map { it.text() }
        val keywords = xml.getElementsByTag("keyword").map { it.text() }
        val title = xml.getElementsByTag("title").first().text()
        val secondaryTitle = xml.getElementsByTag("secondary-title").firstOrNull()?.text()
        val tertiaryTitle = xml.getElementsByTag("tertiary-title").firstOrNull()?.text()
        val abstract = xml.getElementsByTag("abstract").first()?.text()
        val number = xml.getElementsByTag("number").first()?.text()
        val year = xml.getElementsByTag("year").first()?.text()?.toInt()
        val pubDate = xml.getElementsByTag("date").firstOrNull()?.text()
        val language = xml.getElementsByTag("language").first()?.text()

        val pages = xml.getElementsByTag("pages").firstOrNull()?.text()
        val issue = xml.getElementsByTag("issue").firstOrNull()?.text()
        val section = xml.getElementsByTag("section").firstOrNull()?.text()

        val custom1 = xml.getElementsByTag("custom1").firstOrNull()?.text()
        val custom2 = xml.getElementsByTag("custom2").firstOrNull()?.text()
        val custom3 = xml.getElementsByTag("custom3").firstOrNull()?.text()
        val custom4 = xml.getElementsByTag("custom4").firstOrNull()?.text()
        val custom5 = xml.getElementsByTag("custom5").firstOrNull()?.text()

        return EcmwfPublicationDTO(
            nodeId = nodeId,
            contributors = contributors,
            keywords = keywords,
            title = title,
            secondaryTitle = secondaryTitle,
            tertiaryTitle = tertiaryTitle,
            abstract = abstract,
            number = number,
            year = year,
            pubDate = pubDateToLocalDate(pubDate, nodeId),
            language = language,
            pages = pages,
            issue = issue,
            section = section,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            custom4 = custom4,
            custom5 = custom5
        )
    }

    private fun pubDateToLocalDate(pubDate: String?, nodeId: Int): LocalDate? {
        if (pubDate == null) {
            return null
        }
        val pubDateParts = pubDate.split("/").map { it.toInt() }
        return try {
            when (pubDateParts.size) {
                3 -> {
                    LocalDate.of(pubDateParts[2], pubDateParts[1], pubDateParts[0])
                }
                2 -> {
                    LocalDate.of(pubDateParts[1], pubDateParts[0], 1)
                }
                1 -> {
                    LocalDate.of(pubDateParts[0], 1, 1)
                }
                else -> error("pubDateParts has wrong size $pubDateParts")
            }
        } catch (ex: Throwable) {
            log.error("Couldn't convert pubDateToLocalDate: $pubDate fpr $nodeId ")
            null
        }
    }
}


