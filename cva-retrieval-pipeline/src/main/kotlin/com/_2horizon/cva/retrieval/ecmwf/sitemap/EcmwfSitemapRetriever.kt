package com._2horizon.cva.retrieval.ecmwf.sitemap

import com._2horizon.cva.retrieval.event.SitemapEvent
import com._2horizon.cva.retrieval.sitemap.Sitemap
import com._2horizon.cva.retrieval.sitemap.SitemapRetrievalService
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-19.
 */
@Singleton
class EcmwfSitemapRetriever(
    private val sitemapRetrievalService: SitemapRetrievalService,
    @Value("\${app.feature.retrieval-pipeline.sitemap-enabled:false}") private val retrievalEcmwfSitemapsEnabled: Boolean,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalEcmwfSitemapsEnabled) {
            retrievalEcmwfSitemaps()
        }
    }

    private fun retrievalEcmwfSitemaps(): List<Sitemap> {
        val sitemaps = sitemapRetrievalService.retrieveEcmwfSitemaps()
        applicationEventPublisher.publishEvent(SitemapEvent(sitemaps))
        return sitemaps
    }
}
