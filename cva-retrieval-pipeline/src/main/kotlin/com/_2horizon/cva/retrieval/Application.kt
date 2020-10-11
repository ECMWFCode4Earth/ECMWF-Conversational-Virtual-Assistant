package com._2horizon.cva.retrieval

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import java.util.TimeZone

@OpenAPIDefinition(
    info = Info(
        title = "cva-retrieval-pipeline",
        version = "0.0"
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Micronaut.build()
            .packages("cva-retrieval-pipeline")
            .mainClass(Application.javaClass)
            .start()
    }
}
