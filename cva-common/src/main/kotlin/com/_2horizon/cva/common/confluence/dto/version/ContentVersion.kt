package com._2horizon.cva.common.confluence.dto.version


import com._2horizon.cva.common.confluence.dto.content.User
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class ContentVersion(
    @JsonProperty("by")
    val user: User,
    
    @JsonProperty("hidden")
    val hidden: Boolean,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("minorEdit")
    val minorEdit: Boolean,

    @JsonProperty("number")
    val number: Int,

    @JsonProperty("when")
    val datetime: OffsetDateTime
)
