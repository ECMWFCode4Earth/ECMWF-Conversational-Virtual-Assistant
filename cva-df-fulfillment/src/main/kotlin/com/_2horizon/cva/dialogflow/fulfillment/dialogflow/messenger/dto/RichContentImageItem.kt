package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentImageItem(
    @JsonProperty("rawUrl")
    val rawUrl: String,
    @JsonProperty("accessibilityText")
    val accessibilityText: String,
    @JsonProperty("type")
    val type: String = "image"
):RichContentItem
