package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class C3SFulfillmentState {
    CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK,
    CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET,
    CDS_DATASET_SHOW_CDS_API_REQUEST_OF_SELECTED_DATASET,
    CDS_DATASET_EXECUTE_DATASET_SEARCH,
    CKB_SEARCH_BY_KEYWORD,
    CDS_SHOW_LIVE_STATUS,
    FALLBACK_GLOBAL,
    NOTHING,
}
fun actionAsFulfillmentState(id:String): C3SFulfillmentState {
    return when(id){
        "cds_dataset_search_dataset_by_name_or_keyword_fallback" -> C3SFulfillmentState.CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK
        "cds_dataset_execute_dataset_search" -> C3SFulfillmentState.CDS_DATASET_EXECUTE_DATASET_SEARCH
        "cds_dataset_question_concerning_one_selected_dataset" -> C3SFulfillmentState.CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET
        "cds_dataset_show_cds_api_request_of_selected_dataset" -> C3SFulfillmentState.CDS_DATASET_SHOW_CDS_API_REQUEST_OF_SELECTED_DATASET
        "ckb_search_by_keyword" -> C3SFulfillmentState.CKB_SEARCH_BY_KEYWORD
        "cds_show_live_status" -> C3SFulfillmentState.CDS_SHOW_LIVE_STATUS
        "fallback.global" -> C3SFulfillmentState.FALLBACK_GLOBAL
        "nothing" -> C3SFulfillmentState.NOTHING
        else -> error("FulfillmentState not defined $id")
    }
}


