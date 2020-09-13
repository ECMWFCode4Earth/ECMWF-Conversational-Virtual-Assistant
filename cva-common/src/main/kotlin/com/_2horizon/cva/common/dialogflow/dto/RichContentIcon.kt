package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentIcon(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("color")
    val color: String,
)
