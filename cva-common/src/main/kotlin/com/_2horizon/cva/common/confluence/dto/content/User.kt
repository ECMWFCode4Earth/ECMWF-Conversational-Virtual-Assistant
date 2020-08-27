package com._2horizon.cva.common.confluence.dto.content


import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("displayName")
    val displayName: String,
    @JsonProperty("_links")
    val links: Links,
    @JsonProperty("profilePicture")
    val profilePicture: ProfilePicture,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("userKey")
    val userKey: String,
    @JsonProperty("username")
    val username: String
)
