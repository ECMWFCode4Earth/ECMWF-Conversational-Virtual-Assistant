package com._2horizon.cva.dialogflow.manager.dialogflow.copernicus

import com.google.cloud.dialogflow.v2beta1.KnowledgeBase
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-07-10.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-cva-c3c")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-cva-c3c-da6a0854c00e.json")
class CopernicusDialogflowKnowledgeBaseServiceTest extends Specification {

    @Inject
    CopernicusDialogflowKnowledgeBaseService service


    @Ignore
    def "Should get createKnowledgeBase"() {
        when:
        KnowledgeBase knowledgeBase = service.createKnowledgeBase("CKB")

        then:
        knowledgeBase.displayName == 'CKB'

    }

    @Ignore
    def "Should addAllCKBDocuments"() {
        when:
        def r = service.addAllCKBDocuments()

        then:
        noExceptionThrown()
    }

    def "Should addAllFaqDocuments"() {
        when:
        def r = service.addAllFaqDocuments()

        then:
        noExceptionThrown()
    }


}
