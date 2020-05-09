package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class ContentResponse(
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("_links")
    val links: Links,
    @JsonProperty("results")
    val contents: List<Content>,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("start")
    val start: Int
)
