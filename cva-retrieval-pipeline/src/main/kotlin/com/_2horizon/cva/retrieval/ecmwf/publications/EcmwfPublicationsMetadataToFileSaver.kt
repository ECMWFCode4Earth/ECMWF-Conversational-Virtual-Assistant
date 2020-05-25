package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.event.EcmwfPublicationEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-23.
 */
@Singleton
open class EcmwfPublicationsMetadataToFileSaver(
    @Value("\${app.retrieval.ecmwf.publications-path}") private val publicationsPath: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Async
    open fun ecmwfPublicationEventReceived(ecmwfPublicationEvent: EcmwfPublicationEvent) {
        log.debug("EcmwfPublicationToFileSaver: EcmwfPublicationEvent received")

        val pubDTO = ecmwfPublicationEvent.ecmwfPublicationDTO

        File("$publicationsPath/json/${pubDTO.nodeId}.json").writeText(
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pubDTO)
        )
    }
}
