package com._2horizon.cva.common.confluence.dto.space

import com.fasterxml.jackson.annotation.JsonProperty

data class Icon(
    @JsonProperty("height")
    val height: Int,
    @JsonProperty("isDefault")
    val isDefault: Boolean,
    @JsonProperty("path")
    val path: String,
    @JsonProperty("width")
    val width: Int
)
