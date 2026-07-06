package com.example.billionemotosappkt.desktop.admin.clientes.model

import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse

data class ClienteFormState(
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String = "",
    val cnh: String = "",
    val cnhCategoria: String = "",
    val endereco: String = "",
    val enderecoParente: String = "", // Adicionado para bater com o cURL e Dialog
    val cidade: String = "",
    val estado: String = "",
    val cep: String = "",
    val telefoneEmergencia1: String = "",
    val telefoneEmergencia2: String = "",
    val observacoes: String = "",
    val cnhUrl: String? = null,
    val status: ClienteAprovacaoStatus = ClienteAprovacaoStatus.PENDENTE,
    val comprovanteResidenciaUrl: String? = null,
    val comprovanteData: String = "", // Adicionado
    val planoId: String? = null
)

data class ClienteListItem(
    val id: String,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val cnh: String = "",
    val cnhCategoria: String = "",
    val endereco: String = "",
    val enderecoParente: String = "", // Adicionado para bater com o cURL e Dialog
    val cidade: String = "",
    val estado: String = "",
    val createdAt: String? = "",
    val telefone: String = "",
    val cep: String = "",
    val telefoneEmergencia1: String = "",
    val telefoneEmergencia2: String = "",
    val observacoes: String = "",
    val cnhUrl: String = "",
    val status: ClienteAprovacaoStatus = ClienteAprovacaoStatus.PENDENTE,
    val comprovanteResidenciaUrl: String = "",
    val planoId: String = "",
    val comprovanteData: String = ""
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
    val planoId = null
    return ClienteListItem(
        id = id,
        nome = nome,
        cpf = cpf ?: "",
        telefone = telefone.orEmpty().ifBlank { "(sem telefone)" },
        email = email.orEmpty().ifBlank { "(sem email)" },
        cnh = cnh ?: "",
        cnhCategoria = cnhCategoria ?: "",
        endereco = endereco ?: "",
        enderecoParente = enderecoParente ?: "", // Mapeado da response
        cidade = cidade.orEmpty().ifBlank { "Sem cidade" },
        estado = estado.orEmpty().ifBlank { "" },
        cep = cep ?: "",
        status = statusAprovacao,
        createdAt = createdAt,
        telefoneEmergencia1 = telefoneEmergencia1 ?: "",
        telefoneEmergencia2 = telefoneEmergencia2 ?: "",
        observacoes = observacoes ?: "",
        cnhUrl = cnhUrl ?: "",
        comprovanteResidenciaUrl = comprovanteResidenciaUrl ?: "",
        planoId = planoId ?: "",
        comprovanteData = comprovanteData ?: "" // Mapeado da response
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