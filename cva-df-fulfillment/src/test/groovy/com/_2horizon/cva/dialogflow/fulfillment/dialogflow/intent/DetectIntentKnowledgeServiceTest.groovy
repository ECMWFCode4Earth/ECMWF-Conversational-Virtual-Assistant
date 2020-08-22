package com._2horizon.cva.dialogflow.fulfillment.dialogflow.intent

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-07-11.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-cva-c3c")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-cva-c3c-da6a0854c00e.json")
class DetectIntentKnowledgeServiceTest extends Specification {

    @Inject
    DetectIntentKnowledgeService service


//    @Ignore
    def "Should get detectIntentKnowledge"() {
        when:
        def knowledgeBase = service.detectIntentKnowledge("GBON")

        then:
        noExceptionThrown()

    }
}
