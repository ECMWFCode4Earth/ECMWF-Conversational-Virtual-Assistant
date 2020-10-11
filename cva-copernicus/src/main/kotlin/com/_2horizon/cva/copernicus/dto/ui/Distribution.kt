package com._2horizon.cva.copernicus.dto.ui

import com.fasterxml.jackson.annotation.JsonProperty

data class Distribution(

    @JsonProperty("contentUrl")
    val contentUrl: String,

    @JsonProperty("encodingFormat")
    val encodingFormat: String?,

    @JsonProperty("@type")
    val type: String
)
