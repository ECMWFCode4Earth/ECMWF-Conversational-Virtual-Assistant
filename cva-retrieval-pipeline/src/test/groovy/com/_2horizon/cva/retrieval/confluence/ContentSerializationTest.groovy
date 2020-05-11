package com._2horizon.cva.retrieval.confluence

import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-09.
 */
@MicronautTest
class ContentSerializationTest extends Specification {

    @Inject
    ObjectMapper objectMapper

   @Unroll
    def "Should read the content #contentID json string into Content"() {
        given:
        String json = new File("./src/test/resources/data/confluence/content/content-${contentID}.json").text

        when:
        Content content = objectMapper.readValue(json, Content)

        then:
        content.title == title

        where:
        contentID    | title
        "133262398" | 'ERA5: How to calculate wind speed and wind direction from u and v components of the wind?'
        "140385202" | 'ERA5-Land: data documentation'

    }
}
