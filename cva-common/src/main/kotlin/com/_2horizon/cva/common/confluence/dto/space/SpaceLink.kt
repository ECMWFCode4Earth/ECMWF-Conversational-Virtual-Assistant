package com._2horizon.cva.common.confluence.dto.space

import com.fasterxml.jackson.annotation.JsonProperty

data class SpaceLink(
    @JsonProperty("self")
    val self: String,
    @JsonProperty("webui")
    val webui: String
)
