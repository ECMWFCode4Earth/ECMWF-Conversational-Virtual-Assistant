package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationsEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
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
    open fun ecmwfPublicationEventReceived(ecmwfPublicationsEvent: EcmwfPublicationsEvent) {
        log.debug("EcmwfPublicationToFileSaver: EcmwfPublicationEvent received")

        ecmwfPublicationsEvent.ecmwfPublicationDTOs.forEach { pubDTO ->
            File("$publicationsPath/json/${pubDTO.nodeId}.json").writeText(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pubDTO)
            )
        }
    }

    fun readInLocalEcmwfPublicationDTO(nodeId: Int): EcmwfPublicationDTO {
        return readInLocalFileEcmwfPublicationDTO(File("$publicationsPath/json/${nodeId}.json"))
    }

    fun readInLocalFileEcmwfPublicationDTO(nodeIdJsonFile: File): EcmwfPublicationDTO {
        return objectMapper.readValue(nodeIdJsonFile, EcmwfPublicationDTO::class.java)
    }
}
