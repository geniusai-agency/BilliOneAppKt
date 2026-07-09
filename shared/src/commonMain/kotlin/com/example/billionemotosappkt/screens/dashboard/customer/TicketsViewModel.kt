package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.shared.utils.ViewModel
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.CreateTicketCommentRequest
import com.example.billionemotosappkt.shared.api.CreateTicketRequest
import com.example.billionemotosappkt.shared.api.TicketCategoria
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketPrioridade
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.api.UpdateTicketRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketsUiState(
	val isLoadingList: Boolean = true,
	val listError: String? = null,
	val tickets: List<TicketResponse> = emptyList(),
	val isLoadingDetail: Boolean = false,
	val detailError: String? = null,
	val selectedTicket: TicketResponse? = null,
	val isCreating: Boolean = false,
	val createError: String? = null,
	val isSaving: Boolean = false,
	val isAddingComment: Boolean = false,
	val isUploadingPhoto: Boolean = false,
	val actionError: String? = null,
)

class TicketsViewModel(
	private val api: BillioneMotosApi,
) : ViewModel() {
	private val repository = TicketsRepository(api)
	private val _uiState = MutableStateFlow(TicketsUiState())
	val uiState: StateFlow<TicketsUiState> = _uiState.asStateFlow()

	/** Exposes the base URL so the UI can build `/arquivos/{id}/render` links. */
	val baseUrl: String = api.config.baseUrl.trimEnd('/')

	init {
		loadList()
	}

	fun loadList() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoadingList = true, listError = null) }
			runCatching { repository.list() }
				.onSuccess { tickets ->
					_uiState.update {
						it.copy(isLoadingList = false, listError = null, tickets = tickets)
					}
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isLoadingList = false, listError = error.toUserMessage())
					}
				}
		}
	}

	fun openDetail(id: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoadingDetail = true, detailError = null, actionError = null) }
			runCatching { repository.get(id) }
				.onSuccess { ticket ->
					_uiState.update {
						it.copy(isLoadingDetail = false, detailError = null, selectedTicket = ticket)
					}
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isLoadingDetail = false, detailError = error.toUserMessage())
					}
				}
		}
	}

	fun clearDetail() {
		_uiState.update {
			it.copy(selectedTicket = null, detailError = null, actionError = null)
		}
	}

	fun clearCreateState() {
		_uiState.update { it.copy(createError = null) }
	}

	/**
	 * Creates a ticket, uploads every selected photo, refreshes the list and
	 * finally reports the created id so the UI can open the detail view.
	 */
	fun createTicket(
		titulo: String,
		descricao: String?,
		categoria: TicketCategoria,
		prioridade: TicketPrioridade?,
		photos: List<UploadFileRequest>,
		onCreated: (String) -> Unit,
	) {
		if (_uiState.value.isCreating) return

		viewModelScope.launch {
			_uiState.update { it.copy(isCreating = true, createError = null) }
			runCatching {
				val created = repository.create(
					CreateTicketRequest(
						titulo = titulo,
						descricao = descricao,
						origem = TicketOrigem.CLIENTE,
						categoria = categoria,
						prioridade = prioridade,
					),
				)
				photos.forEach { photo ->
					runCatching { repository.addPhoto(created.id, photo) }
						.onFailure { println("[TicketsViewModel] photo upload failed: ${it.message}") }
				}
				created
			}
				.onSuccess { created ->
					_uiState.update { it.copy(isCreating = false, createError = null) }
					loadList()
					openDetail(created.id)
					onCreated(created.id)
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isCreating = false, createError = error.toUserMessage())
					}
				}
		}
	}

	fun updateTicket(id: String, titulo: String, descricao: String?) {
		if (_uiState.value.isSaving) return

		viewModelScope.launch {
			_uiState.update { it.copy(isSaving = true, actionError = null) }
			runCatching {
				repository.update(
					id,
					UpdateTicketRequest(titulo = titulo, descricao = descricao),
				)
			}
				.onSuccess { ticket ->
					_uiState.update {
						it.copy(isSaving = false, selectedTicket = ticket)
					}
					loadList()
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isSaving = false, actionError = error.toUserMessage())
					}
				}
		}
	}

	fun addComment(id: String, mensagem: String, photo: UploadFileRequest?) {
		if (_uiState.value.isAddingComment) return

		viewModelScope.launch {
			_uiState.update { it.copy(isAddingComment = true, actionError = null) }
			runCatching {
				repository.addComment(id, CreateTicketCommentRequest(mensagem = mensagem))
				if (photo != null) {
					repository.addPhoto(id, photo)
				}
			}
				.onSuccess {
					_uiState.update { it.copy(isAddingComment = false) }
					openDetail(id)
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isAddingComment = false, actionError = error.toUserMessage())
					}
				}
		}
	}

	fun addPhoto(id: String, file: UploadFileRequest) {
		if (_uiState.value.isUploadingPhoto) return

		viewModelScope.launch {
			_uiState.update { it.copy(isUploadingPhoto = true, actionError = null) }
			runCatching { repository.addPhoto(id, file) }
				.onSuccess {
					_uiState.update { it.copy(isUploadingPhoto = false) }
					openDetail(id)
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(isUploadingPhoto = false, actionError = error.toUserMessage())
					}
				}
		}
	}

	fun canEdit(ticket: TicketResponse?): Boolean = ticket?.status == TicketStatus.ABERTO

	private fun Throwable.toUserMessage(): String {
		return when (this) {
			is ApiException -> message ?: "Falha ao comunicar com a API."
			else -> message ?: "Falha inesperada ao comunicar com a API."
		}
	}

	companion object {}
}
