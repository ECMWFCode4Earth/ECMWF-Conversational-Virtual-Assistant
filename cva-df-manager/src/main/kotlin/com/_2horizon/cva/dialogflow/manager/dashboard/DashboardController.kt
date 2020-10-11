package com._2horizon.cva.dialogflow.manager.dashboard

import com._2horizon.cva.dialogflow.manager.elastic.ConversionStep
import com._2horizon.cva.dialogflow.manager.elastic.ElasticConversionStepSearch
import com._2horizon.cva.dialogflow.manager.reporting.IntentHealthDTO
import com._2horizon.cva.dialogflow.manager.reporting.IntentHealthReportingService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-09-13.
 */
// @Secured(SecurityRule.IS_AUTHENTICATED)
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/dashboard")
class DashboardController(
    private val intentHealthReportingService: IntentHealthReportingService,
    private val elasticConversionStepSearch: ElasticConversionStepSearch,
) {

    // @Produces(MediaType.TEXT_PLAIN)
    // @Get("/index")
    // fun index(principal: Principal): String {
    //     return principal.name
    // }

    @Get("/intent-health/{agent}")
    fun intentHealth(agent: String): List<IntentHealthDTO> {
        return when (agent) {
            "c3s" -> intentHealthReportingService.checkC3sIntentHealthDTO()
            "cams" -> intentHealthReportingService.checkCamsIntentHealthDTO()
            "ecmwf" -> intentHealthReportingService.checkEcmwfIntentHealthDTO()
            else -> {
                error("No agent registered for $agent")
            }
        }
    }

    @Get("/intents-count/{agent}")
    fun intentsCount(agent: String): Int {
        return when (agent) {
            "c3s" -> intentHealthReportingService.countC3sIntents()
            "cams" -> intentHealthReportingService.countCamsIntents()
            "ecmwf" -> intentHealthReportingService.countEcmwfIntents()
            else -> {
                error("No agent registered for $agent")
            }
        }
    }

    @Get("/training-sentences-count/{agent}")
    fun trainingSentencesCount(agent: String): Int {
        return when (agent) {
            "c3s" -> intentHealthReportingService.countC3sTrainingSentences()
            "cams" -> intentHealthReportingService.countCamsTrainingSentences()
            "ecmwf" -> intentHealthReportingService.countEcmwfTrainingSentences()
            else -> {
                error("No agent registered for $agent")
            }
        }
    }

    @Get("/conversion-session-stats/{agent}/{type}")
    fun conversionSessionStats(agent: String, type: String): Mono<List<ConversionStep>> {

        val days = when (type) {
            "month" -> 30L
            "week" -> 7L
            "year" -> 365L
            else -> 30L
        }

        return when (agent) {
            "c3s" -> elasticConversionStepSearch.retrieveC3sConversionSessionStats(days)
            "ecmwf" -> elasticConversionStepSearch.retrieveEcmwfConversionSessionStats(days)
            else -> {
                error("No agent registered for $agent")
            }
        }
    }
}
