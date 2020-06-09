package com._2horizon.cva.retrieval.nlp


import io.micronaut.context.event.ApplicationEventPublisher
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import spock.lang.Ignore
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
    def "Should find questions in storage text of #nodeId"() {
        when:
        def r = nlpEnricher.findQuestionsInStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text)

        then:
        noExceptionThrown()

        where:
        nodeId    | clazz
        181122979 | 'LocationDataResponseFlow.Success'
        171412993 | 'LocationDataResponseFlow.Success'
        65232123 | 'LocationDataResponseFlow.Success'
    }

    @Unroll
    @Ignore
    def "Should find sentences in storage text of #nodeId"() {
        when:
        def sentences = nlpEnricher.findSentencesInStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        then:
        sentences.size() == s

        where:
        nodeId    | s
        177472176 | 14
        181122979 | 14
        171412993 | 4
    }

    @Unroll
    def "Should find findStrucutredStorageFormat in storage text of #nodeId"() {
        given:
        def doc = Jsoup.parse(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text,"", Parser.xmlParser())

        when:
        def r = nlpEnricher.findStrucutredStorageFormat(doc)

        then:
        noExceptionThrown()

        where:
        nodeId    | dummy
        181122979 | 'dummy'
        171412993 | 'dummy'
        65232123 | 'dummy'
    }




}
