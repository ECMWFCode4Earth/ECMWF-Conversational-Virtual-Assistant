package com._2horizon.cva.retrieval.copernicus.cams.datasets


import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
@MicronautTest
class AtmosphereDataStoreUiResourcesTest extends Specification {

    @Inject
    AtmosphereDataStoreOperations camsOperations

    def "Should retrieve all UI resources of AtmosphereDataStore API"() {
        given:
        List<String> resources = camsOperations.resources.get()

        expect:
        resources.forEach({
            println("c3sOperations.getUiResourceByKey for $it")
            camsOperations.getUiResourceByKey(it).get()
        })
    }

    @Unroll
    def "Should retrieve UI resource #key by key of AtmosphereDataStore API"() {
        when:
        UiResource uiResource = camsOperations.getUiResourceByKey(key)get()

        then:
        uiResource.id == id
        uiResource.type == type

        where:
        key                          | id                                                 | type
        'cams-global-greenhouse-gas-inversion' | 'eu.copernicus.atmosphere.cams-global-greenhouse-gas-inversion' | 'dataset'
        'cams-global-reanalysis-eac4-monthly'  | 'eu.copernicus.atmosphere.cams-global-reanalysis-eac4-monthly'  | 'dataset'
    }




}
