package com._2horizon.cva.retrieval.confluence

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-15.
 */
class ConfluenceExtensionKtTest extends Specification {

    @Unroll
    def "Should be able identify a ConfluenceSiteLink of #href"() {
        when:
        def r = ConfluenceExtensionKt.isConfluenceSpaceLink(href)

        then:
        r == result

        where:
        href                                                                                                            | result
        'https://confluence.ecmwf.int/display/ECAC'                                                                     | true
        'https://confluence.ecmwf.int/display/ECAC/'                                                                    | true
        'https://confluence.ecmwf.int//display/BUFR/BUFRDC+Home'                                                        | false
        'https://software.ecmwf.int/wiki/display/BUFR/BUFRDC+Home?preview=/35752427/36012655/bufr_reference_manual.pdf' | false
        'https://www.ecmwf.int/assets/elearning/eccodes/eccodes2/story_html5.html'                                      | false
    }

    @Unroll
    def "Should be able identify a ConfluencePageLink of #href"() {
        when:
        def r = ConfluenceExtensionKt.isConfluencePageLink(href)

        then:
        r == result

        where:
        href                                                                                                            | result
        'https://confluence.ecmwf.int/display/ECAC'                                                                     | false
        'https://confluence.ecmwf.int/display/ECAC/'                                                                    | false
        'https://confluence.ecmwf.int//display/BUFR/BUFRDC+Home'                                                        | true
        'https://software.ecmwf.int/wiki/display/BUFR/BUFRDC+Home?preview=/35752427/36012655/bufr_reference_manual.pdf' | true
        'https://www.ecmwf.int/assets/elearning/eccodes/eccodes2/story_html5.html'                                      | false
    }

    @Unroll
    def "Should be able identify a ConfluenceLink of #href"() {
        when:
        def r = ConfluenceExtensionKt.isConfluenceLink(href)

        then:
        r == result

        where:
        href                                                                                                            | result
        'https://confluence.ecmwf.int/display/ECAC'                                                                     | true
        'https://confluence.ecmwf.int//display/BUFR/BUFRDC+Home'                                                        | true
        'https://software.ecmwf.int/wiki/display/BUFR/BUFRDC+Home?preview=/35752427/36012655/bufr_reference_manual.pdf' | true
        'https://www.ecmwf.int/assets/elearning/eccodes/eccodes2/story_html5.html'                                      | false
    }

    @Unroll
    def "Should be able convertToCanonicalConfluenceLink of #href"() {
        when:
        def r = ConfluenceExtensionKt.convertToCanonicalConfluenceLink(href)

        then:
        r == result

        where:
        href                                                                                                            | result
        'https://confluence.ecmwf.int/display/ECAC'                                                                     | 'https://confluence.ecmwf.int/display/ECAC'
        'https://confluence.ecmwf.int//display/BUFR/BUFRDC+Home'                                                        | 'https://confluence.ecmwf.int/display/BUFR/BUFRDC+Home'
        'https://software.ecmwf.int/wiki/display/BUFR/BUFRDC+Home?preview=/35752427/36012655/bufr_reference_manual.pdf' | 'https://confluence.ecmwf.int/display/BUFR/BUFRDC+Home?preview=/35752427/36012655/bufr_reference_manual.pdf'
        'https://www.ecmwf.int/assets/elearning/eccodes/eccodes2/story_html5.html'                                      | 'https://www.ecmwf.int/assets/elearning/eccodes/eccodes2/story_html5.html'
    }
}
