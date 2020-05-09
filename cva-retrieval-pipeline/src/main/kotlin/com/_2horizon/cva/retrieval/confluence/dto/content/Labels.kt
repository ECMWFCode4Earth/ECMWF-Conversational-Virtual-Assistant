package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class Labels(
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("_links")
    val links: Links,
    @JsonProperty("results")
    val results: List<MetadataLabels>,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("start")
    val start: Int
)
