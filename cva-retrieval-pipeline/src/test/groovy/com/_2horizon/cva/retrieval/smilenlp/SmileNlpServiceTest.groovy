package com._2horizon.cva.retrieval.smilenlp

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
class SmileNlpServiceTest extends Specification {

    @Shared
    SmileNlpService service = new SmileNlpService()

    @Unroll
    def "Should get convert text to the correct number of sentences"() {
        given:
        String text = new File("./src/test/resources/data/nlp/sampletext/bbc/${textId}.txt").text


        when:
        List<String> sentences = service.textToSentences(text)

        then:
        sentences.size() == 16

        where:
        textId | s
        '001'  | 0
    }




}
