package com._2horizon.cva.retrieval.confluence.dto.pagechildren


import com.fasterxml.jackson.annotation.JsonProperty

data class Children(

    @JsonProperty("page")
    val page: Page
)
