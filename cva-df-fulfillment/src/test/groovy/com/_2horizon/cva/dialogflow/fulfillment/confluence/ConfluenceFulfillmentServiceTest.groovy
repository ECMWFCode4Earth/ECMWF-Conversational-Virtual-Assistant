package com._2horizon.cva.dialogflow.fulfillment.confluence


import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-08-25.
 */
@MicronautTest
class ConfluenceFulfillmentServiceTest extends Specification {

    @Inject
    ConfluenceFulfillmentService service

    def "Should searchByKeyword"() {
        when:
        def r = service.searchByKeyword('api').block()

        then:
        r.contents.size()>10
    }
}
