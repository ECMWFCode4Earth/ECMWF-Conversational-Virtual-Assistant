package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RichContentImage(
    @JsonProperty("src")
    val src: RichContentImageSrc,
)
