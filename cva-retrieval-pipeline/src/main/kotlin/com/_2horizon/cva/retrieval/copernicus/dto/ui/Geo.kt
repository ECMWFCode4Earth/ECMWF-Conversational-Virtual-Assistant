package com._2horizon.cva.retrieval.copernicus.dto.ui


import com.fasterxml.jackson.annotation.JsonProperty

data class Geo(

    @JsonProperty("box")
    val box: String,

    @JsonProperty("@type")
    val type: String
)
