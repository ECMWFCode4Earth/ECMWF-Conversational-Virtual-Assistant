package com._2horizon.cva.retrieval.twitter.api

import com._2horizon.cva.retrieval.twitter.config.TwitterConfig
import org.slf4j.LoggerFactory
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2019-04-08.
 */
@Singleton
class TwitterApiService(private val twitterConfig: TwitterConfig) {

    private val log = LoggerFactory.getLogger(TwitterApiService::class.java)

    val twitter: Twitter by lazy {
        val confBuilder = ConfigurationBuilder()
            .setOAuthConsumerKey(twitterConfig.consumerKey)
            .setOAuthConsumerSecret(twitterConfig.consumerSecret)
            .setOAuthAccessToken(twitterConfig.accessToken)
            .setOAuthAccessTokenSecret(twitterConfig.accessTokenSecret)
            .setTweetModeExtended(true)
            .setJSONStoreEnabled(true)
            .setIncludeEntitiesEnabled(true)

        twitter4j.TwitterFactory(confBuilder.build()).instance
    }
}
