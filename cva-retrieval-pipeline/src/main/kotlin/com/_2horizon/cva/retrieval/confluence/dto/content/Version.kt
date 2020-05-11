package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Version(
    @JsonProperty("by")
    val user: User,
    @JsonProperty("hidden")
    val hidden: Boolean,
    @JsonProperty("_links")
    val links: Links,
    @JsonProperty("message")
    val message: String,
    @JsonProperty("minorEdit")
    val minorEdit: Boolean,
    @JsonProperty("number")
    val number: Int,
    @JsonProperty("when")
    val `when`: OffsetDateTime
)
