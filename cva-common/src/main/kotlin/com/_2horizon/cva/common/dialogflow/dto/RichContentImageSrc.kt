package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentImageSrc(
    @JsonProperty("rawUrl")
    val rawUrl: String
)
