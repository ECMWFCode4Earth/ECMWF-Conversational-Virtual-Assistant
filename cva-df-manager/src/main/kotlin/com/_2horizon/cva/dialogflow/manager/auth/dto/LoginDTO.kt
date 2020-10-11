package com._2horizon.cva.dialogflow.manager.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.micronaut.security.authentication.AuthenticationRequest
import java.io.Serializable
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Introspected
data class LoginDTO(
    @JsonProperty("email")
    @field:Email
    val email: String,

    @JsonProperty("password")
    @field:Size(min = 6, max = 100)
    val password: String
) : Serializable, AuthenticationRequest<String, String> {

    override fun getIdentity(): String {
        return email
    }

    override fun getSecret(): String {
        return password
    }
}
