package com._2horizon.cva.retrieval.confluence.dto.content


import com._2horizon.cva.retrieval.confluence.dto.space.Labels
import com.fasterxml.jackson.annotation.JsonProperty

data class Metadata(
    @JsonProperty("labels")
    val labels: Labels
)
