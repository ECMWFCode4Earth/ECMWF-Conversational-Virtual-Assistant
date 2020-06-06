package com._2horizon.cva.retrieval.nlp

import com._2horizon.cva.retrieval.copernicus.Datastore
import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationEvent
import com._2horizon.cva.retrieval.event.SignificantTermsReceivedEvent
import com._2horizon.cva.retrieval.extensions.TextInBrackets
import com._2horizon.cva.retrieval.extensions.extractTextInBrackets
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
@Requires(property = "app.feature.nlp-pipeline.ecmwf-basic-nlp-enabled", value = "true")
open class EcmwfBasicNlpEnricher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Async
    open fun ecmwfPublicationEventReceived(ecmwfPublicationEvent: EcmwfPublicationEvent) {
        log.info("EcmwfBasicNlpEnricher EcmwfPublicationEvent received")

        val pubDTO: EcmwfPublicationDTO = ecmwfPublicationEvent.ecmwfPublicationDTO

        val significantTerms = findSigTextInBrackets(pubDTO)

        applicationEventPublisher.publishEvent(
            SignificantTermsReceivedEvent(
                Datastore.ECMWF_PUBLICATIONS,
                significantTerms
            )
        )
    }

    private fun findSigTextInBrackets(pubDTO: EcmwfPublicationDTO): List<SignificantTerm> {
        return mutableListOf<TextInBrackets>().apply {

            addAll(pubDTO.title.extractTextInBrackets())

            if (pubDTO.abstract != null) {
                addAll(pubDTO.abstract.extractTextInBrackets())
            }

        }.groupBy { it.textInBrackets }
            .map { entry ->

                val abbreviation = entry.key
                val occurrences = entry.value.size
                val textAround = entry.value.joinToString(" | ") { it.textPriorBrackets }

                SignificantTerm(abbreviation, pubDTO.nodeId.toString(), "https://www.ecmwf.int/node/${pubDTO.nodeId}", occurrences, textAround)
            }
    }
}
