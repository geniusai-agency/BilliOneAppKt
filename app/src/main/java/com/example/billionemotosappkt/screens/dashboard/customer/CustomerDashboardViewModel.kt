package com.example.billionemotosappkt.screens.dashboard.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.SolicitarAnaliseRequest
import com.example.billionemotosappkt.shared.api.UpdateProfileRequest
import com.example.billionemotosappkt.shared.api.UpdateUserRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.api.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerDashboardUiState(
	val isLoading: Boolean = true,
	val isRefreshing: Boolean = false,
	val isSavingProfile: Boolean = false,
	val isUploadingAvatar: Boolean = false,
	val isSubmittingAnalysis: Boolean = false,
	val errorMessage: String? = null,
	val dashboardData: CustomerDashboardData? = null,
	val user: UserResponse? = null,
	val contratosCount: Int = 0,
	val availablePlans: List<com.example.billionemotosappkt.shared.api.PlanoResponse> = emptyList(),
	val availableModels: List<com.example.billionemotosappkt.shared.api.MotoModeloResponse> = emptyList(),
)

class CustomerDashboardViewModel(
	private val api: BillioneMotosApi,
	private val authState: AuthUiState,
) : ViewModel() {
	private val repository = CustomerDashboardRepository(api)
	private val _uiState = MutableStateFlow(CustomerDashboardUiState())
	val uiState: StateFlow<CustomerDashboardUiState> = _uiState.asStateFlow()

	init {
		refresh()
	}

	fun refresh() {
		if (_uiState.value.isRefreshing) return

		viewModelScope.launch {
			val initialLoad = _uiState.value.dashboardData == null
			_uiState.update {
				it.copy(
					isLoading = initialLoad,
					isRefreshing = true,
					errorMessage = null,
				)
			}

			runCatching { repository.load(authState) }
				.onSuccess { snapshot ->
					_uiState.update {
						it.copy(
							isLoading = false,
							isRefreshing = false,
							errorMessage = null,
							dashboardData = snapshot.toCustomerDashboardData(authState),
							user = snapshot.user,
							contratosCount = snapshot.contratos.size,
							availablePlans = snapshot.planos,
							availableModels = snapshot.modelos,
						)
					}
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isLoading = false,
							isRefreshing = false,
							errorMessage = error.toUserMessage(),
						)
					}
				}
		}
	}

	fun saveProfile(
		nome: String,
		telefone: String?,
		bio: String?,
		timezone: String?,
		language: String?,
	) {
		if (_uiState.value.isSavingProfile) return

		viewModelScope.launch {
			_uiState.update { it.copy(isSavingProfile = true, errorMessage = null) }

			runCatching {
				api.users.updateMe(
					UpdateUserRequest(
						nome = nome,
						telefone = telefone,
						profile = UpdateProfileRequest(
							bio = bio,
							timezone = timezone,
							language = language,
						),
					),
				)
			}.onSuccess {
				_uiState.update { it.copy(isSavingProfile = false) }
				refresh()
			}.onFailure { error ->
				_uiState.update {
					it.copy(
						isSavingProfile = false,
						errorMessage = error.toUserMessage(),
					)
				}
			}
		}
	}

	fun uploadAvatar(file: UploadFileRequest, descricao: String? = null) {
		if (_uiState.value.isUploadingAvatar) return

		viewModelScope.launch {
			_uiState.update { it.copy(isUploadingAvatar = true, errorMessage = null) }

			runCatching {
				api.users.uploadMeImage(file = file, descricao = descricao)
			}.onSuccess {
				_uiState.update { it.copy(isUploadingAvatar = false) }
				refresh()
			}.onFailure { error ->
				_uiState.update {
					it.copy(
						isUploadingAvatar = false,
						errorMessage = error.toUserMessage(),
					)
				}
			}
		}
	}

	fun requestAnalysis(
		request: SolicitarAnaliseRequest,
		cnhImage: UploadFileRequest? = null,
		identidadeImage: UploadFileRequest? = null,
		comprovanteResidenciaImage: UploadFileRequest? = null,
	) {
		if (_uiState.value.isSubmittingAnalysis) return

		viewModelScope.launch {
			_uiState.update { it.copy(isSubmittingAnalysis = true, errorMessage = null) }

			runCatching {
				api.users.solicitarAnalise(
					request = request,
					cnhImage = cnhImage,
					identidadeImage = identidadeImage,
					comprovanteResidenciaImage = comprovanteResidenciaImage,
				)
			}.onSuccess {
				_uiState.update { it.copy(isSubmittingAnalysis = false) }
				refresh()
			}.onFailure { error ->
				_uiState.update {
					it.copy(
						isSubmittingAnalysis = false,
						errorMessage = error.toUserMessage(),
					)
				}
			}
		}
	}

	private fun Throwable.toUserMessage(): String {
		return when (this) {
			is ApiException -> message ?: "Falha ao comunicar com a API."
			else -> message ?: "Falha inesperada ao comunicar com a API."
		}
	}

	companion object {
		fun factory(
			api: BillioneMotosApi,
			authState: AuthUiState,
		): ViewModelProvider.Factory {
			return object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T {
					return CustomerDashboardViewModel(api, authState) as T
				}
			}
		}
	}
}
