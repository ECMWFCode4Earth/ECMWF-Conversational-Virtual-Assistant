package com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
@Singleton
class EcmwfPublicationsSearchService(
    private val ecmwfPublicationSearchOperations: EcmwfPublicationSearchOperations,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun findNumberOfResultsByKeyword(keyword: String): Mono<Int> {
        return ecmwfPublicationSearchOperations.search(keyword).map(::convert)
    }

    fun convert(html: String): Int {
        return try {
            val document = Jsoup.parse(html, "https://www.ecmwf.int")

            val results =
                document.selectFirst("div.region-content").selectFirst("div.current-search-item-results").text()
            val numberOfResults = results.split(" ").filter { it.toIntOrNull() != null }
            check(numberOfResults.size == 1) { "Found more than 1 result $numberOfResults" }
            numberOfResults.first().toInt()
        } catch (ex: Throwable) {
            log.error(ex.message)
            0
        }
    }
}
