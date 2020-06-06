package com._2horizon.cva.retrieval.ecmwf.publications

import com._2horizon.cva.retrieval.ecmwf.publications.dto.EcmwfPublicationDTO
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-06-06.
 */
@MicronautTest
@Property(name = "app.retrieval.ecmwf.publications-path", value = '')
class EcmwfPublicationsMetadataToFileSaverTest extends Specification {

    @Inject
    EcmwfPublicationsMetadataToFileSaver service

    @Unroll
    def "Should get LocationDataFlow of #identifier"() {
        when:
        EcmwfPublicationDTO r = service.readInLocalFileEcmwfPublicationDTO(new File("./src/test/resources/data/ecmwf/pubDTO/${nodeId}.json"))

        then:
        r.title == title

        where:
        nodeId | title
        19340  | 'A new hybrid formulation for the background error covariance in the IFS: evaluation'
    }
}
