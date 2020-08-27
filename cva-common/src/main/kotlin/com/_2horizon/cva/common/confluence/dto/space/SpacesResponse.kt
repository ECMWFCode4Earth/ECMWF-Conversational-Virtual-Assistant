package com._2horizon.cva.common.confluence.dto.space


import com.fasterxml.jackson.annotation.JsonProperty

data class SpacesResponse(
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("_links")
    val links: Links,
    @JsonProperty("results")
    val spaces: List<Space>,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("start")
    val start: Int
)
