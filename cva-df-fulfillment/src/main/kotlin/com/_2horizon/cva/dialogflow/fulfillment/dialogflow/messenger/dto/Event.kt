package com._2horizon.cva.dialogflow.fulfillment.dialogflow.messenger.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class Event(
    // @JsonProperty("languageCode")
    // val languageCode: String,
    @JsonProperty("name")
    val name: String
    // @JsonProperty("parameters")
    // val parameters: Parameters
)
