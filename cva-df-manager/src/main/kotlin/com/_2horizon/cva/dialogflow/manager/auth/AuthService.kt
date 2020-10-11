package com._2horizon.cva.dialogflow.manager.auth

import com._2horizon.cva.dialogflow.manager.auth.dto.LoginDTO
import com._2horizon.cva.dialogflow.manager.auth.dto.RefreshTokenDTO
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.security.authentication.UserDetails
import io.micronaut.security.event.LoginFailedEvent
import io.micronaut.security.event.LoginSuccessfulEvent
import io.micronaut.security.event.LogoutEvent
import io.micronaut.security.handlers.LoginHandler
import io.micronaut.security.token.jwt.bearer.AccessRefreshTokenLoginHandler
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator
import io.micronaut.security.token.jwt.render.AccessRefreshToken
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-14.
 */
@Singleton
class AuthService(
    private val authenticator: AuthenticationProviderEmailPassword,
    private val loginHandler: LoginHandler,
    private val eventPublisher: ApplicationEventPublisher,
    private val accessRefreshTokenGenerator: AccessRefreshTokenGenerator,
    private val accessRefreshTokenLoginHandler: AccessRefreshTokenLoginHandler,
    @Value("\${cva-admin.auth.refresh-token.expiration}") private val refreshTokenExpiration: Long,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val tokens = ConcurrentHashMap<String, UserDetails>()

    fun authenticate(request: HttpRequest<*>, loginDTO: LoginDTO): Mono<MutableHttpResponse<*>> {
        return authenticator.authenticate(request, loginDTO)
            .map { authenticationResponse ->
                if (authenticationResponse.isAuthenticated && authenticationResponse.userDetails.isPresent) {
                    val userDetails = authenticationResponse.userDetails.get()
                    eventPublisher.publishEvent(LoginSuccessfulEvent(userDetails))
                    val response: MutableHttpResponse<AccessRefreshToken> =
                        accessRefreshTokenLoginHandler.loginSuccess(
                            userDetails,
                            request
                        ) as MutableHttpResponse<AccessRefreshToken>
                    val accessRefreshToken: AccessRefreshToken = response.body.get()
                    tokens[accessRefreshToken.accessToken] = userDetails
                    response
                } else {
                    eventPublisher.publishEvent(LoginFailedEvent(authenticationResponse))
                    loginHandler.loginFailed(authenticationResponse, request)
                }
            }
    }

    fun logout(authorization: String?) {
        // TODO: Must be improved once tokens are stored
        if (authorization != null) {
            eventPublisher.publishEvent(LogoutEvent(authorization))
        }
    }

    fun refreshToken(refreshTokenDTO: RefreshTokenDTO): Mono<MutableHttpResponse<AccessRefreshToken>> {
        val userDetails = tokens.remove(refreshTokenDTO.token)

        // TODO: This token refresh handling is a big hack and needs to be replaced with a proper implementation
        return if (userDetails != null && getDurationSinceLogin(refreshTokenDTO.createdAt).toMinutes() < refreshTokenExpiration) {
            log.info("Creating refresh token for ${refreshTokenDTO.payload.sub}")
            val refreshToken: AccessRefreshToken = accessRefreshTokenGenerator.generate(userDetails).get()
            tokens[refreshToken.accessToken] = userDetails
            Mono.just(HttpResponse.ok(refreshToken))
        } else {
            log.info("Refresh token cannot be created for ${refreshTokenDTO.payload.sub}")
            Mono.just(HttpResponse.status(HttpStatus.FORBIDDEN))
        }
    }

    private fun getDurationSinceLogin(dt: OffsetDateTime): Duration = Duration.between(dt, OffsetDateTime.now())
}
