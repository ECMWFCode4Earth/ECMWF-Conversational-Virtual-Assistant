package com._2horizon.cva.retrieval.dialogflow

import com._2horizon.cva.retrieval.copernicus.dto.ui.UiResource
import com._2horizon.cva.retrieval.event.CopernicusCatalogueReceivedEvent
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-12.
 */
@Singleton
@Requires(property = "app.feature.dialogflow.copernicus.enabled", value = "true")
class CopernicusDialogflowEntityConfigService(
    private val dfEntitiesConfigService: DialogFlowEntitiesConfigService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun cdsCatalogueReceivedEvent(copernicusCatalogueReceivedEvent: CopernicusCatalogueReceivedEvent) {
        log.info("CopernicusDialogflowEntityConfigService CdsCatalogueReceivedEvent received")

        val uiResources: List<UiResource> = copernicusCatalogueReceivedEvent.uiResources
        val datastore = copernicusCatalogueReceivedEvent.datastore

        val datasets = uiResourcesToDfEntity(uiResources, "dataset")
        val datasetEntities = DialogFlowEntityType(
            "${datastore.name.toLowerCase()}_dataset",
            datasets
        )

        val applications = uiResourcesToDfEntity(uiResources, "application")
        val applicationEntities = DialogFlowEntityType(
            "${datastore.name.toLowerCase()}_application",
            applications
        )

        log.info("Going to createEntities in Dialogflow")
        dfEntitiesConfigService.createEntities(datasetEntities,true)
        dfEntitiesConfigService.createEntities(applicationEntities, true)
        log.info("DONE createEntities in Dialogflow")
    }

    private fun uiResourcesToDfEntity(uiResources: List<UiResource>, type: String): List<DialogFlowEntity> {
        return uiResources
            .filter { it.type == type }
            .map { r: UiResource ->
                DialogFlowEntity(r.title, listOf(r.name, r.title))
            }
    }
}


