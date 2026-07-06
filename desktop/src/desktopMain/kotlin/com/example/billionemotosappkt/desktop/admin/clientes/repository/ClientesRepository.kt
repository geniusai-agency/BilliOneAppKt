package com.example.billionemotosappkt.desktop.admin.clientes.repository

import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.toListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.ListClientesQuery
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.UpdateClienteRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import io.ktor.http.HttpMethod
import java.io.File
import org.jetbrains.skia.WStream

data class ClientesLoadResult(
    val clientes: List<ClienteListItem>,
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
    val status: ClienteAprovacaoStatus,
    val planoId: String = "",
    val comprovanteData: String = "",
    val cnhFile: java.io.File? = null,
    val identidadeFile: java.io.File? = null,
    val comprovanteFile: java.io.File? = null
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
        status = status
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
            status = body.status
        )

        val cnhReq = body.cnhFile?.toUploadFileRequest()
        val identidadeReq = body.identidadeFile?.toUploadFileRequest()
        val comprovanteReq = body.comprovanteFile?.toUploadFileRequest()

        val response = api.clientes.updateMultipart(
            id = id,
            request = request,
            cnhImage = cnhReq,
            identidadeImage = identidadeReq,
            comprovanteResidenciaImage = comprovanteReq
        )
        return response.toListItem()
    }


    private fun File.toUploadFileRequest(): UploadFileRequest {
        return UploadFileRequest(
            bytes = this.readBytes(),
            fileName = this.name,
            contentType = when (this.extension.lowercase()) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                else -> "application/octet-stream"
            }
        )
    }

    suspend fun loadPlano():  List<PlanoResponse> = api.planos.list()

    suspend fun loadClientes(): ClientesLoadResult {
        val response = api.clientes.list(
            ListClientesQuery(
                page = 1,
                limit = 20,
            )
        )
        return ClientesLoadResult(
            clientes = response.items.map { it.toListItem() },
            isFallbackData = false,
        )
    }
}
