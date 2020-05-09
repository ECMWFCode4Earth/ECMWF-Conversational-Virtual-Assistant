package com._2horizon.cva.retrieval.confluence.dto.space


import com.fasterxml.jackson.annotation.JsonProperty

data class Plain(
    @JsonProperty("representation")
    val representation: String,
    @JsonProperty("value")
    val value: String
)
