package com._2horizon.cva.retrieval.copernicus

import com._2horizon.cva.copernicus.CopernicusDataStoreAsyncSolrSearchService
import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import com._2horizon.cva.retrieval.event.CopernicusCatalogueReceivedEvent
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@Singleton
class CopernicusDataStoreSyncService(
    private val copernicusDataStoreAsyncSolrSearchService: CopernicusDataStoreAsyncSolrSearchService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.copernicus.cds.enabled:false}") private val retrievalPipelineCdsEnabled: Boolean,
    @Value("\${app.feature.retrieval-pipeline.copernicus.ads.enabled:false}") private val retrievalPipelineAdsEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${app.feature.retrieval-pipeline.copernicus.data-store-cron}")
    fun copernicusDataStoreSync() {
        if (retrievalPipelineCdsEnabled) {
            retrieveClimateDataStoreCatalogue()
        } else {
            log.debug("ClimateDataStoreSyncService disabled")
        }

        if (retrievalPipelineAdsEnabled) {
            retrieveAtmosphereStoreCatalogue()
        } else {
            log.debug("ClimateDataStoreSyncService disabled")
        }
    }

    private fun retrieveAtmosphereStoreCatalogue() {
        log.info("Going to retrieveAtmosphereStoreCatalogue")

        copernicusDataStoreAsyncSolrSearchService.findAllApplications()
            .subscribe { results: List<CopernicusSolrResult> ->
                applicationEventPublisher.publishEvent(CopernicusCatalogueReceivedEvent(Datastore.ADS, results))
            }
    }

    private fun retrieveClimateDataStoreCatalogue() {
        log.info("Going to retrieveClimateDataStoreCatalogue")

        copernicusDataStoreAsyncSolrSearchService.findAllDatasets()
            .subscribe { results: List<CopernicusSolrResult> ->
                applicationEventPublisher.publishEvent(CopernicusCatalogueReceivedEvent(Datastore.CDS, results))
            }
    }
}

enum class Datastore {
    ADS, CDS, ECMWF_PUBLICATIONS, CONFLUENCE
}
