package com._2horizon.cva.retrieval.extract.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-25.
 */
@Singleton
class PDFToTextService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun convertToText(pdf: File): String {
        log.debug("Going to convert ${pdf.name} to text")
        val document: PDDocument = PDDocument.load(pdf)
        val pdfStripper = PDFTextStripper()
        val text = pdfStripper.getText(document)
        document.close()
        return text
    }
}
