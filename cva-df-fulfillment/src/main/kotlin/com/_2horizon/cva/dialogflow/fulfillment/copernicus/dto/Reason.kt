package com._2horizon.cva.dialogflow.fulfillment.copernicus.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class Reason(
    @JsonProperty("message")
    val message: String,

    @JsonProperty("queued")
    val queued: Int,

    @JsonProperty("running")
    val running: Int
)
