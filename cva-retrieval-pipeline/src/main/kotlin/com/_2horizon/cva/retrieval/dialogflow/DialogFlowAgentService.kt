package com._2horizon.cva.retrieval.dialogflow

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.Agent
import com.google.cloud.dialogflow.v2beta1.AgentsClient
import com.google.cloud.dialogflow.v2beta1.AgentsSettings
import com.google.cloud.dialogflow.v2beta1.ContextsClient
import com.google.cloud.dialogflow.v2beta1.ContextsSettings
import com.google.cloud.dialogflow.v2beta1.DocumentsClient
import com.google.cloud.dialogflow.v2beta1.DocumentsSettings
import com.google.cloud.dialogflow.v2beta1.EntityTypesClient
import com.google.cloud.dialogflow.v2beta1.EntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.IntentsClient
import com.google.cloud.dialogflow.v2beta1.IntentsSettings
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesClient
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesSettings
import io.micronaut.context.event.StartupEvent
import io.micronaut.gcp.GoogleCloudConfiguration
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-28.
 *
 * @see [](https://cloud.google.com/dialogflow/docs/reference/common-types)
 */
@Singleton
class DialogFlowAgentService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration

) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    @EventListener
    fun doit(startupEvent: StartupEvent) {

        // log.info(googleCloudConfiguration.projectId)
        // agents()
    }


    fun agents(): Agent {

        getAgentsClient().use { agentsClient ->

            val agent = agentsClient.getAgent("projects/${googleCloudConfiguration.projectId}")

            return agent

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
