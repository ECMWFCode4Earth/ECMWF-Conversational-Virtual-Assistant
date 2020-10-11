package com._2horizon.cva.common.confluence.dto.space

import com.fasterxml.jackson.annotation.JsonProperty

data class Labels(
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("results")
    val results: List<MetadataLabels>,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("start")
    val start: Int
)
