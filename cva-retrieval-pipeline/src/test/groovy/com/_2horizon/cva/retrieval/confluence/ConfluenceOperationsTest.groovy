package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.content.ContentResponse
import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
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
        Optional<SpacesResponse> response = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon(10,0)

        then:
        response.get().spaces.size() == 10

    }

    def "Should retrieve Confluence pages content"() {
        when:
        Optional<ContentResponse> response = confluenceOperations.contentWithMetadataLabelsAndDescriptionAndIcon(spacesKey, limit, 0)

        then:
        response.get().contents.size() == limit

        where:
        spacesKey | limit
        "CKB"     | 3
    }
}
