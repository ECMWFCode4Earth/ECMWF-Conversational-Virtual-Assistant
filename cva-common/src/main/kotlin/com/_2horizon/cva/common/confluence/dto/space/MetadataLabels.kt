package com._2horizon.cva.common.confluence.dto.space


import com.fasterxml.jackson.annotation.JsonProperty

data class MetadataLabels(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("prefix")
    val prefix: String
)
