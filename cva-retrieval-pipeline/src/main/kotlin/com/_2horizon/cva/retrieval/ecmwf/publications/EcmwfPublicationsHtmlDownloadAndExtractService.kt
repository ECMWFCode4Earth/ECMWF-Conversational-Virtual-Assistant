package com._2horizon.cva.retrieval.ecmwf.publications

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.net.URL
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
class EcmwfPublicationsHtmlDownloadAndExtractService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadAndExtractPublicationTypeAndPDF(nodeId: Int): Pair<String?, String> {

        val xml = downloadPublicationHtml(nodeId)

        val publicationType = extractEcmwfPublicationType(xml)
        val publicationPDF = extractEcmwfPublicationPDF(xml)

        check(publicationPDF.size < 2) { "publicationPDF size not 1 but $publicationPDF" }

        return Pair(publicationPDF.firstOrNull(), publicationType)
    }

    private fun downloadPublicationHtml(nodeId: Int): Document {
        val publicationUrl = "https://www.ecmwf.int/node/$nodeId"
        log.debug("Going to request publicationUrl $publicationUrl")
        val xml = Jsoup.parse(URL(publicationUrl), 60000)
        return xml
    }

    private fun extractEcmwfPublicationType(htmlDoc: Document): String = htmlDoc.selectFirst("#biblio-node")
        .select("tr")
        .filter { tr -> tr.select("td").first().text() == "Publication Type" }
        .map { tr -> tr.select("td").last().text() }
        .first()

    private fun extractEcmwfPublicationPDF(htmlDoc: Document): List<String> {
        return if (htmlDoc.select(".field-name-field-publication-file").size>0) {
            htmlDoc.select(".field-name-field-publication-file")
                .select("a")
                .map { a -> a.attr("abs:href") }
        } else {
            emptyList()
        }
    }
}


