package com._2horizon.cva.retrieval.dialogflow

import com.google.cloud.dialogflow.v2beta1.Intent
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Created by Frank Lieber (liefra) on 2020-06-29.
 */
@MicronautTest
@Property(name = "gcp.project-id", value = "ecmwf-cva")
@Property(name = "gcp.credentials.location", value = "/Users/liefra/data/02Projects/ECMWF/100-dev/ECMWF-virtual-assistant/ecmwf-cva-0d9df7a1d3b4.json")
class DialogFlowIntentConfigServiceTest extends Specification {

    @Inject
    DialogFlowIntentConfigService service


    @Unroll
    @Ignore
    def "Should listIntents"() {
        when:
        def intents = service.listAllIntents()

        then:
        intents.size() > 0
    }

    @Unroll
//    @Ignore
    def "Should createPublicationIntent"() {
        when:
        Intent i = service.createParameterIntent(displayName, trainingPhrasesList, messageTexts)

        then:
        i.displayName == displayName

        where:
        displayName | trainingPhrasesList                     | messageTexts
        'pub.test'  | ['What does ECMWF stand for?', 'ECMWF'] | ['European Centre for Medium-Range Weather Forecasts']
    }

    @Unroll
    @Ignore
    def "Should createIntent"() {
        when:
        Intent i = service.createIntent(displayName, trainingPhrasesList, messageTexts)

        then:
        i.displayName == displayName

        where:
        displayName | trainingPhrasesList                     | messageTexts
        'faq2.test'  | ['What does ECMWF stand for?', 'ECMWF'] | ['European Centre for Medium-Range Weather Forecasts']
    }

    @Unroll
    @Ignore
    def "Should findIndentByDisplayName"() {
        when:
        Intent i = service.findIndentByDisplayName(displayName, true)

        then:
        i.displayName == displayName

        where:
        displayName | trainingPhrasesList                     | messageTexts
        'pub.test'  | ['What does ECMWF stand for?', 'ECMWF'] | ['European Centre for Medium-Range Weather Forecasts']
    }


    @Unroll
    @Ignore
    def "Should updateIntent"() {
        given:
        Intent intent = service.findIndentByDisplayName(displayName, true)
        def fm = service.createTrainingPhrasesFieldMask()
        def updateIntent = service.buildIntent(trainingPhrasesList, messageTexts, displayName)

        when:
        Intent i = service.updateIntent(intent, updateIntent, fm)

        
        then:
        i.trainingPhrasesList == trainingPhrasesList
        i.displayName == displayName

        where:
        displayName | trainingPhrasesList                                          | messageTexts
        'faq.test'  | ['What does ECMWF stand for?', 'ECMWF', 'ECMWF means what?'] | ['European Centre for Medium-Range Weather Forecasts']
    }

    @Unroll
    @Ignore
    def "Should findIndentByIntentId"() {
        when:
        Intent i = service.findFullViewIndentByIntentId(indentUuid)

        then:
        assert i.name.endsWith(indentUuid)
        assert i.trainingPhrasesList.size()>0

        where:
        displayName | indentUuid
        'faq.test'  | 'e7fafc7e-2041-4c2c-9e47-b86db1ea3de5'
    }

}
