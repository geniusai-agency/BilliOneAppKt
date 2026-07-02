package com.example.billionemotosappkt.desktop.admin.clientes.model

import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse

data class ClienteListItem(
    val id: String,
    val nome: String,
    val cpf: String,
    val telefone: String,
    val email: String,
    val cidade: String,
    val estado: String,
    val status: ClienteAprovacaoStatus,
    val createdAt: String? = null,
) {
    val initials: String = nome
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "")
        .uppercase()
        .ifBlank { "CL" }

    val cityLabel: String = listOfNotNull(cidade.takeIf { it.isNotBlank() }, estado.takeIf { it.isNotBlank() })
        .joinToString(separator = "/")
        .ifBlank { "Sem cidade" }
}

data class ClientesUiState(
    val isLoading: Boolean = true,
    val isFallbackData: Boolean = false,
    val errorMessage: String? = null,
    val clientes: List<ClienteListItem> = emptyList(),
)

enum class ClientesSortMode(val label: String) {
    NAME("Nome"),
    CITY("Cidade"),
    STATUS("Status"),
}

data class ClientesFilters(
    val query: String = "",
    val status: ClienteAprovacaoStatus? = null,
    val city: String? = null,
    val state: String? = null,
    val sortMode: ClientesSortMode = ClientesSortMode.NAME,
)

data class ClientesSummary(
    val total: Int,
    val approved: Int,
    val analysis: Int,
    val pending: Int,
    val rejected: Int,
)

fun ClienteResponse.toListItem(): ClienteListItem {
    return ClienteListItem(
        id = id,
        nome = nome,
        cpf = cpf,
        telefone = telefone.orEmpty().ifBlank { "(sem telefone)" },
        email = email.orEmpty().ifBlank { "(sem email)" },
        cidade = cidade.orEmpty().ifBlank { "Sem cidade" },
        estado = estado.orEmpty().ifBlank { "" },
        status = statusAprovacao,
        createdAt = createdAt,
    )
}

fun ClienteAprovacaoStatus.label(): String = when (this) {
    ClienteAprovacaoStatus.PENDENTE -> "Pendente"
    ClienteAprovacaoStatus.EM_ANALISE -> "Em análise"
    ClienteAprovacaoStatus.APROVADO -> "Aprovado"
    ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> "Aprovado c/ ressalva"
    ClienteAprovacaoStatus.REPROVADO -> "Reprovado"
}

fun ClienteAprovacaoStatus.accent(): Color = when (this) {
    ClienteAprovacaoStatus.PENDENTE -> Color(0xFFFFB300)
    ClienteAprovacaoStatus.EM_ANALISE -> Color(0xFF7DD3FC)
    ClienteAprovacaoStatus.APROVADO -> Color(0xFF20E65B)
    ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> Color(0xFF8B5CF6)
    ClienteAprovacaoStatus.REPROVADO -> Color(0xFFFF4A4A)
}

