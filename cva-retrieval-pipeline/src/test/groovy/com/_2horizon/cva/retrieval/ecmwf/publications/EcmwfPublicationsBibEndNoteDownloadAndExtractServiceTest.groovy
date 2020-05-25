package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-21.
 */
class EcmwfPublicationsBibEndNoteDownloadAndExtractServiceTest extends Specification {

    @Shared
    EcmwfPublicationsBibEndNoteDownloadAndExtractService service = new EcmwfPublicationsBibEndNoteDownloadAndExtractService()


    @Unroll
    def "Should convert local BibEndNote XML file of #publicationId into EcmwfPublicationDTO"() {
        given:
        Document xml = Jsoup.parse(new File("./src/test/resources/data/ecmwf/publications/endnotexml/${publicationId}.xml").text, "", Parser.xmlParser())

        when:
        EcmwfPublicationDTO pub = service.extractEcmwfPublicationDTO(xml, publicationId)

        then:
        pub.title == title
        pub.contributors.size() == contributors
        pub.keywords.size() == keywords
        pub.year == year
        pub.nodeId == publicationId
        pub.section == section
        pub.issue == issue
        pub.pages == pages

        where:
        publicationId | contributors | year | keywords | section       | issue | pages   | title
        15680         | 1            | 2006 | 1        | null          | null  | null    | 'Global implications of Arctic climate processes and feedbacks'
        19325         | 9            | 2020 | 0        | null          | null  | null    | 'Reduced-resolution ocean configurations for efficient testing with the ECMWF coupled model'
        19365         | 4            | 2020 | 0        | 'Meteorology' | '162' | '32-35' | 'New products for the Global Flood Awareness System'
        15444         | 1            | null | 2        | null          | null  | null    | 'Short, medium range and seasonal forecasts verification methods for the AMMA-EU project'
    }


}
