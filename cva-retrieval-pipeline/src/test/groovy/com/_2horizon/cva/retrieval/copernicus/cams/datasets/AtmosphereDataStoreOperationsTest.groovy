package com._2horizon.cva.retrieval.copernicus.cams.datasets


import com._2horizon.cva.retrieval.copernicus.dto.Resource
import com._2horizon.cva.retrieval.copernicus.dto.TermsList
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
@MicronautTest
class AtmosphereDataStoreOperationsTest extends Specification {

    @Inject
    AtmosphereDataStoreOperations camsOperations

    def "Should retrieve resources list of AtmosphereDataStore API"() {
        when:
        Optional<List<String>> response = camsOperations.resources

        then:
        response.get().size() >= 5
    }

    def "Should retrieve UI resources list of AtmosphereDataStore API"() {
        when:
        Optional<List<String>> response = camsOperations.uiResources

        then:
        response.get().size() >= 5
    }

    @Unroll
    def "Should retrieve resource #key by key of AtmosphereDataStore API"() {
        when:
        Resource resource = camsOperations.getResourceByKey(key).get()

        then:
        resource.id == id
        resource.type == type

        where:
        key                                    | id                                                              | type
        'cams-global-greenhouse-gas-inversion' | 'eu.copernicus.atmosphere.cams-global-greenhouse-gas-inversion' | 'dataset'
        'cams-global-reanalysis-eac4-monthly'  | 'eu.copernicus.atmosphere.cams-global-reanalysis-eac4-monthly'  | 'dataset'
    }

    def "Should retrieve terms list of AtmosphereDataStore API"() {
        when:
        Optional<TermsList> response = camsOperations.termsList

        then:
        response.get().size() >= 32
    }

    def "Should retrieve UI terms list of AtmosphereDataStore API"() {
        when:
        Optional<TermsList> response = camsOperations.uiTermsList

        then:
        response.get().size() >= 32
    }
}
