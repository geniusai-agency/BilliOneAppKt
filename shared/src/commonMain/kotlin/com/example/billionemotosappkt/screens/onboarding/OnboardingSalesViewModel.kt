package com.example.billionemotosappkt.screens.onboarding

import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.SolicitarAnaliseRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import com.example.billionemotosappkt.shared.utils.ViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingSalesUiState(
	val isLoading: Boolean = true,
	val errorMessage: String? = null,
	val planos: List<PlanoResponse> = emptyList(),
	val modelos: List<MotoModeloResponse> = emptyList(),
	val isSubmitting: Boolean = false,
	val submitSuccess: Boolean = false,
	val submitError: String? = null,
)

/**
 * Lean view model for the pre-contract sales/onboarding experience.
 * Loads the storefront (planos + modelos) and submits the analysis request
 * with the document photos (CNH, Identidade, Comprovante de Residência).
 */
class OnboardingSalesViewModel(
	private val api: BillioneMotosApi,
) : ViewModel() {
	private val _uiState = MutableStateFlow(OnboardingSalesUiState())
	val uiState: StateFlow<OnboardingSalesUiState> = _uiState.asStateFlow()

	init {
		refresh()
	}

	fun refresh() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			runCatching {
				coroutineScope {
					val planosDeferred = async { api.planos.list() }
					val modelosDeferred = async { api.motos.modelos() }
					planosDeferred.await() to modelosDeferred.await()
				}
			}.onSuccess { (planos, modelos) ->
				_uiState.update {
					it.copy(
						isLoading = false,
						errorMessage = null,
						planos = planos,
						modelos = modelos.filter { modelo -> !modelo.id.isNullOrBlank() },
					)
				}
			}.onFailure { error ->
				_uiState.update {
					it.copy(isLoading = false, errorMessage = error.toUserMessage())
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
		if (_uiState.value.isSubmitting) return

		viewModelScope.launch {
			_uiState.update { it.copy(isSubmitting = true, submitError = null, submitSuccess = false) }
			runCatching {
				api.users.solicitarAnalise(
					request = request,
					cnhImage = cnhImage,
					identidadeImage = identidadeImage,
					comprovanteResidenciaImage = comprovanteResidenciaImage,
				)
			}.onSuccess {
				_uiState.update { it.copy(isSubmitting = false, submitSuccess = true, submitError = null) }
			}.onFailure { error ->
				_uiState.update {
					it.copy(isSubmitting = false, submitError = error.toUserMessage())
				}
			}
		}
	}

	fun consumeSubmitSuccess() {
		_uiState.update { it.copy(submitSuccess = false) }
	}

	private fun Throwable.toUserMessage(): String {
		return when (this) {
			is ApiException -> message ?: "Falha ao comunicar com a API."
			else -> message ?: "Falha inesperada ao comunicar com a API."
		}
	}

	companion object {}
}
