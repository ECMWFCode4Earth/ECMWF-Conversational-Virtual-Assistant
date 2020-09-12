package com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications


import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
@MicronautTest
class EcmwfPublicationsSearchServiceTest extends Specification {


    @Inject
    EcmwfPublicationsSearchService service

    @Unroll
    def "Should extract #count results from #file"() {
        given:
        String html = new File("./src/test/resources/data/ecmwf/publications/${file}.html").text

        when:
        Integer numberOfResults = service.convert(html)

        then:
        numberOfResults == count

        where:
        file              | count
        'shipping-routes' | 12
        'nothing'         | 0

    }
}
