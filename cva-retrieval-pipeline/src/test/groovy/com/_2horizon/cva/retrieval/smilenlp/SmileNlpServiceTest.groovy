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
        when:
        List<String> sentences = service.textToSentences()

        then:
        sentences.size() == 11
    }


}
