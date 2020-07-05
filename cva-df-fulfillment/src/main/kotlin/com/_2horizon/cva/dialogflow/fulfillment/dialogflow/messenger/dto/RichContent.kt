package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContent(
    @JsonProperty("event")
    val event: Event,
    @JsonProperty("subtitle")
    val subtitle: String? = null,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("type")
    val type: String = "list"
)
