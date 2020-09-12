package com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications

import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
@MicronautTest
class EcmwfPublicationSearchOperationsTest extends Specification {

    @Inject
    EcmwfPublicationSearchOperations operations


    def "Should execute a keyword search"() {
        when:
        String r = operations.search('shipping routes').block()

        then:
        noExceptionThrown()
    }
}
