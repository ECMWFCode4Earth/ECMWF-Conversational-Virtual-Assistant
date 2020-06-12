package com._2horizon.cva.retrieval.nlp

import io.micronaut.context.event.ApplicationEventPublisher
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-08.
 */

class ConfluenceBasicNlpEnricherTest extends Specification {

    @Shared
    ConfluenceBasicNlpEnricher nlpEnricher = new ConfluenceBasicNlpEnricher(Mock(ApplicationEventPublisher))


    @Unroll
    def "Should find sentences in storage text of #nodeId"() {
        when:
        def sentences = nlpEnricher.findSentencesInStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 82
        181122979 | 14
        171412993 | 4
    }

    @Unroll
    def "Should find sentences in storage text of #nodeId using CoreNLP"() {
        when:
        def sentences = nlpEnricher.findCoreNlpSentencesInStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 82
        181122979 | 13
        171412993 | 3
    }






}
