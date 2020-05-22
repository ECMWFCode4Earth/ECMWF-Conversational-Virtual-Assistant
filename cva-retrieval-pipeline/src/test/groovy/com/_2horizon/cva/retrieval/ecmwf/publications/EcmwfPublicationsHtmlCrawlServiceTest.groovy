package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-22.
 */
class EcmwfPublicationsHtmlCrawlServiceTest extends Specification {

    @Shared
    EcmwfPublicationsHtmlCrawlService service = new EcmwfPublicationsHtmlCrawlService()


    @Unroll
    def "Should convert local publication Html file of #publicationId into publication type"() {
        given:
        Document html = Jsoup.parse(new File("./src/test/resources/data/ecmwf/publications/html/${publicationId}.html").text)

        when:
        String publicationType = service.extractEcmwfPublicationType(html)

        then:
        publicationType == pubType

        where:
        publicationId | pubType
        15680         | 'Presentation'
        19325         | 'Technical memorandum'
    }

    @Unroll
    def "Should get download And convert Html of #publicationId into publication type"() {
        when:
        String publicationType = service.downloadAndExtractPublicationType(publicationId)

        then:
        publicationType == pubType

        where:
        publicationId | pubType
        15680         | 'Presentation'
        19325         | 'Technical memorandum'
    }


}
