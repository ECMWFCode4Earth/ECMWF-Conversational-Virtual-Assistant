package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class MetadataLabels(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("prefix")
    val prefix: String
)
