package com._2horizon.cva.dialogflow.fulfillment.ecmwf.publications

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-09-12.
 */
@Client("https://www.ecmwf.int/en/publications")
interface EcmwfPublicationSearchOperations {

    @Get("/search/{keyword}")
    fun search(@PathVariable keyword: String): Mono<String>
}
