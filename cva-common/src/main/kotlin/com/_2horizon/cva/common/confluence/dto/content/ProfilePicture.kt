package com._2horizon.cva.common.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class ProfilePicture(
    @JsonProperty("height")
    val height: Int,
    @JsonProperty("isDefault")
    val isDefault: Boolean,
    @JsonProperty("path")
    val path: String,
    @JsonProperty("width")
    val width: Int
)
