package com._2horizon.cva.common.confluence.dto.content

import com.fasterxml.jackson.annotation.JsonProperty

data class Storage(

    @JsonProperty("representation")
    val representation: String,
    @JsonProperty("value")
    val value: String
)
