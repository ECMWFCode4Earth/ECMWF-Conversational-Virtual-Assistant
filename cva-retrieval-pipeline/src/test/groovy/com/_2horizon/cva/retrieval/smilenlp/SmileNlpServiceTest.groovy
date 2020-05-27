package com._2horizon.cva.retrieval.smilenlp


import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
@Ignore
@MicronautTest
@Property(name = "app.retrieval.ecmwf.publications-path", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/local-file-store/ecmwf/publications")
class SmileNlpServiceTest extends Specification {

    @Inject
    SmileNlpService service

    @Unroll
    def "Should get convert text to the correct number of sentences"() {
        given:
        String text = new File("./src/test/resources/data/nlp/sampletext/ecmwf/${textId}.txt").text


        when:
        List<String> sentences = service.textToSentences(text)

        then:
        sentences.size() == s

        where:
        textId  | s
        '19362' | 599
        '19367' | 68
    }

    // TODO: Not very useful like this
    def "Should analyseEcmwfPublications"() {
        when:
        def t = service.analyseEcmwfPublications()
        def sortedTopTerms = t.first
        def sortedBigrams = t.second

        then:
        sortedTopTerms.size() > 1000
        sortedBigrams.size() > 1000
    }


    // TODO: Not very useful like this
    def "Should getAllTextInBrackets"() {
        when:
        def t = service.getAllTextInBrackets()

        then:
        t.size() == 911

    }

    @Unroll
    def "Should convert local publication PDF file of #publicationId into text"() {
        when:
        List<String> sentences = service.analysePdf(new File("./src/test/resources/data/ecmwf/publications/pdf/${publicationId}.pdf"))

        then:
        sentences.size() == 1

        where:
        publicationId | start
        19307         | 'Part III: Dynamics and Numerical Procedures'
        19516         | 'COMPUTING'
    }


}
