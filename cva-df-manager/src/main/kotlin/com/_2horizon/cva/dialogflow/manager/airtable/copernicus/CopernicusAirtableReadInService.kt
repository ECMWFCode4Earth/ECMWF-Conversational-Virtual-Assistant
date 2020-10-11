package com._2horizon.cva.dialogflow.manager.airtable.copernicus

import com._2horizon.cva.dialogflow.manager.dialogflow.model.DialogflowFaqConfigModel
import com._2horizon.cva.dialogflow.manager.event.DialogflowFaqConfigModelEvent
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableRecord
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-08-02.
 */
@Singleton
@Requires(property = "app.feature.dialogflow.configuration.copernicus.faq.enabled", value = "true")
class CopernicusAirtableReadInService(
    @Value("\${app.airtable.dialogflow.copernicus.c3s.faq}") private val faqBase: String,
    api: AirtableApi,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val faqTable = api.base(faqBase).table("C3S")

    @EventListener
    fun onStartUp(startupEvent: StartupEvent) {
        readInFaqs()
    }

    private fun readInFaqs() {

        val faqs = mutableListOf<DialogflowFaqConfigModel>()

        faqTable.iterator().forEachRemaining { r: AirtableRecord ->

            val readyForUpload = r.getFieldBoolean("For upload")

            if (readyForUpload != null && readyForUpload) {

                val intentId = r.getFieldString("IntentID") ?: error("IntentID field not found")

                val intentName = r.getFieldString("Intent Name") ?: error("Intent Name field not found")

                val trainingPhrases = readTrainingPhrases(r)

                val responses = readResponses(r)

                val dfModel = DialogflowFaqConfigModel(
                    intentId = intentId,
                    intentName = intentName,
                    trainingPhrases = trainingPhrases,
                    responses = responses
                )
                faqs.add(dfModel)
            }

        }

        applicationEventPublisher.publishEvent(DialogflowFaqConfigModelEvent(faqs))
    }

    private fun readResponses(r: AirtableRecord): Set<String> {
        val responses = mutableSetOf<String>()
        for (i in 0..10) {
            val aResponse = r.getFieldString("Answer${i.toString().padStart(2, '0')}")
            if (aResponse != null) {
                responses.add(aResponse)
            }
        }
        return responses
    }

    private fun readTrainingPhrases(r: AirtableRecord): Set<String> {
        val trainingPhrases = mutableSetOf<String>()
        for (i in 0..10) {
            val aTrainingPhrase = r.getFieldString("Train${i.toString().padStart(2, '0')}")
            if (aTrainingPhrase != null) {
                trainingPhrases.add(aTrainingPhrase)
            }
        }
        return trainingPhrases
    }
}
