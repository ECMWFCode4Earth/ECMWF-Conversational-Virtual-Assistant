package com._2horizon.cva.copernicus

import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
class CopernicusDataStoreAsyncSolrSearchServiceTest extends Specification {

    @Shared
    CopernicusDataStoreAsyncSolrSearchService service = new CopernicusDataStoreAsyncSolrSearchService()


    def "Should search and find Solr datasets"() {
        when:
        def r = service.searchDatasetsByQueryTerm("temperature").block()

        then:
        r.size() > 0
    }
}
