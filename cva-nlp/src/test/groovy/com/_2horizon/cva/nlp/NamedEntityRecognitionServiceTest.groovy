package com._2horizon.cva.nlp

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-13.
 */
class NamedEntityRecognitionServiceTest extends Specification {

    @Shared
    NamedEntityRecognitionService service = new NamedEntityRecognitionService()

    @Unroll
    def "Should get LocationDataFlow of #identifier"() {
        when:
        def r = service.detectNamedEntities(new File("./src/test/resources/data/corenlp/JuliaGillard.txt").text)

        then:
        noExceptionThrown()

        where:
        identifier                | text
        'TH-Chiang Saen-[010501]' | 'ERA5-Land is a land surface dataset, from 1981 (soon to be backdated to 1950) to present time (2 months in arrears), produced at higher resolution (9km) and forced by ERA5 atmospheric parameters with lapse rate correction, but with no additional data assimilation.'
    }
}
