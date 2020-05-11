package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class Extensions(
    @JsonProperty("position")
    val position: String
)
