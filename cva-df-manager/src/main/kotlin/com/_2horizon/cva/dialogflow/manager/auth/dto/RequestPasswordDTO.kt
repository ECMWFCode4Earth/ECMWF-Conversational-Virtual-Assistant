package com._2horizon.cva.dialogflow.manager.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email

@Introspected
data class RequestPasswordDTO(
    @JsonProperty("email")
    @field:Email
    val email: String
)
