package com._2horizon.cva.retrieval.copernicus.dto.ui


import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalLink(
    
    @JsonProperty("description")
    val description: String,

    @JsonProperty("groupby")
    val groupby: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("url")
    val url: String
)
