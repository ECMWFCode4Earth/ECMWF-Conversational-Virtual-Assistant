package com._2horizon.cva.common.confluence.dto.space


import com.fasterxml.jackson.annotation.JsonProperty

data class Links(
    @JsonProperty("base")
    val base: String,
    @JsonProperty("context")
    val context: String,
    @JsonProperty("self")
    val self: String
)
