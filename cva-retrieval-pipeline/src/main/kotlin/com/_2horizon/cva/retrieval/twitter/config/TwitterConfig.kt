package com._2horizon.cva.retrieval.twitter.config

import io.micronaut.context.annotation.ConfigurationBuilder
import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotNull

/**
 * Created by Frank Lieber (liefra) on 2019-04-07.
 */
@ConfigurationProperties("app.twitter")
class TwitterConfig {

    @NotNull
    var consumerKey: String? = null

    @NotNull
    var consumerSecret: String? = null

    @NotNull
    var accessToken: String? = null

    @NotNull
    var accessTokenSecret: String? = null

    // @NotNull
    // var crawlerEnabled: Boolean? = null

    @ConfigurationBuilder(configurationPrefix = "crawler")
    val crawler = CrawlerProperties()

    class CrawlerProperties {
        var enabled: Boolean? = null
    }
}
