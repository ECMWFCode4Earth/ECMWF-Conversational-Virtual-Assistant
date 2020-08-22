package com._2horizon.cva.copernicus


import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Frank Lieber (liefra) on 2020-07-12.
 */

class CopernicusDataStoreSolrSearchServiceTest extends Specification {

    @Shared
    CopernicusDataStoreSolrSearchService service = new CopernicusDataStoreSolrSearchService()


    def "Should search and find Solr datasets"() {
        when:
        def r = service.searchDatasetsByQueryTerm("temperature")

        then:
        r.size() > 0
    }
}
