package com._2horizon.cva.retrieval.dialogflow

import com.google.cloud.dialogflow.v2beta1.CreateEntityTypeRequest
import com.google.cloud.dialogflow.v2beta1.EntityType
import com.google.cloud.dialogflow.v2beta1.ListEntityTypesRequest
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import com.google.cloud.dialogflow.v2beta1.UpdateEntityTypeRequest
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-28.
 *
 * @see [](https://cloud.google.com/dialogflow/docs/reference/common-types)
 */
@Singleton
class DialogFlowEntitiesConfigService(
    @param:Named("c3SDialogflowClientsHolder") private val c3s: DialogflowClientsHolder,
    @param:Named("camsDialogflowClientsHolder") private val cams: DialogflowClientsHolder,
    @param:Named("ecmwfDialogflowClientsHolder") private val ecmwf: DialogflowClientsHolder,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun updateEntities(
        agentName: String,
        entityType: EntityType,
    ): EntityType? {

        val dfClientsHolder = lookupDialogflowClientsHolder(agentName)

        val entityTypeRequest = UpdateEntityTypeRequest.newBuilder()
            .setEntityType(entityType)
            .build()

        dfClientsHolder.getEntityTypesClient().use { entityTypesClient ->
            return entityTypesClient.updateEntityType(entityTypeRequest)
        }
    }

    fun createEntities(
        agentName: String,
        dfEntityType: DialogFlowEntityType,
        fuzzyExtraction: Boolean = false,
        autoExpansionMode: EntityType.AutoExpansionMode = EntityType.AutoExpansionMode.AUTO_EXPANSION_MODE_UNSPECIFIED
    ): EntityType? {

        val entities = dfEntityType.entities.map {
            EntityType.Entity.newBuilder().setValue(it.name).addAllSynonyms(it.synonyms).build()
        }

        val entityType = EntityType.newBuilder()
            .setDisplayName(dfEntityType.displayName)
            .setKind(EntityType.Kind.KIND_MAP)
            .setEnableFuzzyExtraction(fuzzyExtraction)
            .setAutoExpansionMode(autoExpansionMode) //https://cloud.google.com/dialogflow/docs/entities-options#expansion
            .addAllEntities(entities)
            .build()

        val dfClientsHolder = lookupDialogflowClientsHolder(agentName)

        val entityTypeRequest = CreateEntityTypeRequest.newBuilder()
            .setParent(ProjectAgentName.of(dfClientsHolder.projectId).toString())
            .setEntityType(entityType)
            .build()

        dfClientsHolder.getEntityTypesClient().use { entityTypesClient ->
            return entityTypesClient.createEntityType(entityTypeRequest)
        }
    }

    internal fun lookupDialogflowClientsHolder(agentName: String): DialogflowClientsHolder {
        return when (agentName) {
            "ADS" -> cams
            "CDS" -> c3s
            "ECMWF" -> ecmwf
            else -> error("Agent name couldn't be resolved $agentName")
        }
    }

    // fun listEntityType(dfClientsHolder: DialogflowClientsHolder, entityTypeName: String): List<EntityType.Entity> {
    //     return dfClientsHolder.getEntityTypesClient().use { entityTypesClient ->
    //         entityTypesClient.getEntityType(entityTypeName).entitiesList
    //     }
    // }

    fun listAllEntityTypes(dfClientsHolder: DialogflowClientsHolder): List<EntityType> {

        dfClientsHolder.getEntityTypesClient().use { entityTypesClient ->
            val listEntityTypesRequest = ListEntityTypesRequest.newBuilder()
                .setParent(
                    ProjectAgentName.of(dfClientsHolder.projectId).toString()
                )
                .build()

            val entityTypes =
                entityTypesClient.listEntityTypes(listEntityTypesRequest).iterateAll()
                    .toList()

            return entityTypes

        }
    }
}

data class DialogFlowEntityType(
    val displayName: String,
    val entities: List<DialogFlowEntity>
)

data class DialogFlowEntity(
    val name: String,
    val synonyms: List<String>
)
