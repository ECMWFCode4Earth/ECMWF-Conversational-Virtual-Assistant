package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class History(
    @JsonProperty("createdBy")
    val createdBy: CreatedBy,
    @JsonProperty("createdDate")
    val createdDate: OffsetDateTime,
    @JsonProperty("latest")
    val latest: Boolean,
    @JsonProperty("_links")
    val links: Links
)
