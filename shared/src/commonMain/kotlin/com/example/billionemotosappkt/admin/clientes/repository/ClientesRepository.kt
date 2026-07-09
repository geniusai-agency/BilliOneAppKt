package com.example.billionemotosappkt.desktop.admin.clientes.repository

import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.AnaliseListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.toListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.DecisaoAprovacaoClienteResponse
import com.example.billionemotosappkt.shared.api.DecisaoClienteRequest
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListClientesQuery
import com.example.billionemotosappkt.shared.api.ListAnalisesQuery
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.UpdateClienteRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest

data class ClientesLoadResult(
    val clientes: List<ClienteListItem>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val isFallbackData: Boolean,
)

data class AnalisesLoadResult(
    val analises: List<AnaliseListItem>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val isFallbackData: Boolean,
)


data class ClientBody(
    var nome: String,
    val cpf: String,
    val email: String,
    val cnh: String,
    val cnhCategoria: String,
    val endereco: String,
    val enderecoParente: String,
    val cidade: String,
    val estado: String,
    val telefone: String,
    val cep: String,
    val telefoneEmergencia1: String,
    val telefoneEmergencia2: String,
    val observacoes: String,
    val planoId: String = "",
    val comprovanteData: String = "",
    val cnhFile: UploadFileRequest? = null,
    val identidadeFile: UploadFileRequest? = null,
    val comprovanteFile: UploadFileRequest? = null
)

fun ClientBody.toUpdateRequest(): UpdateClienteRequest {
    return UpdateClienteRequest(
        nome = nome,
        cpf = cpf,
        cnh = cnh,
        cnhCategoria = cnhCategoria,
        email = email,
        telefone = telefone,
        endereco = endereco,
        enderecoParente = enderecoParente,
        cidade = cidade,
        estado = estado,
        cep = cep,
        telefoneEmergencia1 = telefoneEmergencia1,
        telefoneEmergencia2 = telefoneEmergencia2,
        observacoes = observacoes,
        comprovanteData = comprovanteData,
        planoId = planoId,
    )
}

class ClientesRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun updateCliente(id: String, body: ClientBody): ClienteListItem {
        fun String?.takeIfNotEmpty(): String? = this?.takeIf { it.isNotBlank() }

        val request = UpdateClienteRequest(
            nome = body.nome.takeIfNotEmpty(),
            cpf = body.cpf.takeIfNotEmpty(),
            email = body.email.takeIfNotEmpty(),
            cnh = body.cnh.takeIfNotEmpty(),
            cnhCategoria = body.cnhCategoria.takeIfNotEmpty(),
            endereco = body.endereco.takeIfNotEmpty(),
            enderecoParente = body.enderecoParente.takeIfNotEmpty(),
            cidade = body.cidade.takeIfNotEmpty(),
            estado = body.estado.takeIfNotEmpty(),
            telefone = body.telefone.takeIfNotEmpty(),
            cep = body.cep.takeIfNotEmpty(),
            telefoneEmergencia1 = body.telefoneEmergencia1.takeIfNotEmpty(),
            telefoneEmergencia2 = body.telefoneEmergencia2.takeIfNotEmpty(),
            observacoes = body.observacoes.takeIfNotEmpty(),
            comprovanteData = body.comprovanteData.takeIfNotEmpty(),
            planoId = body.planoId,
        )

        val cnhReq = body.cnhFile
        val identidadeReq = body.identidadeFile
        val comprovanteReq = body.comprovanteFile

        val response = api.clientes.updateMultipart(
            id = id,
            request = request,
            cnhImage = cnhReq,
            identidadeImage = identidadeReq,
            comprovanteResidenciaImage = comprovanteReq
        )
        return response.toListItem()
    }

    suspend fun loadPlano(): List<PlanoResponse> = api.planos.list()

    suspend fun loadAnalises(query: ListAnalisesQuery = ListAnalisesQuery(limit = 50)): AnalisesLoadResult {
        val response = api.clientes.listAnalises(query)
        return AnalisesLoadResult(
            analises = response.items.map { it.toListItem() },
            total = response.total,
            page = response.page,
            limit = response.limit,
            isFallbackData = false,
        )
    }

    suspend fun loadAnalise(id: String): ClienteListItem {
        return api.clientes.get(id).toListItem()
    }

    suspend fun loadAnaliseHistory(id: String): List<DecisaoAprovacaoClienteResponse> {
        return api.clientes.decisionHistory(id)
    }

    suspend fun loadContratoAtual(clienteId: String): ContratoResponse? {
        val contratos = api.contratos.list(
            ListContratosQuery(
                clienteId = clienteId,
                includeRelations = true,
                page = 1,
                limit = 20,
            ),
        ).items

        return contratos
            .sortedByDescending { it.dataInicio }
            .firstOrNull()
    }

    suspend fun decideAnalise(id: String, request: DecisaoClienteRequest): ClienteListItem {
        return api.clientes.decide(id, request).let { api.clientes.get(id).toListItem() }
    }

    suspend fun loadClientes(query: ListClientesQuery = ListClientesQuery(limit = 20)): ClientesLoadResult {
        val response = api.clientes.list(query)
        return ClientesLoadResult(
            clientes = response.items.map { it.toListItem() },
            total = response.total,
            page = response.page,
            limit = response.limit,
            isFallbackData = false,
        )
    }
}
