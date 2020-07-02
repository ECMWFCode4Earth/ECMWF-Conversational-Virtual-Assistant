package com._2horizon.cva.retrieval.dialogflow

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-06-29.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-manual-training-qmphuy")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-manual-training-qmphuy-47ffde11966e.json")
class DialogFlowKnowledgeServiceTest extends Specification {

    @Inject
    DialogFlowKnowledgeService service

    def "Should get detectIntentKnowledge"() {
        when:
        service.detectIntentKnowledge(text)

        then:
        noExceptionThrown()

        where:
        r | text
        2 | "What about ECMWF services for copernicus?"

    }
}
