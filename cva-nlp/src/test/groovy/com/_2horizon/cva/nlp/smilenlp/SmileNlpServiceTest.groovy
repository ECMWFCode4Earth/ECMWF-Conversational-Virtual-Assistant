package com._2horizon.cva.nlp.smilenlp


import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */

@MicronautTest
@Property(name = "app.retrieval.ecmwf.publications-path", value = "/Volumes/DeepT5/02-Ext-Projects/ECMWF/local-file-store/ecmwf/publications")
class SmileNlpServiceTest extends Specification {

    @Inject
    SmileNlpService service

    @Unroll
    @Ignore
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
    @Ignore
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
    @Ignore
    def "Should getAllTextInBrackets"() {
        when:
        def t = service.getAllTextInBrackets()

        then:
        t.size() == 911

    }

    @Unroll
    @Ignore
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

    @Unroll
    @Ignore
    def "Should split into sentences"() {
        when:
        def sentences = service.breakTextToSentences(text)

        then:
        sentences.size() == noSentences

        where:
        noSentences | text
        2           | 'The model surface is logically divided into sea and land points by using a land-sea mask. A grid point is defined as a land point, if more than 50 per cent of the actual surface of the grid-box is land, for example with a TL511 resolution, islands like Corsica, Crete and Cyprus are represented by around five land grid points each.'
        1           | 'When does the information inserted in OSCAR/Surface (e.g., a new station.) show in the WIGOS Data Quality Monitoring System webtool?'
    }

    @Unroll

    def "Should pos"() {
        when:
        def pos = service.pos(aSentence)

        then:
        pos.size() == 13

        where:
        aSentence                                                      | size
        'What bulletin type does NCEP assimilate, TAC, BUFR or both?' | 2

    }


}
