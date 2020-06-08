package com._2horizon.cva.retrieval.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class Content(
    @JsonProperty("body")
    val body: Body,
    @JsonProperty("extensions")
    val extensions: Extensions,
    @JsonProperty("history")
    val history: History,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("metadata")
    val metadata: Metadata,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("version")
    val version: Version
)
