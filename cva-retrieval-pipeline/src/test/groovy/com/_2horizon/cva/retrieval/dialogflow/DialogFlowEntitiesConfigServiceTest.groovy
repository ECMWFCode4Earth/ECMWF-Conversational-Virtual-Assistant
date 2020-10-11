package com._2horizon.cva.retrieval.dialogflow

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-07-01.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-cva")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-cva-0d9df7a1d3b4.json")
//@Property(name = "gcp.project-id", value = "aidradar-net")
//@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/flights-bot-13a6a6130910.json")
class DialogFlowEntitiesConfigServiceTest extends Specification {

    @Inject
    DialogFlowEntitiesConfigService service

    @Unroll
//    @Ignore
    def "Should listEntities"() {
        when:
        def entities = service.listEntities()

        then:
        entities.size() > 0

    }

    @Unroll
    @Ignore
    def "Should createEntities"() {
        given:
        def dfet = new DialogFlowEntityType("PUBLICATION_TYPE", [
                new DialogFlowEntity("newsletter", ["Newsletter"]),
                new DialogFlowEntity("annual report", ["Annual Report", "annualreport"])
        ])

        when:
        def ret = service.createEntities(dfet)

        then:
        ret.displayName == "PUBLICATION_TYPE"

    }
}
