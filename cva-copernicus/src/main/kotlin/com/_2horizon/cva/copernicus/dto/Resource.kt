package com._2horizon.cva.copernicus.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Resource(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("rich_abstract")
    val richAbstract: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("terms")
    val terms: List<String>,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("wms_sample")
    val wmsSample: WmsSample?
)
