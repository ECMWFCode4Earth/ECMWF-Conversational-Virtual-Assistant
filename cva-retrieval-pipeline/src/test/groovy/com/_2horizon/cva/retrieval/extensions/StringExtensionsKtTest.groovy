package com._2horizon.cva.retrieval.extensions


import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-31.
 */
class StringExtensionsKtTest extends Specification {


//    @Unroll
//    def "Should find #size text elements in brackets"() {
//        when:
//        List<TextInBrackets> r = StringExtensionsKt.extractTextInBrackets(text)
//
//        then:
//        r.collect { it.textInBrackets } == result
//
//        where:
//        result             | text
//        ['NUTS 2']         | 'showing in sequence the whole of Europe, European countries (as defined in the Eurostat NUTS 0 administrative levels) and European regions (NUTS 2). Variables in the dataset/application'
//        ['CAMS', 'NUTS 2'] | '(CAMS) and European regions (NUTS 2).\n\nVariables in the dataset/application'
//        []                  | 'and European regions.\n\nVariables in the dataset/application'
//    }

    @Unroll
    def "Should find #size uppercase text elements in brackets"() {
        when:
        List<TextInBrackets> r = StringExtensionsKt.extractUppercaseText(text)

        then:
        r.collect { it.textInBrackets }.toSet().sort().toList() == result

        where:
        result           | text
        ['NUTS']         | 'showing in sequence the whole of Europe, European countries (as defined in the Eurostat NUTS 0 administrative levels) and European regions (NUTS 2). Variables in the dataset/application'
        ['CAMS', 'NUTS'] | '(CAMS) and European regions (NUTS 2).\n\nVariables in the dataset/application'
        []               | 'and European regions.\n\nVariables in the dataset/application'
    }


}
