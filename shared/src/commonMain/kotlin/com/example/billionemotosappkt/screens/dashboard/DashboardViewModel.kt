package com.example.billionemotosappkt.screens.dashboard

import com.example.billionemotosappkt.shared.utils.ViewModel
import com.example.billionemotosappkt.data.dashboard.DashboardRepository
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.billionemotosappkt.shared.utils.currentEpochMillis
import com.example.billionemotosappkt.shared.utils.formatEpochMillisDateTime

class DashboardViewModel(
    private val repository: DashboardRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            val initialLoad = _uiState.value.overview == null
            _uiState.update {
                it.copy(
                    isLoading = initialLoad,
                    isRefreshing = true,
                    errorMessage = null,
                )
            }

            runCatching { repository.loadOverview() }
                .onSuccess { overview ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null,
                            overview = overview,
                            lastUpdatedAt = nowLabel(),
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

    private fun nowLabel(): String {
        return formatEpochMillisDateTime(currentEpochMillis())
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is ApiException -> message ?: "Falha ao carregar a dashboard."
            else -> message ?: "Falha inesperada ao carregar a dashboard."
        }
    }

    companion object {}
}
