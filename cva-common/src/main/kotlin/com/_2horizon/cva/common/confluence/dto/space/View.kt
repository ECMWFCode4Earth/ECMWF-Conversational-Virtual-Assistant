package com._2horizon.cva.common.confluence.dto.space

import com.fasterxml.jackson.annotation.JsonProperty

data class View(
    @JsonProperty("representation")
    val representation: String,
    @JsonProperty("value")
    val value: String
)
