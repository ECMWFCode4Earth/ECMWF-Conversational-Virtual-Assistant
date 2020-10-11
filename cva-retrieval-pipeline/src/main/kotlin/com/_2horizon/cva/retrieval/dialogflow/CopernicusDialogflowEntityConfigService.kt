package com._2horizon.cva.retrieval.dialogflow

import com._2horizon.cva.copernicus.dto.solr.CopernicusSolrResult
import com._2horizon.cva.retrieval.event.CopernicusCatalogueReceivedEvent
import com.google.cloud.dialogflow.v2beta1.EntityType
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
    fun copernicusCatalogueReceived(copernicusCatalogueReceivedEvent: CopernicusCatalogueReceivedEvent) {
        log.info("CopernicusDialogflowEntityConfigService CdsCatalogueReceivedEvent received")

        val results: List<CopernicusSolrResult> = copernicusCatalogueReceivedEvent.results
        val datastore = copernicusCatalogueReceivedEvent.datastore

        val allEntities =
            dfEntitiesConfigService.listAllEntityTypes(dfEntitiesConfigService.lookupDialogflowClientsHolder(datastore.toString()))

        val datasetEntityType = updateEntityType(results, allEntities, "dataset", "cds_dataset")
        val applicationEntityType = updateEntityType(results, allEntities, "application", "cds_application")

        log.info("Going to update entities in Dialogflow")
        dfEntitiesConfigService.updateEntities(datastore.toString(), datasetEntityType)
        dfEntitiesConfigService.updateEntities(datastore.toString(), applicationEntityType)
        log.info("DONE update entities in Dialogflow")
    }

    private fun updateEntityType(
        uiResources: List<CopernicusSolrResult>,
        allEntities: List<EntityType>,
        type: String,
        displayName: String
    ): EntityType {
        val applications = uiResourcesToDfEntity(uiResources, type)
        val dfApplicationEntity = allEntities.first { it.displayName == displayName }
        val unkownApplicationEntities =
            applications.filterNot { dfApplicationEntity.entitiesList.map { entity -> entity.value }.contains(it.name) }
        val allApplicationEntityTypes = unkownApplicationEntities.map {
            EntityType.Entity.newBuilder().setValue(it.name).addAllSynonyms(it.synonyms).build()
        }
            .toMutableList().apply { addAll(dfApplicationEntity.entitiesList) }


        return EntityType.newBuilder()
            .setName(dfApplicationEntity.name)
            .setDisplayName(dfApplicationEntity.displayName)
            .setKind(dfApplicationEntity.kind)
            .setEnableFuzzyExtraction(dfApplicationEntity.enableFuzzyExtraction)
            .setAutoExpansionMode(dfApplicationEntity.autoExpansionMode) //https://cloud.google.com/dialogflow/docs/entities-options#expansion
            .addAllEntities(allApplicationEntityTypes)
            .build()
    }

    private fun uiResourcesToDfEntity(uiResources: List<CopernicusSolrResult>, type: String): List<DialogFlowEntity> {
        return uiResources
            .filter { it.type == type }
            .map { r: CopernicusSolrResult ->
                DialogFlowEntity(r.id, listOf(r.title, r.id))
            }
    }
}


