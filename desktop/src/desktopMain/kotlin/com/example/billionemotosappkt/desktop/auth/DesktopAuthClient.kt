package com.example.billionemotosappkt.desktop.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import java.util.prefs.Preferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

private const val PREF_ACCESS = "access_token"
private const val PREF_REFRESH = "refresh_token"

class DesktopAuthClient(
	private val baseUrl: String,
) : AutoCloseable {
	private val preferences = Preferences.userRoot().node("com.example.billionemotosappkt.desktop.auth")
	private val json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}

	private val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(json)
		}
		defaultRequest {
			url {
				takeFrom(baseUrl)
			}
			header("ngrok-skip-browser-warning", "true")
		}
	}

	suspend fun login(email: String, password: String): AuthSessionResponse {
		val session: AuthSessionResponse = request(
			method = RequestMethod.POST,
			path = "/auth/login",
			body = CreateLoginRequest(email = email, password = password),
			authorized = false,
		)
		saveTokens(session.accessToken, session.refreshToken)
		return session
	}

	suspend fun register(payload: CreateUserRequest): UserResponse {
		return request(
			method = RequestMethod.POST,
			path = "/users/register",
			body = payload,
			authorized = false,
		)
	}

	suspend fun restoreAuthenticationContext(): AuthenticationContextResponse? {
		val accessToken = currentAccessToken() ?: return null
		return try {
			request<AuthenticationContextResponse>(
				method = RequestMethod.GET,
				path = "/auth/authentication",
				authorized = true,
				overrideToken = accessToken,
			)
		} catch (_: Throwable) {
			val refreshToken = currentRefreshToken() ?: return null
			runCatching {
				val session: AuthSessionResponse = request(
					method = RequestMethod.POST,
					path = "/auth/refresh",
					body = RefreshTokenRequest(refreshToken = refreshToken),
					authorized = false,
				)
				saveTokens(session.accessToken, session.refreshToken)
				request<AuthenticationContextResponse>(
					method = RequestMethod.GET,
					path = "/auth/authentication",
					authorized = true,
					overrideToken = session.accessToken,
				)
			}.getOrNull() ?: run {
				clear()
				null
			}
		}
	}

	suspend fun logout(): ApiMessageResponse {
		return try {
			request(
				method = RequestMethod.DELETE,
				path = "/auth/logout",
				authorized = true,
			)
		} finally {
			clear()
		}
	}

	fun currentAccessToken(): String? = preferences.get(PREF_ACCESS, null)

	fun currentRefreshToken(): String? = preferences.get(PREF_REFRESH, null)

	fun saveTokens(accessToken: String, refreshToken: String) {
		preferences.put(PREF_ACCESS, accessToken)
		preferences.put(PREF_REFRESH, refreshToken)
	}

	private fun clear() {
		preferences.remove(PREF_ACCESS)
		preferences.remove(PREF_REFRESH)
	}

	override fun close() {
		client.close()
	}

	private suspend inline fun <reified T> request(
		method: RequestMethod,
		path: String,
		body: Any? = null,
		authorized: Boolean,
		overrideToken: String? = null,
	): T {
		val response = try {
			when (method) {
				RequestMethod.GET -> client.get(path) {
					if (authorized) {
						val token = overrideToken ?: currentAccessToken()
						if (!token.isNullOrBlank()) {
							header(HttpHeaders.Authorization, "Bearer $token")
						}
					}
				}
				RequestMethod.POST -> client.post(path) {
					if (authorized) {
						val token = overrideToken ?: currentAccessToken()
						if (!token.isNullOrBlank()) {
							header(HttpHeaders.Authorization, "Bearer $token")
						}
					}
					contentType(ContentType.Application.Json)
					if (body != null) setBody(body)
				}
				RequestMethod.DELETE -> client.delete(path) {
					if (authorized) {
						val token = overrideToken ?: currentAccessToken()
						if (!token.isNullOrBlank()) {
							header(HttpHeaders.Authorization, "Bearer $token")
						}
					}
				}
			}
		} catch (error: ClientRequestException) {
			throw mapApiError(error.response.status.value, error.response.bodyAsString())
		}

		if (!response.status.isSuccess()) {
			throw mapApiError(response.status.value, response.bodyAsString())
		}

		if (T::class == Unit::class) {
			@Suppress("UNCHECKED_CAST")
			return Unit as T
		}

		return response.body()
	}

	private suspend fun mapApiError(statusCode: Int, rawBody: String?): RuntimeException {
		val parsed = rawBody?.let {
			runCatching { json.decodeFromString<ApiErrorResponse>(it) }.getOrNull()
		}
		val message = when (val payload = parsed?.message) {
			null -> rawBody?.takeIf { it.isNotBlank() } ?: "HTTP $statusCode"
			is JsonPrimitive -> payload.content
			else -> payload.toString()
		}
		return RuntimeException(message)
	}

	private enum class RequestMethod { GET, POST, DELETE }
}

private suspend fun io.ktor.client.statement.HttpResponse.bodyAsString(): String? {
	return runCatching { body<String>() }.getOrNull()
}
