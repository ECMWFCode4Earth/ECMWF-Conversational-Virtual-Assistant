package com._2horizon.cva.dialogflow.fulfillment.dialogflow.intent

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse
import com.google.cloud.dialogflow.v2beta1.KnowledgeBase
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.ProjectName
import com.google.cloud.dialogflow.v2beta1.QueryInput
import com.google.cloud.dialogflow.v2beta1.QueryParameters
import com.google.cloud.dialogflow.v2beta1.QueryResult
import com.google.cloud.dialogflow.v2beta1.SessionName
import com.google.cloud.dialogflow.v2beta1.SessionsClient
import com.google.cloud.dialogflow.v2beta1.SessionsSettings
import com.google.cloud.dialogflow.v2beta1.TextInput
import io.micronaut.gcp.GoogleCloudConfiguration
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-07-11.
 */
@Singleton
class DetectIntentKnowledgeService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun detectIntentKnowledge(query:String): QueryResult {

        val knowledgeBase = findKnowledgeBaseByDisplayName("WIGOS")

        getSessionsClient().use { sessionsClient ->

            val session: SessionName = SessionName.of(googleCloudConfiguration.projectId, UUID.randomUUID().toString())

            val queryParameters: QueryParameters =
                QueryParameters.newBuilder().addKnowledgeBaseNames(knowledgeBase.name).build()

            val queryInput: QueryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(query).setLanguageCode("en")).build()

            val detectIntentRequest: DetectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .setQueryParams(queryParameters)
                .build()

            // Performs the detect intent request
            // Performs the detect intent request
            val response: DetectIntentResponse = sessionsClient.detectIntent(detectIntentRequest)

            // Display the query result

            // Display the query result
            val queryResult: QueryResult = response.queryResult

            return queryResult
        }
    }

    fun findKnowledgeBaseByDisplayName(displayName: String): KnowledgeBase {
        getKnowledgeBasesClient().use { knowledgeBasesClient ->
            return knowledgeBasesClient.listKnowledgeBases(ProjectName.of(googleCloudConfiguration.projectId))
                .iterateAll().first { it.displayName == displayName }
        }
    }

    private fun getKnowledgeBasesClient() =
        KnowledgeBasesClient.create(
            KnowledgeBasesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getSessionsClient() =
        SessionsClient.create(SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())
}
