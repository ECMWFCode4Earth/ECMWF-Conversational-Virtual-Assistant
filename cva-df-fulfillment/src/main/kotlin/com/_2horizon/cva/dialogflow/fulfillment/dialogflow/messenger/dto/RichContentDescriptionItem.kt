package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentDescriptionItem(

    @JsonProperty("title")
    val title: String,

    @JsonProperty("text")
    val text: List<String>,

    @JsonProperty("type")
    val type: String = "description"
):RichContentItem
