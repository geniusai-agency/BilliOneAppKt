package com.example.billionemotosappkt.desktop.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class AppRole { CLIENTE, ADMIN, FINANCEIRO, MECANICO, OFICINA }

@Serializable
enum class UserKind { INTERNAL, CLIENT }

@Serializable
enum class UserStatus { ACTIVE, INVITED, PENDING_PASSWORD, SUSPENDED, ARCHIVED }

@Serializable
data class ApiMessageResponse(
	val message: String,
)

@Serializable
data class ApiErrorResponse(
	val statusCode: Int? = null,
	val message: JsonElement? = null,
	val error: String? = null,
)

@Serializable
data class CreateProfileRequest(
	val avatarUrl: String? = null,
	val bio: String? = null,
	val timezone: String? = null,
	val language: String? = null,
)

@Serializable
data class CreateUserRequest(
	val email: String,
	val cpf: String,
	val password: String,
	val nome: String,
	val telefone: String? = null,
	val kind: UserKind? = null,
	val status: UserStatus? = null,
	val clienteId: String? = null,
	val roles: List<AppRole>? = null,
	val profile: CreateProfileRequest? = null,
)

@Serializable
data class CreateLoginRequest(
	val email: String,
	val password: String,
)

@Serializable
data class RefreshTokenRequest(
	val refreshToken: String,
)

@Serializable
data class AuthJourneyResponse(
	val state: String,
	val screen: String,
	val title: String,
	val message: String,
	val nextAction: String,
)

@Serializable
data class UserResponse(
	val id: String,
	val email: String,
	val nome: String,
	val telefone: String? = null,
	val kind: UserKind,
	val status: UserStatus,
	val clienteId: String? = null,
)

@Serializable
data class AuthSessionResponse(
	@SerialName("access_token") val accessToken: String,
	@SerialName("refresh_token") val refreshToken: String,
	val user: UserResponse,
	val journey: AuthJourneyResponse,
)

@Serializable
data class AuthenticationContextResponse(
	val user: UserResponse,
	val journey: AuthJourneyResponse,
)

fun AuthSessionResponse.toContext(): AuthenticationContextResponse {
	return AuthenticationContextResponse(
		user = user,
		journey = journey,
	)
}
