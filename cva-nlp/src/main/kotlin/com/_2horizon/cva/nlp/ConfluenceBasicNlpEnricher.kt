package com._2horizon.cva.nlp

import com._2horizon.cva.common.confluence.dto.content.Content
import com._2horizon.cva.retrieval.copernicus.Datastore
import com._2horizon.cva.retrieval.event.ConfluenceContentEvent
import com._2horizon.cva.retrieval.event.SignificantTermsReceivedEvent
import com._2horizon.cva.retrieval.extensions.TextInBrackets
import com._2horizon.cva.retrieval.extensions.extractTextInBrackets
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventPublisher
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-03.
 */
@Singleton
@Requires(property = "app.feature.basic-nlp.confluence.enabled", value = "true")
open class ConfluenceBasicNlpEnricher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // @EventListener
    // @Async
    open fun confluenceContentEventReceivedEvent(confluenceContentEvent: ConfluenceContentEvent) {
        log.info("ConfluenceBasicNlpEnricher ConfluenceContentEvent received")

        val spaceKey = confluenceContentEvent.spaceKey
        val contentList = confluenceContentEvent.contentList

        val significantTerms = contentList.map { content ->

            listOf(
                findSigTextInBrackets(spaceKey, content)
            ).flatten()

        }.flatten()

        applicationEventPublisher.publishEvent(
            SignificantTermsReceivedEvent(
                Datastore.CONFLUENCE,
                significantTerms
            )
        )
    }

    private fun findSigTextInBrackets(
        spaceKey: String,
        content: Content
    ): List<SignificantTerm> {
        return mutableListOf<TextInBrackets>().apply {
            addAll(content.title.extractTextInBrackets())
            addAll(content.body!!.view.valueWithoutHtml.extractTextInBrackets())
        }.groupBy { it.textInBrackets }
            .map { entry ->

                val abbreviation = entry.key
                val occurrences = entry.value.size
                val textAround = entry.value.joinToString(" | ") { it.textPriorBrackets }
                val sourceUrl = "https://confluence.ecmwf.int/pages/viewpage.action?pageId=${content.id}"

                SignificantTerm(abbreviation, "$spaceKey-${content.id}", sourceUrl, occurrences, textAround)
            }
    }



    fun findStrucutredStorageFormat(document: Document): List<String> {
        return document.select("ac|structured-macro").map { it.attr("ac:name") }
    }


}


