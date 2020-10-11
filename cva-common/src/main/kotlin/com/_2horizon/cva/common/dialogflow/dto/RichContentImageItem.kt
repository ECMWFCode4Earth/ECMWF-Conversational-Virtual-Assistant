package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentImageItem(
    @JsonProperty("rawUrl")
    val rawUrl: String,
    @JsonProperty("accessibilityText")
    val accessibilityText: String,
    @JsonProperty("type")
    override val type: String = "image"
) : RichContentItem
