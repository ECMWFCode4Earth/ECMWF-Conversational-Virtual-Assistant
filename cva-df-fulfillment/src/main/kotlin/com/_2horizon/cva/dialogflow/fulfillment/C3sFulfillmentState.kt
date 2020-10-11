package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class C3sFulfillmentState : FulfillmentState {
    CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET,
    CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_APPLICATION,
    CONFLUENCE_SEARCH_BY_KEYWORD,
    CDS_SHOW_LIVE_STATUS,
    PORTAL_LIST_EVENTS,
    PORTAL_SHOW_LATEST_COMMUNICATION_MEDIA_TYPE,
    PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD,
    FALLBACK_GLOBAL,
    WELCOME,
    NOTHING,
}

fun c3sActionAsFulfillmentState(id: String): C3sFulfillmentState {
    return when (id) {
        "cds_show_live_status" -> C3sFulfillmentState.CDS_SHOW_LIVE_STATUS
        "portal_show_latest_communication_media_type" -> C3sFulfillmentState.PORTAL_SHOW_LATEST_COMMUNICATION_MEDIA_TYPE
        "portal_search_communication_media_type_by_keyword" -> C3sFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD
        "confluence_search_by_keyword" -> C3sFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD
        "webportal_events__about_events" -> C3sFulfillmentState.PORTAL_LIST_EVENTS
        "Default_Fallback_Intent" -> C3sFulfillmentState.FALLBACK_GLOBAL
        "Default_Welcome_Intent" -> C3sFulfillmentState.WELCOME
        "cds_dataset_question_concerning_one_selected_dataset" -> C3sFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET
        "cds_dataset_question_concerning_one_selected_application" -> C3sFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_APPLICATION
        "nothing" -> C3sFulfillmentState.NOTHING
        else -> error("FulfillmentState not defined $id")
    }
}


