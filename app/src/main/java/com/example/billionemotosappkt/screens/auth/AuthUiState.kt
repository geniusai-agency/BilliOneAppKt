package com.example.billionemotosappkt.screens.auth

import com.example.billionemotosappkt.shared.api.AppRole
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ContratoStatus

/**
 * UI state for the login/authentication screen.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val avatarUrl: String? = null,
    val journeyState: String? = null,
    val journeyScreen: String? = null,
    val journeyTitle: String? = null,
    val journeyMessage: String? = null,
    val clienteId: String? = null,
    val clienteStatus: ClienteAprovacaoStatus? = null,
    val contractId: String? = null,
    val contractStatus: ContratoStatus? = null,
    val roles: List<AppRole> = emptyList(),
    val isAdmin: Boolean = false,
    val registrationSuccess: Boolean = false,
)
