package com._2horizon.cva.retrieval.dialogflow

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.AgentName
import com.google.cloud.dialogflow.v2beta1.AgentsClient
import com.google.cloud.dialogflow.v2beta1.AgentsSettings
import com.google.cloud.dialogflow.v2beta1.ContextsClient
import com.google.cloud.dialogflow.v2beta1.ContextsSettings
import com.google.cloud.dialogflow.v2beta1.DocumentsClient
import com.google.cloud.dialogflow.v2beta1.DocumentsSettings
import com.google.cloud.dialogflow.v2beta1.EntityTypesClient
import com.google.cloud.dialogflow.v2beta1.EntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.GetIntentRequest
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.IntentName
import com.google.cloud.dialogflow.v2beta1.IntentView
import com.google.cloud.dialogflow.v2beta1.IntentsClient
import com.google.cloud.dialogflow.v2beta1.IntentsSettings
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.ListIntentsRequest
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesClient
import com.google.cloud.dialogflow.v2beta1.SessionEntityTypesSettings
import com.google.cloud.dialogflow.v2beta1.UpdateIntentRequest
import com.google.protobuf.FieldMask
import io.micronaut.gcp.GoogleCloudConfiguration
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-28.
 *
 * @see [](https://cloud.google.com/dialogflow/docs/reference/common-types)
 */
@Singleton
class DialogFlowIntentConfigService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration

) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider: FixedCredentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    @JvmOverloads
    fun listAllIntents(fullView: Boolean = false): List<Intent> {
        getIntentsClient().use { intentsClient ->

            val lir = ListIntentsRequest.newBuilder()
                .setParent(ProjectAgentName.of(googleCloudConfiguration.projectId).toString())

            if (fullView) {
                lir.intentView = IntentView.INTENT_VIEW_FULL
            }

            return intentsClient.listIntents(lir.build()).iterateAll()
                .toList()
        }
    }

    fun createParameterIntent(
        displayName: String,
        trainingPhrasesList: List<String>,
        messageTexts: List<String>
    ): Intent {
        val intent: Intent = buildParameterIntent(trainingPhrasesList, messageTexts, displayName)

        getIntentsClient().use { intentsClient ->
            val response: Intent =
                intentsClient.createIntent(AgentName.of(googleCloudConfiguration.projectId), intent)
            System.out.format("Intent created: %s\n", response)
            return response
        }
    }

    private fun buildParameterIntent(
        trainingPhrasesList: List<String>,
        messageTexts: List<String>,
        displayName: String
    ): Intent {

        val parts = mutableListOf<Intent.TrainingPhrase.Part>()
        parts.add(Intent.TrainingPhrase.Part.newBuilder().setText("Show me the latest ").build())

        parts.add(
            Intent.TrainingPhrase.Part.newBuilder()
                .setText("annual report")
                .setUserDefined(true)
                .setAlias("publication_type")
                .setEntityType("@PUBLICATION_TYPE")
                .build()
        )

        // Build the trainingPhrases from the trainingPhrasesParts
        val trainingPhrases = trainingPhrasesList.map { trainingPhrase ->
            Intent.TrainingPhrase.newBuilder()
                // .setType(Intent.TrainingPhrase.Type.EXAMPLE)
                .addAllParts(parts)
                .build()
        }

        // Build the message texts for the agent's response
        val message = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addText("Here you go .... \$publication_type.original").build()
            ).build()

        val parameter = Intent.Parameter.newBuilder()
            .setMandatory(true)
            .setDisplayName("publication_type")
            .setValue("\$publication_type")
            .setEntityTypeDisplayName("@PUBLICATION_TYPE")
            .addAllPrompts(listOf("What publication type?"))

        // Build the intent
        val intent: Intent = Intent.newBuilder()
            .setDisplayName(displayName)
            // .addAllEvents(listOf("FAQ_EVENT"))
            // .setWebhookState(Intent.WebhookState.WEBHOOK_STATE_ENABLED)
            .setAction("ecmwf.publications.listbytype")
            .addParameters(parameter)
            .addMessages(message)
            .addAllTrainingPhrases(trainingPhrases)
            .build()
        return intent
    }

    fun createIntent(displayName: String, trainingPhrasesList: List<String>, messageTexts: List<String>): Intent {
        val intent: Intent = buildIntent(trainingPhrasesList, messageTexts, displayName)

        getIntentsClient().use { intentsClient ->
            val response: Intent =
                intentsClient.createIntent(AgentName.of(googleCloudConfiguration.projectId), intent)
            System.out.format("Intent created: %s\n", response)
            return response
        }
    }

    private fun buildIntent(
        trainingPhrasesList: List<String>,
        messageTexts: List<String>,
        displayName: String
    ): Intent {
        // Build the trainingPhrases from the trainingPhrasesParts
        val trainingPhrases = trainingPhrasesList.map { trainingPhrase ->
            Intent.TrainingPhrase.newBuilder().addParts(
                Intent.TrainingPhrase.Part.newBuilder().setText(trainingPhrase).build()
            )
                .build()
        }

        // Build the message texts for the agent's response
        val message = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(messageTexts).build()
            ).build()

        // Build the intent
        val intent: Intent = Intent.newBuilder()
            .setDisplayName(displayName)
            // .addAllEvents(listOf("FAQ_EVENT"))
            // .setWebhookState(Intent.WebhookState.WEBHOOK_STATE_ENABLED)
            .addMessages(message)
            .addAllTrainingPhrases(trainingPhrases)
            .build()
        return intent
    }

    fun findIndentByDisplayName(displayName: String, fullView: Boolean): Intent {
        val indents = listAllIntents(fullView).filter { it.displayName == displayName }
        check(indents.size == 1) { "More than one indent found with displayName $displayName" }
        return indents.first()
    }

    fun findFullViewIndentByIntentId(intentId: String): Intent {
        getIntentsClient().use { intentsClient ->
            val r = GetIntentRequest.newBuilder()
                .setIntentView(IntentView.INTENT_VIEW_FULL)
                .setName(IntentName.of(googleCloudConfiguration.projectId, intentId).toString())
                .build()
            return intentsClient.getIntent(r)
        }
    }

    fun updateIntent(intent: Intent, updateIntent: Intent, fieldMask: FieldMask): Intent {
        val r = UpdateIntentRequest.newBuilder().setIntent(intent)
            .mergeIntent(updateIntent)
            .setUpdateMask(fieldMask).build()

        getIntentsClient().use { intentsClient ->

            return intentsClient.updateIntent(r)
        }
    }

    private fun createTrainingPhrasesFieldMask(): FieldMask {
        return FieldMask.newBuilder()
            .addPaths("training_phrases")
            .build()
    }

    fun deleteIntent(intentId: String) {
        getIntentsClient().use { intentsClient ->
            val name = IntentName.of(googleCloudConfiguration.projectId, intentId)
            intentsClient.deleteIntent(name)
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
