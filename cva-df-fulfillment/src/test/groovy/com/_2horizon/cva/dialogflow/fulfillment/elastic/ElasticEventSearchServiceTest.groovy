package com._2horizon.cva.dialogflow.fulfillment.elastic

import com._2horizon.cva.common.elastic.ContentSource
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-09-21.
 */
@MicronautTest(propertySources = "classpath:application-test.yml")
class ElasticEventSearchServiceTest extends Specification {

    @Inject
    ElasticEventSearchService service

    def "Should findUpcomingEvents"() {
        when:
        def r = service.findUpcomingEvents(ContentSource.C3S).block()

        then:
        r.eventNodes.size() > 0

    }
}
