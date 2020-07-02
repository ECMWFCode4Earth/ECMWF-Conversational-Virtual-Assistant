package com._2horizon.cva.dialogflow.fullfillment

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
            title = "cva-df-fullfillment",
            version = "0.0"
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("cva-df-fullfillment")
                .mainClass(Application.javaClass)
                .start()
    }
}
