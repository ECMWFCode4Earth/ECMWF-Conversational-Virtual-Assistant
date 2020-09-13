package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentImage(
    @JsonProperty("src")
    val src: RichContentImageSrc,
)
