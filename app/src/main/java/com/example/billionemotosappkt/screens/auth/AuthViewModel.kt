package com.example.billionemotosappkt.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billionemotosappkt.data.auth.BillioneMotosSessionManager
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.AppRole
import com.example.billionemotosappkt.shared.api.CreateProfileRequest
import com.example.billionemotosappkt.shared.api.CreateUserRequest
import com.example.billionemotosappkt.shared.api.UserKind
import com.example.billionemotosappkt.shared.api.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the authentication form state and the persisted session lifecycle.
 */
class AuthViewModel(
	private val sessionManager: BillioneMotosSessionManager,
) : ViewModel() {
	private val _uiState = MutableStateFlow(AuthUiState())
	private val _formUser = MutableStateFlow(
		CreateUserRequest(
			email = "",
			cpf = "",
			password = "",
			nome = "",
			telefone = null,
			kind = UserKind.CLIENT,
			status = UserStatus.ACTIVE,
			clienteId = null,
			roles = listOf(AppRole.CLIENTE),
			profile = null,
		)
	)
	val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
	val formUser: StateFlow<CreateUserRequest> = _formUser.asStateFlow()
		
		init {
			bootstrap()
		}
	
	fun onFormChange(formUser: CreateUserRequest) {
		_formUser.update { formUser }
	}

	fun registerUser() {
		val request = _formUser.value
		if (_uiState.value.isSubmitting) return

		viewModelScope.launch {
			println(
				"[AuthViewModel] register submit email=${request.email.maskEmail()} cpf=${request.cpf.takeLast(4)}"
			)
			_uiState.update { it.copy(isSubmitting = true, errorMessage = null, registrationSuccess = false) }

			runCatching {
				sessionManager.register(request)
			}.onSuccess { response ->
				println(
					"[AuthViewModel] register success user=${response.email.maskEmail()} clienteId=${response.clienteId ?: "null"}"
				)
				_uiState.update {
					it.copy(
						isSubmitting = false,
						errorMessage = null,
						registrationSuccess = true,
					)
				}
			}.onFailure { error ->
				println("[AuthViewModel] register failed ${error::class.simpleName}: ${error.message}")
				_uiState.update {
					it.copy(
						isSubmitting = false,
						errorMessage = error.toUserMessage(),
						registrationSuccess = false,
					)
				}
			}
		}
	}

	fun clearRegistrationSuccess() {
		_uiState.update { it.copy(registrationSuccess = false) }
	}
	
	fun onEmailChange(value: String) {
		_uiState.update {
			it.copy(email = value, errorMessage = null)
		}
	}
	
	fun onPasswordChange(value: String) {
		_uiState.update {
			it.copy(password = value, errorMessage = null)
		}
	}
	
	fun login() {
		val state = _uiState.value
		if (state.isSubmitting) return
		
		viewModelScope.launch {
			println("[AuthViewModel] login submit email=${state.email.trim().maskEmail()}")
			_uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
			
			runCatching {
				sessionManager.login(
					email = state.email.trim(),
					password = state.password,
				)
			}.onSuccess { session ->
				println("[AuthViewModel] login success user=${session.user.email.maskEmail()} journey=${session.journey.screen} status=${session.journey.state} contractId=${session.journey.contractId ?: "null"}")
				applySession(session)
			}.onFailure { error ->
				println("[AuthViewModel] login failed ${error::class.simpleName}: ${error.message}")
				_uiState.update {
					it.copy(
						isSubmitting = false,
						errorMessage = error.toUserMessage(),
					)
				}
			}
		}
	}
	
	fun refreshSession() {
		if (_uiState.value.isSubmitting) return
		
		viewModelScope.launch {
			println("[AuthViewModel] refreshSession start")
			_uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
			
			runCatching { sessionManager.refresh() }
				.onSuccess { session ->
					println("[AuthViewModel] refreshSession success user=${session.user.email.maskEmail()} journey=${session.journey.screen} status=${session.journey.state}")
					applySession(session)
				}
				.onFailure { error ->
					println("[AuthViewModel] refreshSession failed ${error::class.simpleName}: ${error.message}")
					_uiState.update {
						it.copy(
							isSubmitting = false,
							errorMessage = error.toUserMessage(),
						)
					}
				}
		}
	}
	
	fun logout() {
		if (_uiState.value.isSubmitting) return
		
		viewModelScope.launch {
			println("[AuthViewModel] logout start")
			_uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
			
			runCatching { sessionManager.logout() }
				.onSuccess {
					println("[AuthViewModel] logout success")
					_uiState.value = AuthUiState(
						isLoading = false,
					)
				}
				.onFailure { error ->
					println("[AuthViewModel] logout failed ${error::class.simpleName}: ${error.message}")
					_uiState.update {
						it.copy(
							isSubmitting = false,
							errorMessage = error.toUserMessage(),
						)
					}
				}
		}
	}
	
	private fun bootstrap() {
		viewModelScope.launch {
			println("[AuthViewModel] bootstrap start")
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			runCatching {
				sessionManager.initialize()
				sessionManager.restoreAuthenticationContext()
			}.onSuccess { context ->
				if (context == null) {
					println("[AuthViewModel] bootstrap no context")
			_uiState.update {
				it.copy(
					isLoading = false,
					isSubmitting = false,
					isAuthenticated = false,
					registrationSuccess = false,
				)
			}
				} else {
					applySession(
						userName = context.user.nome,
						userEmail = context.user.email,
						avatarUrl = context.user.avatarUrl ?: context.user.profile?.avatarUrl,
						journeyState = context.journey.state,
						journeyScreen = context.journey.screen,
						journeyTitle = context.journey.title,
						journeyMessage = context.journey.message,
						clienteId = context.journey.clienteId ?: context.user.clienteId,
						clienteStatus = context.journey.clienteStatus,
						contractId = context.journey.contractId,
						contractStatus = context.journey.contractStatus,
						roles = context.journey.roles,
					)
				}
			}.onFailure { error ->
				println("[AuthViewModel] bootstrap failed ${error::class.simpleName}: ${error.message}")
				_uiState.update {
					it.copy(
						isLoading = false,
						isSubmitting = false,
						errorMessage = error.toUserMessage(),
					)
				}
			}
		}
	}
	
	private fun applySession(session: com.example.billionemotosappkt.shared.api.AuthSessionResponse) {
		applySession(
			userName = session.user.nome,
			userEmail = session.user.email,
			avatarUrl = session.user.avatarUrl ?: session.user.profile?.avatarUrl,
			journeyState = session.journey.state,
			journeyScreen = session.journey.screen,
			journeyTitle = session.journey.title,
			journeyMessage = session.journey.message,
			clienteId = session.journey.clienteId ?: session.user.clienteId,
			clienteStatus = session.journey.clienteStatus,
			contractId = session.journey.contractId,
			contractStatus = session.journey.contractStatus,
			roles = session.journey.roles.ifEmpty { session.user.roles.map { it.role } },
		)
	}

	private fun applySession(
		userName: String,
		userEmail: String,
		avatarUrl: String?,
		journeyState: String,
		journeyScreen: String,
		journeyTitle: String,
		journeyMessage: String,
		clienteId: String?,
		clienteStatus: com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus?,
		contractId: String?,
		contractStatus: com.example.billionemotosappkt.shared.api.ContratoStatus?,
		roles: List<AppRole>,
	) {
		println(
			"[AuthViewModel] applySession user=${userEmail.maskEmail()} journey=$journeyScreen status=$journeyState contractId=${contractId ?: "null"} roles=${roles.joinToString()}"
		)
		_uiState.update {
			it.copy(
				isLoading = false,
				isSubmitting = false,
				isAuthenticated = true,
				userName = userName,
				userEmail = userEmail,
				avatarUrl = avatarUrl,
				journeyState = journeyState,
				journeyScreen = journeyScreen,
				journeyTitle = journeyTitle,
				journeyMessage = journeyMessage,
				clienteId = clienteId,
				clienteStatus = clienteStatus,
				contractId = contractId,
				contractStatus = contractStatus,
				roles = roles,
				isAdmin = roles.any { it == AppRole.ADMIN },
				errorMessage = null,
				registrationSuccess = false,
			)
		}
	}
	
	private fun Throwable.toUserMessage(): String {
		return when (this) {
			is ApiException -> message ?: "Erro de autenticação."
			else -> message ?: "Erro inesperado."
		}
	}
	
	companion object {
		fun factory(sessionManager: BillioneMotosSessionManager): ViewModelProvider.Factory {
			return object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T {
					return AuthViewModel(sessionManager) as T
				}
			}
		}
	}
}

private fun String.maskEmail(): String {
	val parts = split("@", limit = 2)
	if (parts.size != 2) return "***"
	val local = parts[0]
	val domain = parts[1]
	val visible = local.take(2)
	return if (visible.isEmpty()) "***@$domain" else "$visible***@$domain"
}
