package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.sitemap.SitemapRetrievalService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
class EcmwfPublicationsBibEndNoteCrawlService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadAndExtractBibEndNote(publicationId:Int): EcmwfPublicationDTO {

        val url = "https://www.ecmwf.int/en/elibrary/export/xml/$publicationId"

        log.debug("Going to request BibEndNote $url")

        val xml = Jsoup.connect(url).get()

        return extractEcmwfPublicationDTO(xml)
    }

    private fun extractEcmwfPublicationDTO(xml: Document): EcmwfPublicationDTO {
        val contributors = xml.getElementsByTag("author").map { it.text() }
        val keywords = xml.getElementsByTag("keyword").map { it.text() }
        val title = xml.getElementsByTag("title").first().text()
        val secondaryTitle = xml.getElementsByTag("secondary-title").firstOrNull()?.text()
        val tertiaryTitle = xml.getElementsByTag("tertiary-title").firstOrNull()?.text()
        val abstract = xml.getElementsByTag("abstract").first()?.text()
        val number = xml.getElementsByTag("number").first()?.text()
        val year = xml.getElementsByTag("year").first().text().toInt()
        val pubDates = xml.getElementsByTag("date").map { it.text() }
        val language = xml.getElementsByTag("language").first().text()

        val custom1 = xml.getElementsByTag("custom1").firstOrNull()?.text()
        val custom2 = xml.getElementsByTag("custom2").firstOrNull()?.text()
        val custom3 = xml.getElementsByTag("custom3").firstOrNull()?.text()
        val custom4 = xml.getElementsByTag("custom4").firstOrNull()?.text()
        val custom5 = xml.getElementsByTag("custom5").firstOrNull()?.text()

        return EcmwfPublicationDTO(
            contributors = contributors,
            keywords = keywords,
            title = title,
            secondaryTitle = secondaryTitle,
            tertiaryTitle = tertiaryTitle,
            abstract = abstract,
            number = number,
            year = year,
            pubDates = pubDates,
            language = language,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            custom4 = custom4,
            custom5 = custom5
        )
    }
}


