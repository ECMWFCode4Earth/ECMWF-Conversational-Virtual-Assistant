package com._2horizon.cva.dialogflow.manager.dialogflow.copernicus

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.CreateEntityTypeRequest
import com.google.cloud.dialogflow.v2beta1.EntityType
import com.google.cloud.dialogflow.v2beta1.EntityTypesClient
import com.google.cloud.dialogflow.v2beta1.EntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.ListEntityTypesRequest
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import io.micronaut.gcp.GoogleCloudConfiguration
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-03.
 */
@Singleton
class CopernicusDialogflowConfigurationService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider: FixedCredentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun createEntities(dfEntityType: DialogFlowEntityType): EntityType {

        val entities = dfEntityType.entities.map {
            EntityType.Entity.newBuilder().setValue(it.name).addAllSynonyms(it.synonyms).build()
        }

        val entityType = EntityType.newBuilder()
            .setDisplayName(dfEntityType.displayName)
            .setKind(EntityType.Kind.KIND_MAP)
            .setAutoExpansionMode(EntityType.AutoExpansionMode.AUTO_EXPANSION_MODE_UNSPECIFIED)  // Auto expansion disabled for the entity.
            .addAllEntities(entities)
            .build()

        val entityTypeRequest = CreateEntityTypeRequest.newBuilder()
            .setParent(ProjectAgentName.of(googleCloudConfiguration.projectId).toString())
            .setEntityType(entityType)
            .build()

        getEntityTypesClient().use { entityTypesClient ->
            return entityTypesClient.createEntityType(entityTypeRequest)
        }
    }

    fun listEntities(): List<EntityType> {

        getEntityTypesClient().use { entityTypesClient ->
            val listEntityTypesRequest = ListEntityTypesRequest.newBuilder()
                .setParent(
                    ProjectAgentName.of(googleCloudConfiguration.projectId).toString()
                )
                .build()

            val entityTypes =
                entityTypesClient.listEntityTypes(listEntityTypesRequest).iterateAll()
                    .toList()

            return entityTypes

        }
    }

    fun addCopernicusDataStoreType(): EntityType {

        TODO("not sure if this should be an entity")
        val entities = listOf(
            DialogFlowEntity("CDS", listOf("Climate Data Store", "Climate Data", "Climate Store")),
            DialogFlowEntity("tweet", listOf()),
            DialogFlowEntity("press release", listOf("Pressrelease")),
            DialogFlowEntity("event", listOf("Events"))
        )

        val communicationMediaType = DialogFlowEntityType(
            displayName = "communication_media_type",
            entities = entities
        )
        return createEntities(communicationMediaType)
    }

    fun addCommunicationMediaType(): EntityType {

        val entities = listOf(
            DialogFlowEntity("news", listOf()),
            DialogFlowEntity("tweet", listOf()),
            DialogFlowEntity("press release", listOf("Pressrelease")),
            DialogFlowEntity("event", listOf("Events"))
        )

        val communicationMediaType = DialogFlowEntityType(
            displayName = "communication_media_type",
            entities = entities
        )
        return createEntities(communicationMediaType)
    }

    private fun getEntityTypesClient() =
        EntityTypesClient.create(EntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())
}

data class DialogFlowEntityType(
    val displayName: String,
    val entities: List<DialogFlowEntity>
)

data class DialogFlowEntity(
    val name: String,
    val synonyms: List<String>
)


