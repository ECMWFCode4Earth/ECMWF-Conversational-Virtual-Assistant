package com._2horizon.cva.dialogflow.manager.dialogflow.copernicus

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-cva-c3c")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-cva-c3c-da6a0854c00e.json")
class CopernicusDialogflowConfigurationServiceTest extends Specification {

    @Inject
    CopernicusDialogflowConfigurationService service

    @Unroll
    @Ignore
    def "Should listEntities"() {
        when:
        def entities = service.listEntities()

        then:
        entities.size()>0

    }

    @Unroll
//    @Ignore
    def "Should create communication_media_type Entities"() {

        when:
        def ret = service.addCommunicationMediaType()

        then:
        ret.displayName == "communication_media_type"

    }
}
