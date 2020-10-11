package com._2horizon.cva.dialogflow.manager.auth

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-13.
 */
@Singleton
class AuthenticationProviderEmailPassword(
    @Value("\${cva-admin.admins}") cvaAdminsConcatenated: List<String>
) : AuthenticationProvider {

    private val log = LoggerFactory.getLogger(javaClass)

    val cvaAdmins = cvaAdminsConcatenated.map { s ->
        val parts = s.split("|")
        CvaAdmin(name = parts[0], email = parts[1], password = parts[2], listOf("ROLE_ADMIN"))
    }

    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Mono<AuthenticationResponse> {

        return Mono.create { sink ->
            if (authenticationRequest != null &&
                cvaAdmins.map { it.email }.contains(authenticationRequest.identity) &&
                cvaAdmins.first { it.email == authenticationRequest.identity }.password == authenticationRequest.secret
            ) {
                log.info("Successful login for ${authenticationRequest.identity}")
                val userDetails = cvaAdmins.first { it.email == authenticationRequest.identity }.toEmailUserDetails()
                sink.success(userDetails)
            } else {
                log.warn("Wrong login request")
                sink.error(AuthenticationException(AuthenticationFailed()))
            }
        }
    }
}

data class CvaAdmin(val name: String, val email: String, val password: String, val roles: List<String>) {
    fun toEmailUserDetails(): EmailUserDetails {
        return EmailUserDetails(name = name, roles = roles, email = email)
    }
}
