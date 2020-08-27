package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class FulfillmentState {
    CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK,
    CF_CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET,
    CF_CDS_DATASET_SHOW_CDS_API_REQUEST_OF_SELECTED_DATASET,
    CF_CDS_DATASET_EXECUTE_DATASET_SEARCH,
    CF_CKB_SEARCH_BY_KEYWORD,
    FALLBACK_GLOBAL,
    NOTHING,
}
fun actionAsFulfillmentState(id:String): FulfillmentState {
    return when(id){
        "cf_cds_dataset_search-dataset-by-name-or-keyword-fallback" -> FulfillmentState.CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK
        "cf_cds_dataset_execute-dataset-search" -> FulfillmentState.CF_CDS_DATASET_EXECUTE_DATASET_SEARCH
        "cf_cds_dataset_question_concerning_one_selected_dataset" -> FulfillmentState.CF_CDS_DATASET_QUESTION_CONCERNING_ONE_SELECTED_DATASET
        "cf_cds_dataset_show-cds-api-request-of-selected-dataset" -> FulfillmentState.CF_CDS_DATASET_SHOW_CDS_API_REQUEST_OF_SELECTED_DATASET
        "cf_ckb_search-by-keyword" -> FulfillmentState.CF_CKB_SEARCH_BY_KEYWORD
        "fallback.global" -> FulfillmentState.FALLBACK_GLOBAL
        "nothing" -> FulfillmentState.NOTHING
        else -> error("FulfillmentState not defined $id")
    }
}


