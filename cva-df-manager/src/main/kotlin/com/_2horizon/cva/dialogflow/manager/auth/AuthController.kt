package com._2horizon.cva.dialogflow.manager.auth

import com._2horizon.cva.dialogflow.manager.auth.dto.LoginDTO
import com._2horizon.cva.dialogflow.manager.auth.dto.RefreshTokenDTO
import com._2horizon.cva.dialogflow.manager.auth.dto.RequestPasswordDTO
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authenticator
import io.micronaut.security.endpoints.LoginController
import io.micronaut.security.handlers.LoginHandler
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.token.jwt.render.AccessRefreshToken
import io.micronaut.validation.Validated
import reactor.core.publisher.Mono
import javax.validation.Valid

/**
 * Created by Frank Lieber (liefra) on 2020-09-13.
 */

@Requirements(
    Requires(beans = [LoginHandler::class]),
    Requires(beans = [Authenticator::class])
)
@Controller("/api/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
@Validated
@Replaces(bean = LoginController::class)
class AuthController(
    private val authService: AuthService,

    ) {

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON)
    @Post("/login")
    fun login(
        @Body @Valid loginDTO: LoginDTO,
        request: HttpRequest<*>
    ): Mono<MutableHttpResponse<*>> {
        return authService.authenticate(request, loginDTO)
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON)
    @Post("/refresh-token")
    fun refreshToken(
        @Body @Valid refreshTokenDTO: RefreshTokenDTO,
        request: HttpRequest<*>
    ): Mono<MutableHttpResponse<AccessRefreshToken>> {
        return authService.refreshToken(refreshTokenDTO)
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON)
    @Post("/request-password-reset")
    fun requestPasswordReset(
        @Body @Valid requestPasswordDTO: RequestPasswordDTO,
        request: HttpRequest<*>
    ) {
        //TODO: Currently no password reset implemented
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON)
    @Post("/logout")
    fun logout(
        request: HttpRequest<*>,
        @Header("Authorization") authorization: String?
    ) {
        authService.logout(authorization)
    }
}
