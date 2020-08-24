package com._2horizon.cva.dialogflow.fulfillment

/**
 * Created by Frank Lieber (liefra) on 2020-08-24.
 */
enum class FulfillmentState {
    CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK,
    CF_CDS_DATASET_EXECUTE_DATASET_SEARCH,
    FALLBACK_GLOBAL,
    NOTHING,
}
fun actionAsFulfillmentState(id:String): FulfillmentState {
    return when(id){
        "cf_cds_dataset_search-dataset-by-name-or-keyword-fallback" -> FulfillmentState.CF_CDS_DATASET_SEARCH_DATASET_BY_NAME_OR_KEYWORD_FALLBACK
        "cf_cds_dataset_execute-dataset-search" -> FulfillmentState.CF_CDS_DATASET_EXECUTE_DATASET_SEARCH
        "fallback.global" -> FulfillmentState.FALLBACK_GLOBAL
        "nothing" -> FulfillmentState.NOTHING
        else -> error("FulfillmentState not defined $id")
    }
}


