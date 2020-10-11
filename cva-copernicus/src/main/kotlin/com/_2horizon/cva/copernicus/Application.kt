package com._2horizon.cva.retrieval

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("cva-copernicus")
            .mainClass(Application.javaClass)
            .start()
    }
}
