// package com._2horizon.cva.retrieval.airtable
//
// import com._2horizon.cva.retrieval.event.SignificantTermsReceivedEvent
// import dev.fuxing.airtable.AirtableApi
// import dev.fuxing.airtable.AirtableRecord
// import dev.fuxing.airtable.AirtableTable
// import dev.fuxing.airtable.exceptions.AirtableApiException
// import dev.fuxing.airtable.formula.AirtableFormula
// import dev.fuxing.airtable.formula.LogicalOperator
// import io.micronaut.context.annotation.Requires
// import io.micronaut.context.annotation.Value
// import io.micronaut.runtime.event.annotation.EventListener
// import org.slf4j.LoggerFactory
// import javax.inject.Singleton
//
// /**
//  * Created by Frank Lieber (liefra) on 2020-05-09.
//  */
// @Singleton
// @Requires(property = "app.feature.ingest-pipeline.airtable-abbrevations-ingest-enabled", value = "true")
// class AirtableAbbrevationsIngestionRepository(
//     @Value("\${app.airtable.retrieval.abbrevations}") private val abbrevationsBase: String,
//     api: AirtableApi
// ) {
//     private val log = LoggerFactory.getLogger(javaClass)
//
//     private val abbreviationTable = api.base(abbrevationsBase).table("Abbrevations")
//
//     @EventListener
//     fun spacesReceived(significantTermsReceivedEvent: SignificantTermsReceivedEvent) {
//         log.debug("SignificantTermsReceivedEvent received")
//
//         val datastore = significantTermsReceivedEvent.datastore
//         val significantTerms = significantTermsReceivedEvent.significantTerms
//
//         significantTerms.forEach significantTermsLoop@{ sigTerm ->
//
//             // only process unknown pagess
//             if (lookupAbbreviation(sigTerm.abbreviation) != null) {
//                 return@significantTermsLoop
//             }
//
//             val ar = AirtableRecord().apply {
//                 putField("Abbreviation", sigTerm.abbreviation)
//                 putField("Prefix", sigTerm.textAround)
//                 putField("Source", sigTerm.source)
//                 putField("Source URL", sigTerm.sourceUrl)
//                 putField("Occurrences", sigTerm.occurrences)
//                 putField("DB", datastore)
//             }
//
//             try {
//                 log.info("Going to save significantTerm ${sigTerm.abbreviation}")
//                 abbreviationTable.post(ar)
//             } catch (ex: AirtableApiException) {
//                 log.warn("Couldn't save because ${ex.type}: ${ex.message}")
//             }
//         }
//     }
//
//     private fun lookupAbbreviation(abbreviation: String) =
//         try {
//             abbreviationTable.list { querySpec: AirtableTable.QuerySpec ->
//                 querySpec.filterByFormula(
//                     LogicalOperator.EQ,
//                     AirtableFormula.Object.field("Abbreviation"),
//                     AirtableFormula.Object.value(abbreviation)
//                 )
//             }.firstOrNull()
//         } catch (ex: AirtableApiException) {
//             log.warn("Couldn't save because ${ex.type}: ${ex.message}")
//         }
// }
