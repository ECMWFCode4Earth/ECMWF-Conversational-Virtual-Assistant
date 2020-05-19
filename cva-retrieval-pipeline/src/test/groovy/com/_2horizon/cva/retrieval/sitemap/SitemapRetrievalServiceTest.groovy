package com._2horizon.cva.retrieval.sitemap


import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Frank Lieber (liefra) on 2020-05-19.
 */
class SitemapRetrievalServiceTest extends Specification {

    @Shared
    SitemapRetrievalService service = new SitemapRetrievalService()

    def "Should retrieve ECMWF sitemap index"() {
        when:
        def response = service.retrieveSitemapIndex('https://www.ecmwf.int/sitemap.xml')

        then:
        response.size() == 2

    }

    def "Should retrieve a sitemap"() {
        when:
        def response = service.retrieveSitemapUrl('https://www.ecmwf.int/sitemap.xml?page=1')

        then:
        response.size() == 4996
    }
}
