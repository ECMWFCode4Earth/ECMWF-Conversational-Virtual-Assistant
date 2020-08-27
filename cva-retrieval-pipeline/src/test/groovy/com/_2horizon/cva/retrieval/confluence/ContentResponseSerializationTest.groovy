package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.common.confluence.dto.content.ContentResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class ContentResponseSerializationTest extends Specification {

    @Inject
    ObjectMapper objectMapper
    


    def "Should read the content json string into ContentResponse"() {
        given:
        String json = new File("./src/test/resources/data/confluence/content/content-${spaceKey}.json").text

        when:
        ContentResponse contentResponse = objectMapper.readValue(json, ContentResponse)

        then:
        contentResponse.contents.size() == result
        contentResponse.contents.first().title == "Copernicus Knowledge Base"

        where:
        spaceKey | result
        "CKB"    | 10

    }
}
