package com._2horizon.cva.retrieval.copernicus.dto.ui


import com.fasterxml.jackson.annotation.JsonProperty

data class ContactPoint(
    @JsonProperty("contactType")
    val contactType: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("@type")
    val type: String,
    @JsonProperty("url")
    val url: String
)
