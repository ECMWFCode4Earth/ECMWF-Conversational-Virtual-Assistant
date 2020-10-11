package com._2horizon.cva.dialogflow.manager.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class SignUpDTO(
    @JsonProperty("confirmPassword")
    val confirmPassword: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("fullName")
    val fullName: String,
    @JsonProperty("password")
    val password: String
)
