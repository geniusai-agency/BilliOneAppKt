package com.example.billionemotosappkt.shared.auth

import com.example.billionemotosappkt.shared.api.ApiMessageResponse
import com.example.billionemotosappkt.shared.api.AuthenticationContextResponse
import com.example.billionemotosappkt.shared.api.AuthSessionResponse
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.CreateLoginRequest
import com.example.billionemotosappkt.shared.api.CreateUserRequest
import com.example.billionemotosappkt.shared.api.UserResponse
import com.example.billionemotosappkt.shared.api.AppRole
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ContratoStatus

/**
 * Coordinates login, token persistence and token refresh for the app.
 */
class BillioneMotosSessionManager(
    private val api: BillioneMotosApi,
    private val tokenStore: TokenSessionStore,
) {
    suspend fun initialize() {
        tokenStore.hydrate()
    }

    suspend fun login(cpf: String, password: String): AuthSessionResponse {
        println(
            "[SessionManager] login start CPF=${cpf.maskEmail()} passwordLength=${password.length}"
        )
        val session = api.auth.login(
            CreateLoginRequest(
                cpf = cpf,
                password = password,
            ),
        )

        println(
            "[SessionManager] login success user=${session.user.email.maskEmail()} journey=${session.journey.screen} status=${session.journey.state} contractId=${session.journey.contractId ?: "null"}"
        )
        tokenStore.save(session)
        return session
    }

    suspend fun refresh(): AuthSessionResponse {
        val refreshToken = tokenStore.currentRefreshToken()
            ?: throw IllegalStateException("Nenhum refresh token salvo.")

        println("[SessionManager] refresh start hasRefreshToken=true")
        val session = api.auth.refresh(refreshToken)
        println(
            "[SessionManager] refresh success user=${session.user.email.maskEmail()} journey=${session.journey.screen} status=${session.journey.state}"
        )
        tokenStore.save(session)
        return session
    }

    suspend fun restoreAuthenticationContext(): AuthenticationContextResponse? {
        if (tokenStore.currentAccessToken().isNullOrBlank()) {
            println("[SessionManager] restore skipped no access token")
            return null
        }

        println("[SessionManager] restore start")
        return try {
            val context = api.auth.authentication()
            println(
                "[SessionManager] restore success user=${context.user.email.maskEmail()} journey=${context.journey.screen} status=${context.journey.state}"
            )
            context
        } catch (error: Throwable) {
            println(
                "[SessionManager] restore authentication failed: ${error::class.simpleName}: ${error.message}"
            )
            val refreshToken = tokenStore.currentRefreshToken() ?: return null

            try {
                println("[SessionManager] restore retry via refresh")
                val refreshed = api.auth.refresh(refreshToken)
                tokenStore.save(refreshed)
                val context = api.auth.authentication()
                println(
                    "[SessionManager] restore retry success user=${context.user.email.maskEmail()} journey=${context.journey.screen} status=${context.journey.state}"
                )
                context
            } catch (retryError: Throwable) {
                println(
                    "[SessionManager] restore retry failed: ${retryError::class.simpleName}: ${retryError.message}"
                )
                tokenStore.clear()
                null
            }
        }
    }

    suspend fun logout(): ApiMessageResponse {
        println("[SessionManager] logout start")
        return try {
            val response = api.auth.logout()
            println("[SessionManager] logout success message=${response.message}")
            response
        } finally {
            tokenStore.clear()
        }
    }

    suspend fun logoutAll(): ApiMessageResponse {
        println("[SessionManager] logoutAll start")
        return try {
            val response = api.auth.logoutAll()
            println("[SessionManager] logoutAll success message=${response.message}")
            response
        } finally {
            tokenStore.clear()
        }
    }

    /**
     * Saves a session received from social login or an external auth callback.
     */
    suspend fun save(session: AuthSessionResponse) {
        tokenStore.save(session)
    }

    suspend fun register(request: CreateUserRequest): UserResponse {
        println(
            "[SessionManager] register start email=${request.email.maskEmail()} cpf=${request.cpf.takeLast(4)} roles=${request.roles?.joinToString() ?: "null"}"
        )
        val response = api.users.register(request)
        println(
            "[SessionManager] register success user=${response.email.maskEmail()} clienteId=${response.clienteId ?: "null"}"
        )
        return response
    }

    fun currentAccessToken(): String? = tokenStore.currentAccessToken()

    fun currentRefreshToken(): String? = tokenStore.currentRefreshToken()
}

private fun String.maskEmail(): String {
    val parts = split("@", limit = 2)
    if (parts.size != 2) return "***"
    val local = parts[0]
    val domain = parts[1]
    val visible = local.take(2)
    return if (visible.isEmpty()) "***@$domain" else "$visible***@$domain"
}
