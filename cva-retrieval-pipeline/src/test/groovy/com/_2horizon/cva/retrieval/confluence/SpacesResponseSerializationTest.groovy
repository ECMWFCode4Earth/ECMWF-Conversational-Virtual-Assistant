package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class SpacesResponseSerializationTest extends Specification {

    @Inject
    ObjectMapper objectMapper
    
    def "Should read the global spaces json string into SpacesResponse"() {
        given:
        String json = new File("./src/test/resources/data/confluence/spaces/global-space.json").text

        when:
        SpacesResponse spacesResponse = objectMapper.readValue(json, SpacesResponse)

        then:
        spacesResponse.size == 39
        spacesResponse.spaces.first().key == "AEOL"

    }
}
