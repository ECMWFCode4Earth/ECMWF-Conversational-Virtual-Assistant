package com._2horizon.cva.retrieval.ecmwf.publications

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.nodes.NodeId
import java.net.URL
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
class EcmwfPublicationsHtmlDownloadAndExtractService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadAndExtractPublicationType(nodeId: Int): String {

        val publicationUrl ="https://www.ecmwf.int/node/$nodeId"

        log.debug("Going to request publicationUrl $publicationUrl")

        val xml = Jsoup.parse(URL(publicationUrl), 60000)

        return extractEcmwfPublicationType(xml)
    }

    private fun extractEcmwfPublicationType(htmlDoc: Document): String = htmlDoc.selectFirst("#biblio-node")
        .select("tr")
        .filter { tr -> tr.select("td").first().text() == "Publication Type" }
        .map { tr -> tr.select("td").last().text() }
        .first()
}


