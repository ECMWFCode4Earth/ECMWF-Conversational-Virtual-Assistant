package com._2horizon.cva.retrieval.copernicus.c3s.datasets

import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@MicronautTest
class ClimateDataStoreUiResourceOperationsTest extends Specification {

    @Inject
    ClimateDataStoreOperations c3sOperations

    def "Should retrieve all UI resources of ClimateDataStore API"() {
        given:
        List<String> resources = c3sOperations.resources.get()

        expect:
        resources.forEach({
            println("c3sOperations.getUiResourceByKey for $it")
            c3sOperations.getUiResourceByKey(it).get()
        })
    }

    @Unroll
    def "Should retrieve UI resource #key by key of ClimateDataStore API"() {
        when:
        UiResource uiResource = c3sOperations.getUiResourceByKey(key)get()

        then:
        uiResource.id == id
        uiResource.type == type

        where:
        key                          | id                                                 | type
        'cems-fire-historical'       | 'eu.copernicus.climate.cems-fire-historical'       | 'dataset'
        'sis-fisheries-ocean-fronts' | 'eu.copernicus.climate.sis-fisheries-ocean-fronts' | 'dataset'
    }




}
