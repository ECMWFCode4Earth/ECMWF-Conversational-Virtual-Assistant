package com._2horizon.cva.dialogflow.manager.dialogflow

import com._2horizon.cva.dialogflow.manager.dialogflow.model.DialogflowFaqConfigModel
import com._2horizon.cva.dialogflow.manager.event.DialogflowFaqConfigModelEvent
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import io.micronaut.context.annotation.Requires
import io.micronaut.gcp.GoogleCloudConfiguration
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-02.
 */
@Singleton
@Requires(property = "app.feature.dialogflow.configuration.copernicus.faq.enabled", value = "true")
class DialogflowFaqConfigurationService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration
) : AbstractDialogflowConfigurationService(googleCredentials) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun dialogflowFaqConfigModelEventReceived(event: DialogflowFaqConfigModelEvent) {
        log.debug("DialogflowFaqConfigModel Event Received with ${event.dfModels.size} faqs")

        event.dfModels.forEach {dfModel ->
            createIntent(dfModel)
           
        }

    }

    fun createIntent(dfConfig: DialogflowFaqConfigModel): Intent {
        val intent: Intent = buildIntent(dfConfig)

        getIntentsClient().use { intentsClient ->
            val response: Intent =
                intentsClient.createIntent(ProjectAgentName.of(googleCloudConfiguration.projectId), intent)
            System.out.format("Intent created: %s\n", response)
            return response
        }
    }

    private fun buildIntent(
        dfConfig: DialogflowFaqConfigModel
    ): Intent {
        // Build the trainingPhrases from the trainingPhrasesParts
        val trainingPhrases = dfConfig.trainingPhrases.map { trainingPhrase ->
            Intent.TrainingPhrase.newBuilder().addParts(
                Intent.TrainingPhrase.Part.newBuilder().setText(trainingPhrase).build()
            )
                .build()
        }

        val messages = Intent.Message.newBuilder()
            .setText(
                Intent.Message.Text.newBuilder()
                    .addAllText(dfConfig.responses).build()
            ).build()

        // Build the intent
        val intent: Intent = Intent.newBuilder()
            .setDisplayName(dfConfig.intentId)
            // .addAllEvents(listOf("FAQ_EVENT"))
            // .setWebhookState(Intent.WebhookState.WEBHOOK_STATE_ENABLED)
            .addMessages(messages)
            .addAllTrainingPhrases(trainingPhrases)
            .build()
        return intent
    }
}
