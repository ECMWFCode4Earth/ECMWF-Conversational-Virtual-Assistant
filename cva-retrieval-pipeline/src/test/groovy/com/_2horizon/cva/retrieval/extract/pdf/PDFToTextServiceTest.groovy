package com._2horizon.cva.retrieval.extract.pdf


import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-25.
 */
class PDFToTextServiceTest extends Specification {


    @Shared
    PDFToTextService service = new PDFToTextService()


    @Unroll
    def "Should convert local publication PDF file of #publicationId into text"() {
        given:
        File pdf = new File("./src/test/resources/data/ecmwf/publications/pdf/${publicationId}.pdf")

        when:
        String pdfText = service.convertToText(pdf)

        then:
        pdfText.startsWith(start)

        where:
        publicationId | start
        19307         | 'Part III: Dynamics and Numerical Procedures'
        19516         | 'COMPUTING'
    }
}
