package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import com._2horizon.cva.retrieval.event.EcmwfPublicationsEvent
import com._2horizon.cva.retrieval.event.EcmwfPublicationsWithPdfContentEvent
import com._2horizon.cva.retrieval.extract.pdf.PDFToTextService
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.runtime.event.annotation.EventListener
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import javax.inject.Singleton
import kotlin.streams.toList

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
@Singleton
@Requires(property = "app.feature.retrieval-pipeline.ecmwf.publications.download-enabled", value = "true")
open class EcmwfPublicationsPDFDownloadAndExtractService(
    @Value("\${app.retrieval.ecmwf.publications-path}") private val publicationsPath: String,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val pdfToTextService: PDFToTextService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    open fun ecmwfPublicationEventReceived(ecmwfPublicationsEvent: EcmwfPublicationsEvent) {
        log.debug("EcmwfPublicationsPDFDownloadAndExtractService: EcmwfPublicationEvent received")

        val pubDTOs = ecmwfPublicationsEvent.ecmwfPublicationDTOs
            .filter { pubDTO -> pubDTO.publicationPDF != null }
            .parallelStream()
            .map { pubDTO -> downloadWithCommonsIo(pubDTO) }
            .toList()

        applicationEventPublisher.publishEvent(EcmwfPublicationsWithPdfContentEvent(pubDTOs))
    }

    private fun downloadWithCommonsIo(pubDTO: EcmwfPublicationDTO): EcmwfPublicationDTO {
        val pdfFile = File("$publicationsPath/pdf/${pubDTO.nodeId}.pdf")
        FileUtils.copyURLToFile(
            URL(pubDTO.publicationPDF),
            pdfFile,
            60000,
            60000
        )

        return if (true) {
            val pdfText = pdfToTextService.convertToText(pdfFile)
            pubDTO.copy(publicationPDFContent = pdfText)
        } else {
            pubDTO
        }
    }
}


