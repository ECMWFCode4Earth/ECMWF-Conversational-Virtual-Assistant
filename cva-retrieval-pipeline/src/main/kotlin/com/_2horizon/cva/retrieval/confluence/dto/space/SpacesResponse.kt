package com._2horizon.cva.retrieval.confluence.dto.space


import com._2horizon.cva.retrieval.confluence.dto.space.Links
import com._2horizon.cva.retrieval.confluence.dto.space.Space
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
