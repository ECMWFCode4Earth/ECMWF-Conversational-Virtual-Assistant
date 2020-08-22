package com._2horizon.cva.copernicus.dto.ui


import com.fasterxml.jackson.annotation.JsonProperty

data class SpatialCoverage(

    @JsonProperty("geo")
    val geo: Geo,

    @JsonProperty("@type")
    val type: String
)
