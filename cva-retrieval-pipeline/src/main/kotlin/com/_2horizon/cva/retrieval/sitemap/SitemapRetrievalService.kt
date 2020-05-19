package com._2horizon.cva.retrieval.sitemap

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-19.
 */
@Singleton
class SitemapRetrievalService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun retrieveEcmwfSitemaps(): List<Sitemap> = retrieveSitemapIndex("https://www.ecmwf.int/sitemap.xml")
        .map(::retrieveSitemapUrl)
        .flatten()

    private fun retrieveSitemapIndex(sitemapIndexUrl: String): List<String> =
        Jsoup.connect(sitemapIndexUrl).get().parser(Parser.xmlParser())
            .getElementsByTag("loc")
            .map { it.text() }

    private fun retrieveSitemapUrl(sitemapUrl: String): List<Sitemap> {

        return Jsoup.connect(sitemapUrl).get().parser(Parser.xmlParser()).getElementsByTag("url")
            .map { urlElement ->
                Sitemap(
                    loc = urlElement.getElementsByTag("loc").first().text(),
                    lastmod = if (urlElement.getElementsByTag("lastmod").firstOrNull() == null) {
                        null
                    } else {
                        OffsetDateTime.parse(
                            urlElement.getElementsByTag("lastmod").first().text(),
                            DateTimeFormatter.ISO_DATE_TIME
                        )
                    },
                    changefreq = urlElement.getElementsByTag("changefreq").firstOrNull()?.text()
                )

            }
    }
}

data class Sitemap(
    val loc: String,
    val lastmod: OffsetDateTime?,
    val changefreq: String?
)




