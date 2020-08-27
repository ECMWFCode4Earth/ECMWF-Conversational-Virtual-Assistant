package com._2horizon.cva.common.confluence.dto.pagechildren


import com.fasterxml.jackson.annotation.JsonProperty

data class Page(
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("results")
    val results: List<Result>,
    @JsonProperty("size")
    val size: Int,
    @JsonProperty("start")
    val start: Int
)
