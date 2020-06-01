package com._2horizon.cva.retrieval.copernicus.c3s.datasets

import com._2horizon.cva.retrieval.event.CdsCatalogueReceivedEvent
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-05-29.
 */
@Singleton
class ClimateDataStoreSyncService(
    private val cdsOperations: ClimateDataStoreOperations,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${app.feature.retrieval-pipeline.cds-enabled:false}") private val retrievalPipelineCdsEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartup(startupEvent: StartupEvent) {
        if (retrievalPipelineCdsEnabled) {
            retrieveClimateDataStoreCatalogue()
        } else {
            log.debug("ClimateDataStoreSyncService disabled")
        }
    }

    fun retrieveClimateDataStoreCatalogue() {

        log.info("Going to retrieveClimateDataStoreCatalogue")
        val uiResources = cdsOperations.getResources().get().take(2).map { cdsOperations.getUiResourceByKey(it).get() }
        applicationEventPublisher.publishEvent(CdsCatalogueReceivedEvent(uiResources))
    }
}
