package com._2horizon.cva.retrieval.dialogflow

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse
import com.google.cloud.dialogflow.v2beta1.KnowledgeAnswers
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesClient
import com.google.cloud.dialogflow.v2beta1.KnowledgeBasesSettings
import com.google.cloud.dialogflow.v2beta1.QueryInput
import com.google.cloud.dialogflow.v2beta1.QueryParameters
import com.google.cloud.dialogflow.v2beta1.QueryResult
import com.google.cloud.dialogflow.v2beta1.SessionName
import com.google.cloud.dialogflow.v2beta1.SessionsClient
import com.google.cloud.dialogflow.v2beta1.SessionsSettings
import com.google.cloud.dialogflow.v2beta1.TextInput
import io.micronaut.gcp.GoogleCloudConfiguration
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-06-29.
 */
@Singleton
class DialogFlowKnowledgeService(
    googleCredentials: GoogleCredentials,
    private val googleCloudConfiguration: GoogleCloudConfiguration
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val credentialsProvider = FixedCredentialsProvider.create(googleCredentials)

    fun detectIntentKnowledge(text:String): MutableMap<String, KnowledgeAnswers> {

       val kbs =  getKnowledgeBasesClient().use { knowledgeBasesClient ->
            knowledgeBasesClient.listKnowledgeBases("projects/${googleCloudConfiguration.projectId}").iterateAll().toList()
        }

        // Instantiates a client
        val allKnowledgeAnswers: MutableMap<String, KnowledgeAnswers> = mutableMapOf()

        getSessionsClient().use { sessionsClient ->
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            val session: SessionName = SessionName.of(googleCloudConfiguration.projectId, "${LocalDateTime.now().toEpochSecond(
                ZoneOffset.UTC)}")
            println("Session Path: $session")
            val textInput: TextInput.Builder = TextInput.newBuilder().setText(text).setLanguageCode("en-US")

            // Build the query with the TextInput
            val queryInput: QueryInput = QueryInput.newBuilder().setText(textInput).build()
            val queryParameters: QueryParameters = QueryParameters.newBuilder()  
                .addKnowledgeBaseNames( "projects/${googleCloudConfiguration.projectId}/knowledgeBases/OTAzNjExNjQxMDU1MTUwMDgw")
                .build()                                                                          
            val detectIntentRequest: DetectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .setQueryParams(queryParameters)
                .build()
            // Performs the detect intent request
            val response: DetectIntentResponse = sessionsClient.detectIntent(detectIntentRequest)

            // Display the query result
            val queryResult: QueryResult = response.queryResult
            System.out.format("Knowledge results:\n")
            System.out.format("====================\n")
            System.out.format("Query Text: '%s'\n", queryResult.queryText)
            System.out.format(
                "Detected Intent: %s (confidence: %f)\n",
                queryResult.intent.displayName, queryResult.intentDetectionConfidence
            )
            System.out.format("Fulfillment Text: '%s'\n", queryResult.fulfillmentText)
            val knowledgeAnswers: KnowledgeAnswers = queryResult.knowledgeAnswers
            for (answer in knowledgeAnswers.answersList) {
                System.out.format(" - Answer: '%s'\n", answer.answer)
                System.out.format(" - Confidence: '%s'\n", answer.matchConfidence)
            }
            val answers: KnowledgeAnswers = queryResult.knowledgeAnswers
            allKnowledgeAnswers[text] = answers
        }
        return allKnowledgeAnswers
    }

    private fun getKnowledgeBasesClient() =
        KnowledgeBasesClient.create(
            KnowledgeBasesSettings.newBuilder().setCredentialsProvider(credentialsProvider).build()
        )

    private fun getSessionsClient() =
        SessionsClient.create(SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build())

}
