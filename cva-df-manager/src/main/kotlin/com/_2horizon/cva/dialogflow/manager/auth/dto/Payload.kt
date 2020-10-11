package com._2horizon.cva.dialogflow.manager.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Payload(
    @JsonProperty("email")
    val email: String,
    @JsonProperty("exp")
    val exp: Long,
    @JsonProperty("iat")
    val iat: Long,
    @JsonProperty("iss")
    val iss: String,
    @JsonProperty("nbf")
    val nbf: Long,
    @JsonProperty("roles")
    val roles: List<String>,
    @JsonProperty("sub")
    val sub: String
)
