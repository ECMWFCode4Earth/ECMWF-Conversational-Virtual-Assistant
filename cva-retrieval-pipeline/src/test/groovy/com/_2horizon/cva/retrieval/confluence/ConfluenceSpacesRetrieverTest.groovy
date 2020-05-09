package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class ConfluenceSpacesRetrieverTest extends Specification {

    @Inject
    ConfluenceSpacesRetriever retriever

    def "Should get LocationDataFlow of #identifier"() {
        when:
        SpacesResponse spacesResponse = retriever.retrieveSpaces()

        then:
        spacesResponse.spaces.size() > 0
    }
}
