package com._2horizon.cva.copernicus.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class WmsSample(
    @JsonProperty("layer_prefix")
    val layerPrefix: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("url")
    val url: String
)
