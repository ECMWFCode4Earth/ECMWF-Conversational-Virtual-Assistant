package com._2horizon.cva.dialogflow.manager.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.time.OffsetDateTime

@Introspected
data class RefreshTokenDTO(
    @JsonProperty("createdAt")
    val createdAt: OffsetDateTime,
    @JsonProperty("ownerStrategyName")
    val ownerStrategyName: String,
    @JsonProperty("payload")
    val payload: Payload,
    @JsonProperty("token")
    val token: String
)
