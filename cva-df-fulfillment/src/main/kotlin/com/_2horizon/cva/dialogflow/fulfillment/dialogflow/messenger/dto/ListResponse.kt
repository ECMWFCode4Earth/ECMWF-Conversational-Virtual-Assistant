package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class ListResponse(
    @JsonProperty("richContent")
    val richContent: List<List<RichContent>>
)