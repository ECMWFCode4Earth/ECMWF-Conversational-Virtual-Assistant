package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PayloadRoot(
    @JsonProperty("payload")
    val payload: CustomPayload
)
