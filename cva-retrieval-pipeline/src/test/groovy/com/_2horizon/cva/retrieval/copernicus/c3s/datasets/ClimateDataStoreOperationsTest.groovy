package com._2horizon.cva.retrieval.copernicus.c3s.datasets

import com._2horizon.cva.retrieval.copernicus.dto.Resource
import com._2horizon.cva.retrieval.copernicus.dto.TermsList
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@MicronautTest
class ClimateDataStoreOperationsTest extends Specification {

    @Inject
    ClimateDataStoreOperations c3sOperations

    def "Should retrieve resources list of ClimateDataStore API"() {
        when:
        Optional<List<String>> response = c3sOperations.resources

        then:
        response.get().size() >= 90
    }

    def "Should retrieve UI resources list of ClimateDataStore API"() {
        when:
        Optional<List<String>> response = c3sOperations.uiResources

        then:
        response.get().size() >= 90
    }

    @Unroll
    def "Should retrieve resource #key by key of ClimateDataStore API"() {
        when:
        Resource resource = c3sOperations.getResourceByKey(key).get()

        then:
        resource.id == id
        resource.type == type

        where:
        key                          | id                                                 | type
        'cems-fire-historical'       | 'eu.copernicus.climate.cems-fire-historical'       | 'dataset'
        'sis-fisheries-ocean-fronts' | 'eu.copernicus.climate.sis-fisheries-ocean-fronts' | 'dataset'
    }

    def "Should retrieve terms list of ClimateDataStore API"() {
        when:
        Optional<TermsList> response = c3sOperations.termsList

        then:
        response.get().size() >= 32
    }

    def "Should retrieve UI terms list of ClimateDataStore API"() {
        when:
        Optional<TermsList> response = c3sOperations.uiTermsList

        then:
        response.get().size() >= 32
    }


}
