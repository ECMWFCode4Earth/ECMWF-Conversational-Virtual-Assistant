package com._2horizon.cva.dialogflow.manager

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "cva-df-manager",
        version = "0.0"
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("cva-df-manager")
            .mainClass(Application.javaClass)
            .start()
    }
}
