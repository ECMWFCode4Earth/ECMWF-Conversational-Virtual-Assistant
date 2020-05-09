package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class Links(
    @JsonProperty("self")
    val self: String
)
