package com._2horizon.cva.retrieval.confluence


import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class ConfluenceExperimentalOperationsTest extends Specification {

    @Inject
    ConfluenceExperimentalOperations confluenceOperations


    @Unroll
    def "Should retrieve Confluence version of content #contentId"() {
        when:
        def response = confluenceOperations.contentVersion(contentId, version)

        then:
        response.get().user.displayName == user

        where:
        contentId | version | user
        133257478 | 4       | 'Michela Giusti'
        133257478 | 1       | 'Daniel Varela Santoalla'
    }
}
