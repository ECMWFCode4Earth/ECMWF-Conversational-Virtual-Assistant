package com._2horizon.cva.copernicus

import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
class CopernicusDataStoreAsyncSolrSearchServiceTest extends Specification {

    @Shared
    CopernicusDataStoreAsyncSolrSearchService service = new CopernicusDataStoreAsyncSolrSearchService()

    def "Should search and find all Solr datasets"() {
        when:
        def r = service.findAllDatasets().block()

        then:
        r.size() > 0
    }

    def "Should search and find one dataset"() {
        when:
        def r = service.findDatasetById(id).block()

        then:
        r.title == title

        where:
        id                                                         | title
        "eu.copernicus.climate.sis-tourism-fire-danger-indicators" | "Fire danger indicators for Europe from 1970 to 2098 derived from climate projections"
    }

    def "Should search and find one application"() {
        when:
        def r = service.findApplicationById(id).block()

        then:
        r.title == title

        where:
        id                                        | title
        "eu.copernicus.climate.app-era5-explorer" | "ERA5 explorer"
    }


    def "Should search and find Solr temperature datasets"() {
        when:
        def r = service.searchDatasetsByQueryTerm("temperature").block()

        then:
        r.size() > 0
    }
}
