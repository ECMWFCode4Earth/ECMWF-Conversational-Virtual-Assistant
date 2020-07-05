package com._2horizon.cva.dialogflow.fulfillment.dialogflow

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject


/**
 * Created by Frank Lieber (liefra) on 2020-07-02.
 */
@MicronautTest
class DialogflowFulfillmentControllerTest extends Specification {

    @Inject
    @Client("/")
    RxHttpClient client


    def "Should get LocationDataFlow of identifier"() {
        given:
        def body = new File("./src/test/resources/fulfillment/${file}.json").text
        def httpRequest = HttpRequest.POST("/fulfillment/request", body)

        when:
        def r = client.toBlocking().exchange(httpRequest)

        then:
        r.code() == 200

        where:
        file                | clazz
        'webhookRequest001' | 'LocationDataResponseFlow.Success'
    }

}
