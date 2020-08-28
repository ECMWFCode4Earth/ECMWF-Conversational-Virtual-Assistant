package com._2horizon.cva.nlp.corenlp

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-05-18.
 */
class POSTaggerServiceTest extends Specification {

    @Shared
    POSTaggerService service = new POSTaggerService()

    @Unroll
    def "Should questionDetectorStanfordCoreNLP"() {
        when:
        def isQuestion = service.questionDetectorStanfordCoreNLP(text)

        then:
        isQuestion == q

        where:
        text                                                                                   | q
        'How do I download ERA5 data for a given area?'                                        | true
        'What is the GRIB key stepUnits - ecCodes GRIB FAQ'                                    | true
        'How do I change the data structure in a message - ecCodes BUFR FAQ'                   | true
        'Is ecCodes thread-safe - ecCodes FAQ'                                                 | true
        'Can I use my own GRIB/BUFR sample files - ecCodes GRIB and BUFR FAQ'                  | true
        'What are namespaces - ecCodes GRIB FAQ'                                               | true
        'What is MARS?'                                                                        | true
        'Read the GTS bulletin keys as well as GRIB/BUFR messages - ecCodes BUFR and GRIB FAQ' | false
        'Manage your data files on our system (ECPDS)'                                         | false
    }


    @Unroll
    def "Should questionDetector"() {
        when:
        def isQuestion = service.questionDetector(text)

        then:
        isQuestion == q

        where:
        text                                                                                   | q
        'How do I download ERA5 data for a given area?'                                        | true
        'What is the GRIB key stepUnits - ecCodes GRIB FAQ'                                    | true
        'How do I change the data structure in a message - ecCodes BUFR FAQ'                   | true
        'Is ecCodes thread-safe - ecCodes FAQ'                                                 | true
        'Can I use my own GRIB/BUFR sample files - ecCodes GRIB and BUFR FAQ'                  | true
        'What are namespaces - ecCodes GRIB FAQ'                                               | true
        'What is MARS?'                                                                        | true
        'Python: How do I install the Python3 bindings - ecCodes FAQ'                          | true
        'How to use the CDS interactive forms for seasonal forecast datasets'                  | true
        'Read the GTS bulletin keys as well as GRIB/BUFR messages - ecCodes BUFR and GRIB FAQ' | false
        'Manage your data files on our system (ECPDS)'                                         | false
    }

}
