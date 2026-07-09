package com.example.billionemotosappkt.desktop.admin.clientes.model

import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.shared.api.ClienteAnalisePedidoResponse
import com.example.billionemotosappkt.shared.api.ClienteAnalisePedidoStatus
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.UpdateClienteRequest
import kotlin.String
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

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
    val identidadeUrl: String = "",
    val status: ClienteAprovacaoStatus = ClienteAprovacaoStatus.PENDENTE,
    val comprovanteResidenciaUrl: String = "",
    val planoId: String = "",
    val planoNome: String = "",
    val contratoId: String = "",
    val contratoStatus: String = "",
    val motoNome: String = "",
    val motoPlaca: String = "",
    val comprovanteData: String = "",
    val contratoAssinadoEm: String? = null
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

data class AnaliseListItem(
    val id: String,
    val userId: String,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String = "",
    val endereco: String = "",
    val enderecoParente: String = "",
    val cidade: String = "",
    val estado: String = "",
    val cep: String = "",
    val observacoes: String = "",
    val cnh: String = "",
    val cnhCategoria: String = "",
    val planoId: String = "",
    val planoNome: String = "",
    val modeloMotoId: String = "",
    val motoId: String = "",
    val motoNome: String = "",
    val cnhUrl: String = "",
    val identidadeUrl: String = "",
    val comprovanteResidenciaUrl: String = "",
    val cooldownUntil: String = "",
    val decisaoJustificativa: String = "",
    val decididoPor: String = "",
    val decididoEm: String = "",
    val updatedAt: String = "",
    val status: ClienteAnalisePedidoStatus = ClienteAnalisePedidoStatus.PENDENTE,
    val createdAt: String? = "",
) {
    val cityLabel: String = listOfNotNull(cidade.takeIf { it.isNotBlank() }, estado.takeIf { it.isNotBlank() })
        .joinToString(separator = "/")
        .ifBlank { "Sem cidade" }
}

fun ClienteListItem.toUpdateRequest(): UpdateClienteRequest {
    return UpdateClienteRequest(
        nome = nome.orEmpty(),
        cpf = cpf.orEmpty(),
        email = email.orEmpty(),
        cnh = cnh.orEmpty(),
        cnhCategoria = cnhCategoria.orEmpty(),
        endereco = endereco.orEmpty(),
        identidadeUrl = identidadeUrl,
        enderecoParente = enderecoParente.orEmpty(),
        cidade = cidade.orEmpty(),
        estado = estado.orEmpty(),
        telefone = telefone.orEmpty(),
        cnhUrl = cnhUrl,
        comprovanteResidenciaUrl = comprovanteResidenciaUrl,
        cep = cep.orEmpty(),
        telefoneEmergencia1 = telefoneEmergencia1.orEmpty(),
        telefoneEmergencia2 = telefoneEmergencia2.orEmpty(),
        observacoes = observacoes.orEmpty(),
        planoId = planoId.orEmpty(),
        comprovanteData = comprovanteData
    )
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
    val usuario = usuario
    val nomeCliente = nome ?: usuario?.nome.orEmpty()
    val cpfCliente = cpf ?: usuario?.cpf.orEmpty()
    val emailCliente = email ?: usuario?.email.orEmpty()
    val telefoneCliente = telefone ?: usuario?.telefone.orEmpty()
    val contratoAtual = contratos.firstOrNull { it.status == com.example.billionemotosappkt.shared.api.ContratoStatus.ATIVO }
        ?: contrato
        ?: contratos.firstOrNull()
    val motoAtual = contratoAtual?.moto
    val planoAtual = plano ?: contratoAtual?.plano
    val planoNome = planoAtual?.nome ?: contratoAtual?.planoSnapshotNome.orEmpty()

    return ClienteListItem(
        id = id,
        nome = nomeCliente,
        cpf = cpfCliente,
        telefone = telefoneCliente.orEmpty().ifBlank { "(sem telefone)" },
        email = emailCliente.orEmpty().ifBlank { "(sem email)" },
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
        identidadeUrl = identidadeUrl ?: "",
        comprovanteResidenciaUrl = comprovanteResidenciaUrl ?: "",
        planoId = planoId ?: "",
        planoNome = planoNome,
        contratoId = contratoAtual?.id.orEmpty(),
        contratoStatus = contratoAtual?.status?.name.orEmpty(),
        motoNome = motoAtual?.modelo ?: motoAtual?.modeloMoto?.nome.orEmpty(),
        motoPlaca = motoAtual?.placa.orEmpty(),
        comprovanteData = comprovanteData ?: "" // Mapeado da response
    )
}

private fun JsonElement?.asString(key: String): String {
    val jsonObject = this as? JsonObject ?: return ""
    return jsonObject[key]
        ?.jsonPrimitive
        ?.contentOrNull
        ?.trim()
        .orEmpty()
}

fun ClienteAnalisePedidoResponse.toListItem(): AnaliseListItem {
    val usuarioLocal = usuario
    val payloadLocal = payload
    return AnaliseListItem(
        id = id,
        userId = userId ?: usuarioLocal?.id.orEmpty(),
        nome = usuarioLocal?.nome.orEmpty(),
        cpf = usuarioLocal?.cpf.orEmpty(),
        email = usuarioLocal?.email.orEmpty(),
        telefone = usuarioLocal?.telefone.orEmpty(),
        endereco = payloadLocal.asString("endereco"),
        enderecoParente = payloadLocal.asString("enderecoParente"),
        cidade = payloadLocal.asString("cidade"),
        estado = payloadLocal.asString("estado"),
        cep = payloadLocal.asString("cep"),
        observacoes = payloadLocal.asString("observacoes"),
        cnh = payloadLocal.asString("cnh"),
        cnhCategoria = payloadLocal.asString("cnhCategoria"),
        planoId = payloadLocal.asString("planoId"),
        planoNome = plano?.nome.orEmpty(),
        modeloMotoId = payloadLocal.asString("modeloMotoId"),
        motoId = payloadLocal.asString("motoId"),
        motoNome = payloadLocal.asString("modelo"),
        cnhUrl = payloadLocal.asString("cnhUrl"),
        identidadeUrl = payloadLocal.asString("identidadeUrl"),
        comprovanteResidenciaUrl = payloadLocal.asString("comprovanteResidenciaUrl"),
        cooldownUntil = cooldownUntil ?: "",
        decisaoJustificativa = decisaoJustificativa ?: "",
        decididoPor = decididoPor ?: "",
        decididoEm = decididoEm ?: "",
        updatedAt = updatedAt ?: "",
        status = status,
        createdAt = createdAt,
    )
}

fun ClienteAprovacaoStatus.label(): String = when (this) {
    ClienteAprovacaoStatus.PENDENTE -> "Pendente"
    ClienteAprovacaoStatus.EM_ANALISE -> "Em análise"
    ClienteAprovacaoStatus.APROVADO -> "Aprovado"
    ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> "Aprovado c/ ressalva"
    ClienteAprovacaoStatus.REPROVADO -> "Reprovado"
    ClienteAprovacaoStatus.AGUARDANDO_ASSINATURA -> "Aguard. assinatura"
}

fun ClienteAprovacaoStatus.accent(): Color = when (this) {
    ClienteAprovacaoStatus.PENDENTE -> Color(0xFFFFB300)
    ClienteAprovacaoStatus.EM_ANALISE -> Color(0xFF7DD3FC)
    ClienteAprovacaoStatus.APROVADO -> Color(0xFF20E65B)
    ClienteAprovacaoStatus.APROVADO_COM_RESSALVA -> Color(0xFF8B5CF6)
    ClienteAprovacaoStatus.REPROVADO -> Color(0xFFFF4A4A)
    ClienteAprovacaoStatus.AGUARDANDO_ASSINATURA -> Color(0xFF5BC0FF)
}

fun ClienteAnalisePedidoStatus.label(): String = when (this) {
    ClienteAnalisePedidoStatus.PENDENTE -> "Pendente"
    ClienteAnalisePedidoStatus.EM_ANALISE -> "Em análise"
    ClienteAnalisePedidoStatus.APROVADO -> "Aprovado"
    ClienteAnalisePedidoStatus.REPROVADO -> "Reprovado"
    ClienteAnalisePedidoStatus.CANCELADO -> "Cancelado"
}

fun ClienteAnalisePedidoStatus.accent(): Color = when (this) {
    ClienteAnalisePedidoStatus.PENDENTE -> Color(0xFFFFB300)
    ClienteAnalisePedidoStatus.EM_ANALISE -> Color(0xFF7DD3FC)
    ClienteAnalisePedidoStatus.APROVADO -> Color(0xFF20E65B)
    ClienteAnalisePedidoStatus.REPROVADO -> Color(0xFFFF4A4A)
    ClienteAnalisePedidoStatus.CANCELADO -> Color(0xFF9CA3AF)
}
