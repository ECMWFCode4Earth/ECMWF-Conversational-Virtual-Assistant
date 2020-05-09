package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class History(
    @JsonProperty("createdBy")
    val createdBy: CreatedBy,
    @JsonProperty("createdDate")
    val createdDate: String,
    @JsonProperty("latest")
    val latest: Boolean,
    @JsonProperty("_links")
    val links: Links
)
