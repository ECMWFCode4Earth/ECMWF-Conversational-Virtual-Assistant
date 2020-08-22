package com._2horizon.cva.dialogflow.fulfillment.copernicus

import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-07-06.
 */
@MicronautTest
class CopernicusDataStoreStatusOperationsTest extends Specification {

    @Inject
    CopernicusDataStoreStatusOperations api




    def "Should get the live activity status of the Copernicus Data Store"() {
        when:
        def r = api.liveActivityStatus().get()

        then:
        r.running > 0
    }
}
