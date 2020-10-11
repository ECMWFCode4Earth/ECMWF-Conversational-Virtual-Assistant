package com._2horizon.cva.dialogflow.manager.dialogflow

import com.google.api.gax.core.CredentialsProvider
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

/**
 * Created by Frank Lieber (liefra) on 2020-09-16.
 */
class DialogflowClientsHolder(
    private val credentialsProvider: CredentialsProvider,
    val projectId: String,
) {

    internal fun getSessionEntityTypesClient() =
        SessionEntityTypesClient.create(
            SessionEntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    internal fun getKnowledgeBasesClient() =
        KnowledgeBasesClient.create(
            KnowledgeBasesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    internal fun getDocumentsClient() =
        DocumentsClient.create(DocumentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    internal fun getContextsClient() =
        ContextsClient.create(ContextsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    internal fun getIntentsClient() =
        IntentsClient.create(IntentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    internal fun getAgentsClient() =
        AgentsClient.create(AgentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

    internal fun getEntityTypesClient() =
        EntityTypesClient.create(EntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())
}
