package com._2horizon.cva.copernicus.dto.ui

import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedResource(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("type")
    val type: String
)
