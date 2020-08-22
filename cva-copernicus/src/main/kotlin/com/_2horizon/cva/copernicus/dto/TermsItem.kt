package com._2horizon.cva.copernicus.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class TermsItem(
    @JsonProperty("display_url")
    val displayUrl: String,
    @JsonProperty("download_url")
    val downloadUrl: String,
    @JsonProperty("revision")
    val revision: Int,
    @JsonProperty("terms_id")
    val termsId: String,
    @JsonProperty("title")
    val title: String
)
