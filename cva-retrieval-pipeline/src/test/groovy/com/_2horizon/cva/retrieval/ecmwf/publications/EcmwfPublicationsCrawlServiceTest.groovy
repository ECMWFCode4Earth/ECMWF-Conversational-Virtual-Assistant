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
class EcmwfPublicationsCrawlServiceTest extends Specification {

    @Shared
    EcmwfPublicationsBibEndNoteCrawlService service = new EcmwfPublicationsBibEndNoteCrawlService()


    @Unroll
    def "Should convert local BibEndNote XML file of #publicationId into EcmwfPublicationDTO"() {
        given:
        Document xml = Jsoup.parse(new File("./src/test/resources/data/ecmwf/publications/endnotexml/${publicationId}-Biblio-EndNote.xml").text, "", Parser.xmlParser())

        when:
        EcmwfPublicationDTO pub = service.extractEcmwfPublicationDTO(xml)

        then:
        pub.title == title
        pub.contributors.size() == contributors
        pub.keywords.size() == keywords
        pub.year == year

        where:
        publicationId | contributors | year | keywords | title
        15680         | 1            | 2006 | 1        | 'Global implications of Arctic climate processes and feedbacks'
        19325         | 9            | 2020 | 0        | 'Reduced-resolution ocean configurations for efficient testing with the ECMWF coupled model'
    }

    @Unroll
    def "Should get download And convert BibEndNote of #publicationId into EcmwfPublicationDTO"() {
        when:
        EcmwfPublicationDTO pub = service.downloadAndExtractBibEndNote(publicationId)

        then:
        pub.title == title
        pub.contributors.size() == contributors
        pub.keywords.size() == keywords
        pub.year == year

        where:
        publicationId | contributors | year | keywords | title
        15680         | 1            | 2006 | 1        | 'Global implications of Arctic climate processes and feedbacks'
        19325         | 9            | 2020 | 0        | 'Reduced-resolution ocean configurations for efficient testing with the ECMWF coupled model'
    }
}
