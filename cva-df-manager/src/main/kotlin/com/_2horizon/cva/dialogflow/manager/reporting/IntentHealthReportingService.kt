package com._2horizon.cva.dialogflow.manager.reporting

import com._2horizon.cva.common.dialogflow.dto.Event
import com._2horizon.cva.common.dialogflow.dto.PayloadRoot
import com._2horizon.cva.common.dialogflow.dto.RichContentButtonItem
import com._2horizon.cva.common.dialogflow.dto.RichContentItem
import com._2horizon.cva.common.dialogflow.dto.RichContentListItem
import com._2horizon.cva.common.extensions.readValue
import com._2horizon.cva.dialogflow.manager.dialogflow.DialogflowClientsHolder
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.dialogflow.v2beta1.Intent
import com.google.cloud.dialogflow.v2beta1.IntentView
import com.google.cloud.dialogflow.v2beta1.ListIntentsRequest
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName
import com.google.protobuf.util.JsonFormat
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-13.
 */
@Singleton
@Requires(property = "app.feature.dialogflow.reporting.enabled", value = "true")
class IntentHealthReportingService(
    private val objectMapper: ObjectMapper,
    @param:Named("c3SDialogflowClientsHolder") private val c3s: DialogflowClientsHolder,
    @param:Named("camsDialogflowClientsHolder") private val cams: DialogflowClientsHolder,
    @param:Named("ecmwfDialogflowClientsHolder") private val ecmwf: DialogflowClientsHolder,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onStartUp(startupEvent: StartupEvent) {
        // val c3sIntentHealth = checkC3sIntentHealth()
        // val camsIntentHealth = checkCamsIntentHealth()
        // val ecmwfIntentHealth = checkEcmwfIntentHealth()
        // log.info("Done check intent health")
    }

    internal fun checkC3sIntentHealthDTO() =
        checkC3sIntentHealth().map { IntentHealthDTO(it.intent.displayName, it.errors) }

    internal fun checkCamsIntentHealthDTO() =
        checkCamsIntentHealth().map { IntentHealthDTO(it.intent.displayName, it.errors) }

    internal fun checkEcmwfIntentHealthDTO() =
        checkEcmwfIntentHealth().map { IntentHealthDTO(it.intent.displayName, it.errors) }

    internal fun checkC3sIntentHealth() = checkIntentHealth(listAllC3sIntents())
    internal fun checkCamsIntentHealth() = checkIntentHealth(listAllCamsIntents())
    internal fun checkEcmwfIntentHealth() = checkIntentHealth(listAllEcmwfIntents())

    internal fun listAllC3sIntents() = listAllIntents(c3s)
    internal fun listAllCamsIntents() = listAllIntents(cams)
    internal fun listAllEcmwfIntents() = listAllIntents(ecmwf)

    internal fun countC3sIntents() = listAllIntents(c3s).size
    internal fun countCamsIntents() = listAllIntents(cams).size
    internal fun countEcmwfIntents() = listAllIntents(ecmwf).size

    internal fun countC3sTrainingSentences() = aggNumberOfTrainingSentences(listAllIntents(c3s, true))
    internal fun countCamsTrainingSentences() = aggNumberOfTrainingSentences(listAllIntents(cams, true))
    internal fun countEcmwfTrainingSentences() = aggNumberOfTrainingSentences(listAllIntents(ecmwf, true))

    private fun aggNumberOfTrainingSentences(intents: List<Intent>): Int {
        return intents.map { it.trainingPhrasesCount }.sum()
    }

    private fun checkIntentHealth(allIntents: List<Intent>): List<IntentHealth> {
        val allEvents = allIntents.flatMap { intent -> intent.eventsList.map { it } }

        val validatedIntents = allIntents.map { intent: Intent ->

            val errors = mutableListOf<String>()

            errors.addAll(checkIntentNamings(intent))

            errors.addAll(checkDeadIntentEvents(intent, allEvents))

            IntentHealth(intent = intent, errors = errors)
        }

        val errorIntents = validatedIntents.filter { it.errors.isNotEmpty() }

        errorIntents.forEach { intentHealth ->
            println("Error for ${intentHealth.intent.displayName}: ${intentHealth.errors}")
        }

        return errorIntents
    }

    private fun checkDeadIntentEvents(intent: Intent, allEvents: List<String>): List<String> {
        val events = responseEvents(intent)
        return events.mapNotNull { event ->
            if (!allEvents.contains(event.name)) {
                "Event name not found: ${event.name}"
            } else {
                null
            }
        }
    }

    internal fun responseEvents(intent: Intent): List<Event> {
        return payloadResponses(intent).flatten().flatten()
            .filter { item -> item.type == "button" || item.type == "list" }
            .mapNotNull { item ->
                when (item) {
                    is RichContentButtonItem -> item.event
                    is RichContentListItem -> item.event
                    else -> error("Unknown Type")
                }
            }.filter { event -> event.name.isNotBlank() }
    }

    private fun payloadResponses(intent: Intent): List<List<List<RichContentItem>>> {
        val payloadMessages =
            intent.messagesList.filter { message -> message.hasPayload() }.map { JsonFormat.printer().print(it) }

        return payloadMessages.map { payloadMessage ->

            try {
                objectMapper.readValue<PayloadRoot>(payloadMessage).payload.richContent
            } catch (ex: Throwable) {
                log.error("Error while deserializing ${intent.displayName} with payload $payloadMessage")
                emptyList()
            }

        }
    }

    private fun textResponses(intent: Intent): List<String> =
        intent.messagesList.flatMap { message -> message.text.textList.map { it } }

    private fun numberOfTrainingSentences(intent: Intent): Int = intent.trainingPhrasesCount
    private fun numberOfMessages(intent: Intent): Int = intent.messagesCount
    private fun numberOfParameters(intent: Intent): Int = intent.parametersCount
    private fun inputContextNamesCount(intent: Intent): Int = intent.inputContextNamesCount
    private fun outputContextsCount(intent: Intent): Int = intent.outputContextsCount

    private fun checkIntentNamings(intent: Intent): List<String> {

        val errors = mutableListOf<String>()

        if (intent.webhookStateValue == 0) {
            errors.add("Webhook not enabled")
        }

        if (intent.displayName.contains(" ")) {
            errors.add("Intent name must not contain blanks")
        }

        if (!intent.eventsList.contains(intent.displayName)) {
            errors.add("Intent must have at least one event configured containing the intent name")
        }

        if (intent.action.isNotBlank() && intent.action != intent.displayName) {
            errors.add("Intent action must equal the intent name")
        }

        return errors
    }

    @JvmOverloads
    fun listAllIntents(dfClientsHolder: DialogflowClientsHolder, fullView: Boolean = false): List<Intent> {
        dfClientsHolder.getIntentsClient().use { intentsClient ->

            val lir = ListIntentsRequest.newBuilder()
                .setParent(ProjectAgentName.of(dfClientsHolder.projectId).toString())

            if (fullView) {
                lir.intentView = IntentView.INTENT_VIEW_FULL
            }

            return intentsClient.listIntents(lir.build()).iterateAll()
                .toList()
        }
    }
}

data class IntentHealth(val intent: Intent, val errors: List<String>)
data class IntentHealthDTO(val displayName: String, val errors: List<String>)
