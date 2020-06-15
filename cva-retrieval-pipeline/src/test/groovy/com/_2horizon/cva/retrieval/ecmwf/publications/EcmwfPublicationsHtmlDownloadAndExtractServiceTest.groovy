package com._2horizon.cva.retrieval.ecmwf.publications


import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-22.
 */
class EcmwfPublicationsHtmlDownloadAndExtractServiceTest extends Specification {

    @Shared
    EcmwfPublicationsHtmlDownloadAndExtractService service = new EcmwfPublicationsHtmlDownloadAndExtractService()


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
    def "Should convert local publication Html file of #publicationId into publication PDF link"() {
        given:
        Document html = Jsoup.parse(new File("./src/test/resources/data/ecmwf/publications/html/${publicationId}.html").text, 'https://www.ecmwf.int')

        when:
        def publicationPDFs = service.extractEcmwfPublicationPDF(html)

        then:
        publicationPDFs.size() == size
        if (publicationPDFs.size() > 0)
            publicationPDFs.first() == pdfLink

        where:
        publicationId | size | pdfLink
        15680         | 1    | 'https://www.ecmwf.int/file/30392/download?token=OWy1zLKC'
        19325         | 1    | 'https://www.ecmwf.int/file/285919/download?token=iqRJ4EhB'
        19275         | 0    | null
    }


    @Unroll
    def "Should get download publication Html and create Jsoup document"() {
        when:
        Document document = service.downloadPublicationHtml(publicationId)

        then:
        document.hasText()

        where:
        publicationId | pubType
        15680         | ''
        19325         | ''
    }

    @Unroll
    def "Should convert local publication Html file of #publicationId into publication web link"() {
        given:
        Document html = Jsoup.parse(new File("./src/test/resources/data/ecmwf/publications/html/${publicationId}.html").text, 'https://www.ecmwf.int')

        when:
        def publicationLinks = service.extractEcmwfPublicationLink(html)

        then:
        publicationLinks.size() == size
        if (publicationLinks.size() > 0)
            publicationLinks.first() == link

        where:
        publicationId | size | link
        16559         | 1    | 'https://software.ecmwf.int/wiki/display/FUG/Forecast+User+Guide'
        15680         | 0    | null
        19275         | 1    | 'https://www.ecmwf.int/assets/elearning/stochphysics/stochphysics1/story_html5.html'
        19325         | 0    | null
    }


}
