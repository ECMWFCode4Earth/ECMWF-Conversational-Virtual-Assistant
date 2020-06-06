package com._2horizon.cva.retrieval.nlp

import com._2horizon.cva.retrieval.copernicus.Datastore
import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import com._2horizon.cva.retrieval.event.CopernicusCatalogueReceivedEvent
import com._2horizon.cva.retrieval.event.SignificantTermsReceivedEvent
import com._2horizon.cva.retrieval.extensions.TextInBrackets
import com._2horizon.cva.retrieval.extensions.extractTextInBrackets
import com._2horizon.cva.retrieval.extensions.extractUppercaseText
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-03.
 */
@Singleton
@Requires(property = "app.feature.nlp-pipeline.copernicus-basic-nlp-enabled", value = "true")
open class CopernicusBasicNlpEnricher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Async
    open fun cdsCatalogueReceivedEvent(copernicusCatalogueReceivedEvent: CopernicusCatalogueReceivedEvent) {
        log.info("CopernicusBasicNlpEnricher CdsCatalogueReceivedEvent received")

        val uiResources = copernicusCatalogueReceivedEvent.uiResources
        val datastore = copernicusCatalogueReceivedEvent.datastore

        val significantTerms = uiResources.map { uiResource ->

            listOf(
                findSigTextInBrackets(uiResource, datastore)
            ).flatten()

        }.flatten()



        applicationEventPublisher.publishEvent(
            SignificantTermsReceivedEvent(
                datastore,
                significantTerms
            )
        )
    }

    private fun findUppercaseText(uiResource: UiResource, datastore: Datastore): List<SignificantTerm> {
        return mutableListOf<TextInBrackets>().apply {
            addAll(uiResource.title.extractUppercaseText())
            addAll(uiResource.richAbstractCleaned.extractUppercaseText())
        }.groupBy { it.textInBrackets }
            .map { entry ->

                val abbreviation = entry.key
                val occurrences = entry.value.size
                val textAround = entry.value.joinToString(" | ") { it.textPriorBrackets }
                val sourceUrl = sourceUrl(datastore, uiResource.name)

                SignificantTerm(abbreviation, uiResource.id, sourceUrl, occurrences, textAround)
            }
    }

    private fun findSigTextInBrackets(
        uiResource: UiResource,
        datastore: Datastore
    ): List<SignificantTerm> {
        return mutableListOf<TextInBrackets>().apply {
            addAll(uiResource.title.extractTextInBrackets())
            addAll(uiResource.richAbstractCleaned.extractTextInBrackets())
        }.groupBy { it.textInBrackets }
            .map { entry ->

                val abbreviation = entry.key
                val occurrences = entry.value.size
                val textAround = entry.value.joinToString(" | ") { it.textPriorBrackets }
                val sourceUrl = sourceUrl(datastore, uiResource.name)

                SignificantTerm(abbreviation, uiResource.id, sourceUrl, occurrences, textAround)
            }
    }

    private fun sourceUrl(datastore: Datastore, name: String) = when (datastore) {
        Datastore.ADS -> {
            "https://ads.atmosphere.copernicus.eu/api/v2.ui/resources/$name"
        }
        Datastore.CDS -> {
            "https://cds.climate.copernicus.eu/api/v2.ui/resources/$name"
        }
        else -> error("Wrong datastore $datastore")
    }
}

data class SignificantTerm(
    val abbreviation: String,
    val source: String,
    val sourceUrl: String,
    val occurrences: Int,
    val textAround: String
)
