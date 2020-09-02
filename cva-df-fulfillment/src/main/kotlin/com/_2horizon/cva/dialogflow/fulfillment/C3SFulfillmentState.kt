package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class C3SFulfillmentState {
    CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK,
    CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET,
    CDS_DATASET_EXECUTE_DATASET_SEARCH,
    CONFLUENCE_SEARCH_BY_KEYWORD,
    CDS_SHOW_LIVE_STATUS,
    PORTAL_SHOW_LATEST_COMMUNICATION_MEDIA_TYPE,
    PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD,
    FALLBACK_GLOBAL,
    NOTHING,
}
fun actionAsFulfillmentState(id:String): C3SFulfillmentState {
    return when(id){
        "cds_dataset_search_dataset_by_name_or_keyword_fallback" -> C3SFulfillmentState.CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK
        "cds_dataset_execute_dataset_search" -> C3SFulfillmentState.CDS_DATASET_EXECUTE_DATASET_SEARCH
        "cds_dataset_question_concerning_one_selected_dataset" -> C3SFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET
        "confluence_search_by_keyword" -> C3SFulfillmentState.CONFLUENCE_SEARCH_BY_KEYWORD
        "cds_show_live_status" -> C3SFulfillmentState.CDS_SHOW_LIVE_STATUS
        "portal_show_latest_communication_media_type" -> C3SFulfillmentState.PORTAL_SHOW_LATEST_COMMUNICATION_MEDIA_TYPE
        "portal_search_communication_media_type_by_keyword" -> C3SFulfillmentState.PORTAL_SEARCH_COMMUNICATION_MEDIA_TYPE_BY_KEYWORD
        "fallback.global" -> C3SFulfillmentState.FALLBACK_GLOBAL
        "nothing" -> C3SFulfillmentState.NOTHING
        else -> error("FulfillmentState not defined $id")
    }
}


