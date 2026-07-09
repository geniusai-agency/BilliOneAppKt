package com.example.billionemotosappkt.desktop.admin.data

import com.example.billionemotosappkt.shared.api.MotoResponse

data class MotosUiState(
	val isLoading: Boolean = true,
	val motos: List<MotoResponse> = emptyList(),
	val errorMessage: String? = null,
)
