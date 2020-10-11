package com._2horizon.cva.airtable

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotNull

/**
 * Created by Frank Lieber (liefra) on 2019-04-03.
 */
@ConfigurationProperties("app.airtable")
class AirtableConfig {

    @NotNull
    var apiKey: String? = null
}


