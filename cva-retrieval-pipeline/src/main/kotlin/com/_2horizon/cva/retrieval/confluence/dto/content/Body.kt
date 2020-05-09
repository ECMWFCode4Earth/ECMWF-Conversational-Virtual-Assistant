package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class Body(

    @JsonProperty("storage")
    val storage: Storage,
    @JsonProperty("view")
    val view: View
)
