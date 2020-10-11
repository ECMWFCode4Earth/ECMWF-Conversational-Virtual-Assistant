package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class EcmwfFulfillmentState : FulfillmentState {
    FALLBACK_GLOBAL,
    NOTHING,
    WELCOME,
    SHOW_LATEST_TWEET,
    SEARCH_TWEETS_BY_KEYWORD,
    CONFLUENCE_SEARCH_BY_KEYWORD,
    SEARCH_PUBLICATIONS_BY_PUBLICATION_TYPE,
    QUERY_USER_INPUT_FOR_UDOC_SEARCH_KEYWORD,
}

fun ecmwfActionAsFulfillmentState(id: String): EcmwfFulfillmentState {
    return when (id) {
        "Default_Fallback_Intent" -> EcmwfFulfillmentState.FALLBACK_GLOBAL
        "Default_Welcome_Intent" -> EcmwfFulfillmentState.WELCOME
        "nothing" -> EcmwfFulfillmentState.NOTHING
        "show_latest_tweet" -> EcmwfFulfillmentState.SHOW_LATEST_TWEET
        "search_tweets_by_keyword" -> EcmwfFulfillmentState.SEARCH_TWEETS_BY_KEYWORD
        "confluence_search_by_keyword" -> EcmwfFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD
        "search_publications_by_publication_type" -> EcmwfFulfillmentState.SEARCH_PUBLICATIONS_BY_PUBLICATION_TYPE
        "query_user_input_for_udoc_search_keyword" -> EcmwfFulfillmentState.QUERY_USER_INPUT_FOR_UDOC_SEARCH_KEYWORD
        else -> error("FulfillmentState not defined $id")
    }
}


