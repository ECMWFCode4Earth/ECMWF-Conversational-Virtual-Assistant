package com._2horizon.cva.retrieval.copernicus.cams.portal

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@Client("https://atmosphere.copernicus.eu/")
@Retryable(attempts = "3", multiplier = "1.5")
interface CamsPortalOperations {

    @Get("/news")
    fun getNews(@QueryValue("q") q: String = "news", @QueryValue("page") page: Int = 0): Mono<String>

    @Get("/press-releases")
    fun getPressReleases(@QueryValue("q") q: String = "press-releases", @QueryValue("page") page: Int = 0): Mono<String>

    @Get("/events")
    fun getEvents(@QueryValue("q") q: String = "events", @QueryValue("page") page: Int = 0): Mono<String>
}



