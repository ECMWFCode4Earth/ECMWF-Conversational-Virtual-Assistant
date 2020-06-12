package com._2horizon.cva.retrieval.confluence

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.Properties
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */
@Singleton
class ConfluenceLinkExtractor {

    private val log = LoggerFactory.getLogger(javaClass)

    fun extractInternalConfluenceLinks(document: Document, spaceKey: String): List<InternalConfluenceLink> {
        return document.select("ac|link")
            .filter { it.select("ri|page").size > 0 }
            .map { link ->
                val anchor = if (link.attr("ac:anchor").isNotBlank()) {
                    link.attr("ac:anchor")
                } else {
                    null
                }
                val pageLink = link.select("ri|page")
                val contentTitle = pageLink.attr("ri:content-title")
                check(contentTitle.isNotBlank()) { "contentTitle cannot be blank" }
                val sk = if (pageLink.attr("ri:space-key").isNotBlank()) {
                    pageLink.attr("ri:space-key")
                } else {
                    spaceKey
                }

                InternalConfluenceLink(contentTitle, sk, anchor)
            }
    }

    fun extractExternalLinks(document: Document): List<ExternalConfluenceLink> {
        return document.select("a[href]").map { it.attr("href") }
            .mapNotNull { href ->

                when {
                    href.startsWith("https://www.ecmwf.int/en/elibrary/") -> {
                        createEcmwfPublicationsLink(href)
                    }
                    href.startsWith("https://cds.climate.copernicus.eu/cdsapp#!/") -> {
                        createCdsLink(href)
                    }
                    href.startsWith("https://ads.atmosphere.copernicus.eu/cdsapp#!/") -> {
                        createAdsLink(href)
                    }
                    href.startsWith("https://confluence.ecmwf.int") -> {
                        createConfluenceLink(href)
                    }
                    else -> ExternalConfluenceLink(href, ExternalConfluenceLinkType.OTHER, Properties())
                }

            }
    }

    private fun createConfluenceLink(href: String): ExternalConfluenceLink? {

        // skip document attachements
        if (href.toLowerCase().contains("attachments")) {
            return null
        }

        if (href.toLowerCase().startsWith("https://confluence.ecmwf.int/pages/viewpage.action?pageid=")) {
            val pageId =
                href.replace("https://confluence.ecmwf.int/pages/viewpage.action?pageId=", "").split("#").first()
            check(pageId.toIntOrNull() != null) { "Couldn't extract pageId of $pageId" }
            return ExternalConfluenceLink(
                href = href,
                type = ExternalConfluenceLinkType.CONFLUENCE_DIRECT_LINK,
                properties = Properties().apply {
                    setProperty("pageId", pageId)
                }
            )
        } else if (href.toLowerCase().startsWith("https://confluence.ecmwf.int/display")) {
            val uri = URI(href)
            val anchor: String? = uri.fragment
            val pathSegements = uri.path.split("/")

            when (pathSegements.size) {
                4 -> {
                    val spaceKey = pathSegements[2]
                    val contentTitle = URLDecoder.decode(pathSegements[3], Charset.defaultCharset())
                    val properties = Properties().apply {
                        setProperty("contentTitle", contentTitle)
                        setProperty("spaceKey", spaceKey)
                    }
                    if (anchor != null) {
                        properties.setProperty("anchor", anchor)
                    }

                    return ExternalConfluenceLink(
                        href = href,
                        type = ExternalConfluenceLinkType.CONFLUENCE_LINK,
                        properties = properties
                    )
                }
                3 -> {
                    val spaceKey = pathSegements[2]
                    val properties = Properties().apply {
                        setProperty("spaceKey", spaceKey)
                    }
                    return ExternalConfluenceLink(
                        href = href,
                        type = ExternalConfluenceLinkType.CONFLUENCE_SITE_LINK,
                        properties = properties
                    )
                }
                else -> {
                    log.error("Wrong link pathSegements $pathSegements format for $href")
                    return null
                }
            }



        } else {
            error("Wrong link href format $href")
        }
    }

    private fun createAdsLink(href: String): ExternalConfluenceLink {
        val cds =
            href.replace("https://ads.atmosphere.copernicus.eu/cdsapp#!/", "").split("/").last().split("?").first()
        return ExternalConfluenceLink(
            href = href,
            type = ExternalConfluenceLinkType.ADS_DATASET,
            properties = Properties().apply { setProperty("id", cds) }
        )
    }

    private fun createCdsLink(href: String): ExternalConfluenceLink {
        val cds = href.replace("https://cds.climate.copernicus.eu/cdsapp#!/", "").split("/").last().split("?").first()
        return ExternalConfluenceLink(
            href = href,
            type = ExternalConfluenceLinkType.CDS_DATASET,
            properties = Properties().apply { setProperty("id", cds) }
        )
    }

    private fun createEcmwfPublicationsLink(href: String): ExternalConfluenceLink {
        val pubId = href.replace("https://www.ecmwf.int/en/elibrary/", "").split("-").first()
        check(pubId.toIntOrNull() != null) { "Couldn't extract publication id of $href" }
        return ExternalConfluenceLink(
            href = href,
            type = ExternalConfluenceLinkType.ECMWF_PUBLICATION,
            properties = Properties().apply { setProperty("id", pubId) }
        )
    }
}

data class InternalConfluenceLink(
    val contentTitle: String,
    val spaceKey: String,
    val anchor: String?
)

data class ExternalConfluenceLink(
    val href: String,
    val type: ExternalConfluenceLinkType,
    val properties: Properties
)

enum class ExternalConfluenceLinkType {
    ECMWF_PUBLICATION, ADS_DATASET, ADS_APPLICATION, CDS_DATASET, CDS_APPLICATION, OTHER, CONFLUENCE_LINK, CONFLUENCE_DIRECT_LINK, CONFLUENCE_SITE_LINK
}
