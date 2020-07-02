package com._2horizon.cva.retrieval.confluence


import com._2horizon.cva.retrieval.confluence.dto.pagechildren.PageChildrenResponse
import com._2horizon.cva.retrieval.confluence.dto.space.SpacesResponse
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

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
        Optional<SpacesResponse> response = confluenceOperations.spacesWithMetadataLabelsAndDescriptionAndIcon(10, 0,'global')

        then:
        response.get().spaces.size() == 10

    }
//
//    def "Should retrieve Confluence pages content"() {
//        when:
//        Optional<ContentResponse> response = confluenceOperations.contentWithMetadataLabelsAndDescriptionAndIcon(spacesKey, limit, 0)
//
//        then:
//        response.get().contents.size() == limit
//
//        where:
//        spacesKey | limit
//        "CKB"     | 3
//    }

    @Unroll
    def "Should retrieve Confluence child pages of content #contentId"() {
        when:
        PageChildrenResponse response = confluenceOperations.contentWithChildPages(contentId, 500, 0)

        then:
        response.page.results.size() == results

        where:
        contentId | results
//        174866096 | 3
        55116796 | 5
    }

    @Unroll
    def "Should retrieve Confluence comments of content #contentId"() {
        when:
        def response = confluenceOperations.contentComments(contentId, size, 0)

        then:
        response.get().contents.size()==size

        where:
        contentId | size
        140380476 | 2
        153391781 | 1
    }

   
}
