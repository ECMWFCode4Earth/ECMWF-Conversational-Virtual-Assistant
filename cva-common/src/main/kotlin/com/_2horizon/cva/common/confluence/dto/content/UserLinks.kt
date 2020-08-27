package com._2horizon.cva.common.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class UserLinks(
    @JsonProperty("self")
    val self: String
)
