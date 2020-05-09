package com._2horizon.cva.retrieval.confluence.dto.space


import com.fasterxml.jackson.annotation.JsonProperty

data class Space(
    @JsonProperty("description")
    val description: Description,
    @JsonProperty("_expandable")
    val expandable: Expandable,
    @JsonProperty("icon")
    val icon: Icon,
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("key")
    val key: String,
    @JsonProperty("_links")
    val links: SpaceLink,
    @JsonProperty("metadata")
    val metadata: Metadata,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("type")
    val type: String
)
