package com._2horizon.cva.common.confluence.dto.pagechildren

import com.fasterxml.jackson.annotation.JsonProperty

data class Result(
    @JsonProperty("children")
    val children: Children?,

    @JsonProperty("id")
    val id: Long,

    @JsonProperty("status")
    val status: String,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("type")
    val type: String
)
