package com._2horizon.cva.nlp

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-27.
 */
class ExtractAbbrevServiceTest extends Specification {

    @Shared
    ExtractAbbrevService service = new ExtractAbbrevService()


    @Unroll
    def "Should extract #abbrevs abbreviation pairs in #file"() {
        when:
        def r = service.extractAbbrPairs(new File("./src/test/resources/data/nlp/sampletext/abbrev/${file}.txt").text)

        then:
        r.size() == abbrevs

        where:
        file                     | abbrevs
        'yeast_abbrev_unlabeled' | 616
        'test'                   | 1
    }
}
