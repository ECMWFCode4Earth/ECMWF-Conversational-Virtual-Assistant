package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.sitemap.SitemapRetrievalService
import io.micronaut.context.event.ApplicationEventPublisher
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-23.
 */
class EcmwfPublicationsCrawlerTest extends Specification {

    @Shared
    EcmwfPublicationsCrawler service = new EcmwfPublicationsCrawler(
            GroovyMock(EcmwfPublicationsBibEndNoteDownloadAndExtractService),
            GroovyMock(EcmwfPublicationsHtmlDownloadAndExtractService),
            GroovyMock(SitemapRetrievalService),
            false,
            GroovyMock(ApplicationEventPublisher)
    )


    @Unroll
    def "Should get extract NodeId FromSitemapLoc #url"() {
        when:
        def r = service.extractNodeIdFromSitemapLoc(url)

        then:
        r == nodeId

        where:
        url                                                                                                  | nodeId
        'http://www.ecmwf.int/en/elibrary/7918-recent-advances-land-surface-modelling-ecmwf'                 | 7918
        'http://www.ecmwf.int/en/elibrary/7932-ssmi/s-radiances-over-land-all-sky-framework-one-year-report' | 7932
        'http://www.ecmwf.int/en/elibrary/11872'                                                             | 11872
    }
}
