package com._2horizon.cva.retrieval.dialogflow

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.AgentsClient
import com.google.cloud.dialogflow.v2beta1.AgentsSettings
import com.google.cloud.dialogflow.v2beta1.ContextsClient
import com.google.cloud.dialogflow.v2beta1.ContextsSettings
import com.google.cloud.dialogflow.v2beta1.CreateEntityTypeRequest
import com.google.cloud.dialogflow.v2beta1.DocumentsClient
import com.google.cloud.dialogflow.v2beta1.DocumentsSettings
import com.google.cloud.dialogflow.v2beta1.EntityType
import com.google.cloud.dialogflow.v2beta1.EntityTypesClient
import com.google.cloud.dialogflow.v2beta1.EntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.IntentsClient
import com.google.cloud.dialogflow.v2beta1.IntentsSettings
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.ListEntityTypesRequest
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesClient
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesSettings
import io.micronaut.gcp.GoogleCloudConfiguration
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-28.
 *
 * @see [](https://cloud.google.com/dialogflow/docs/reference/common-types)
 */
@Singleton
class DialogFlowEntitiesConfigService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration

) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider: FixedCredentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun createEntities(dfEntityType:DialogFlowEntityType): EntityType? {

       val entities =  dfEntityType.entities.map {
             EntityType.Entity.newBuilder().setValue(it.name) .addAllSynonyms(it.synonyms).build()
        }

        val entityType =  EntityType.newBuilder()
            .setDisplayName(dfEntityType.displayName)
            .setKind(EntityType.Kind.KIND_MAP)
            .setAutoExpansionMode(EntityType.AutoExpansionMode.AUTO_EXPANSION_MODE_UNSPECIFIED)  // Auto expansion disabled for the entity.
            .addAllEntities(entities)
            .build()

        val entityTypeRequest =   CreateEntityTypeRequest.newBuilder()
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

    private fun getSessionEntityTypesClient() =
        SessionEntityTypesClient.create(
            SessionEntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getKnowledgeBasesClient() =
        KnowledgeBasesClient.create(
            KnowledgeBasesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getDocumentsClient() =
        DocumentsClient.create(DocumentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    private fun getContextsClient() =
        ContextsClient.create(ContextsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    private fun getIntentsClient() =
        IntentsClient.create(IntentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    private fun getAgentsClient() =
        AgentsClient.create(AgentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    private fun getEntityTypesClient() =
        EntityTypesClient.create(EntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())
}

data class DialogFlowEntityType(
    val displayName: String,
    val entities:List<DialogFlowEntity>
)

data class DialogFlowEntity(
    val name:String,
    val synonyms:List<String>
)
