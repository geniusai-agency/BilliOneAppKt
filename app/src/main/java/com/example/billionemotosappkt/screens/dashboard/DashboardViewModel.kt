package com.example.billionemotosappkt.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billionemotosappkt.data.dashboard.DashboardRepository
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

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
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"))
        return formatter.format(System.currentTimeMillis())
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is ApiException -> message ?: "Falha ao carregar a dashboard."
            else -> message ?: "Falha inesperada ao carregar a dashboard."
        }
    }

    companion object {
        fun factory(api: BillioneMotosApi): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(DashboardRepository(api)) as T
                }
            }
        }
    }
}
