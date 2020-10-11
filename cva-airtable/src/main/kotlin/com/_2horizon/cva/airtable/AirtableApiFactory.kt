package com._2horizon.cva.airtable

import dev.fuxing.airtable.AirtableApi
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory

/**
 * Created by Frank Lieber (liefra) on 2020-05-06.
 */
@Factory
class AirtableApiFactory(private val config: AirtableConfig) {

    @Bean
    fun api(): AirtableApi {
        return AirtableApi(config.apiKey!!)
    }
}
