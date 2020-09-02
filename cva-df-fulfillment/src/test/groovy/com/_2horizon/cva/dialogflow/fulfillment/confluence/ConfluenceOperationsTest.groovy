package com._2horizon.cva.dialogflow.fulfillment.confluence

import com._2horizon.cva.common.confluence.dto.content.ContentResponse
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class ConfluenceOperationsTest extends Specification {

    @Inject
    ConfluenceOperations confluenceOperations

    def "Should retrieve Confluence spaces"() {
        when:
        ContentResponse response = confluenceOperations.searchByKeyword("api",'CKB',200,0).block()

        then:
        response.contents.size() == 10


    }



   
}
