package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationEvent
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
@Requires(property =  "app.feature.retrieval-pipeline.ecmwf.publications.download-enabled", value = "true")
open class EcmwfPublicationsPDFDownloadAndExtractService(
    @Value("\${app.retrieval.ecmwf.publications-path}") private val publicationsPath: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Async
    open fun ecmwfPublicationEventReceived(ecmwfPublicationEvent: EcmwfPublicationEvent) {
        log.debug("EcmwfPublicationsPDFDownloadAndExtractService: EcmwfPublicationEvent received")

        val pubDTO: EcmwfPublicationDTO = ecmwfPublicationEvent.ecmwfPublicationDTO

        if (pubDTO.publicationPDF!=null){
            downloadWithCommonsIo(pubDTO)
        } else {
            log.debug("No PDF found for ${pubDTO.nodeId}")
        }
    }

    private fun downloadWithCommonsIo(pubDTO: EcmwfPublicationDTO){
        FileUtils.copyURLToFile(
            URL(pubDTO.publicationPDF),
            File("$publicationsPath/pdf/${pubDTO.nodeId}.pdf"),
            60000,
            60000
        )
    }
    
}


