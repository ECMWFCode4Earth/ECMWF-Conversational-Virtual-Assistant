package com._2horizon.cva.retrieval.confluence

import org.jsoup.nodes.Document
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */
class StorageFormatUtilTest extends Specification {

    @Unroll
    def "Should create a document of StructuredStorageFormat of  nodeId #nodeId"() {
        when:
        Document document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        then:
        document.getAllElements().size() == s

        where:
        nodeId    | s
        177472176 | 119
        181122979 | 115
        171412993 | 9
    }

}
