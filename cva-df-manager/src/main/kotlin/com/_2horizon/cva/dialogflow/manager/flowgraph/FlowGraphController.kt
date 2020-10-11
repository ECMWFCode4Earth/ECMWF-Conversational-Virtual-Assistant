package com._2horizon.cva.dialogflow.manager.flowgraph

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import reactor.core.publisher.Mono

/**
 * Created by Frank Lieber (liefra) on 2020-09-19.
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/flow-graph")
class FlowGraphController(
    private val flowGraphService: FlowGraphService,
) {

    @Get("/index/{agent}")
    fun index(
        agent: String
    ): Mono<String> {
        return when (agent) {
            "c3s" -> flowGraphService.c3sFlowGraph()
            "ecmwf" -> flowGraphService.ecmwfFlowGraph()
            else -> {
                error("No agent registered for $agent")
            }
        }
    }
}
