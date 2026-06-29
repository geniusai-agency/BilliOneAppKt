package com.example.billionemotosappkt.shared.api

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.request
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.http.appendPathSegments
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse

/**
 * High-level typed API surface for the backend documented in
 * `docs/api-contract-matrix.md`.
 */
class BillioneMotosApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {
    constructor(config: ApiConfig) : this(createBillioneMotosHttpClient(config), config)

    val auth = AuthEndpoints()
    val users = UsersEndpoints()
    val clientes = ClientesEndpoints()
    val planos = PlanosEndpoints()
    val motos = MotosEndpoints()
    val contratos = ContratosEndpoints()
    val pagamentos = PagamentosEndpoints()
    val financeiro = FinanceiroEndpoints()
    val tickets = TicketsEndpoints()
    val manutencoes = ManutencoesEndpoints()
    val relatorios = RelatoriosEndpoints()

    suspend fun close() {
        client.close()
    }

    private suspend inline fun <reified T> call(
        method: HttpMethod,
        path: String,
        query: Map<String, Any?> = emptyMap(),
        body: Any? = null,
        authorized: Boolean = true,
    ): T {
        logRequest(method, path, query, body, authorized)

        val response: HttpResponse = client.request {
            this.method = method
            url {
                takeFrom(config.baseUrl)
                val segments = path.trim('/').split('/').filter { it.isNotBlank() }
                appendPathSegments(*segments.toTypedArray())
                query.forEach { (key, value) ->
                    if (value == null) return@forEach
                    when (value) {
                        is Iterable<*> -> value.forEach { item ->
                            if (item != null) parameters.append(key, item.toString())
                        }
                        else -> parameters.append(key, value.toString())
                    }
                }
            }
            contentType(ContentType.Application.Json)
            config.defaultHeaders.forEach { (key, value) ->
                header(key, value)
            }
            if (authorized) {
                config.accessTokenProvider()?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (body != null) {
                setBody(body)
            }
        }

        val rawBody = response.bodyAsText()
        if (response.status.value !in 200..299) {
            logResponseError(method, path, response.status.value, rawBody)
            throw mapException(response.status.value, rawBody)
        }

        if (T::class == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            return Unit as T
        }

        return runCatching {
            decodeBody<T>(rawBody)
        }.onFailure { error ->
            logDecodeFailure(method, path, response.status.value, rawBody, error)
        }.getOrThrow()
    }

    private fun mapException(statusCode: Int, rawBody: String?): ApiException {
        val apiError = rawBody?.let { runCatching { BillioneMotosJson.decodeFromString(ApiErrorResponse.serializer(), it) }.getOrNull() }
        val message = when (val payload = apiError?.message) {
            null -> "HTTP $statusCode"
            else -> when {
                payload is kotlinx.serialization.json.JsonPrimitive -> payload.content
                else -> payload.toString()
            }
        }

        return ApiException(
            statusCode = statusCode,
            message = message,
            rawBody = rawBody,
        )
    }

    inner class AuthEndpoints {
        suspend fun login(request: CreateLoginRequest): AuthSessionResponse =
            call(HttpMethod.Post, "/auth/login", body = request, authorized = false)

        suspend fun authentication(): AuthenticationContextResponse =
            call(HttpMethod.Get, "/auth/authentication")

        suspend fun refresh(refreshToken: String): AuthSessionResponse =
            call(HttpMethod.Post, "/auth/refresh", body = RefreshTokenRequest(refreshToken), authorized = false)

        suspend fun logout(): ApiMessageResponse =
            call(HttpMethod.Delete, "/auth/logout")

        suspend fun logoutAll(): ApiMessageResponse =
            call(HttpMethod.Delete, "/auth/logout-all")

        suspend fun googleLogin(): AuthSessionResponse =
            call(HttpMethod.Post, "/auth/google")
    }

    inner class UsersEndpoints {
        suspend fun register(request: CreateUserRequest): UserResponse =
            call(HttpMethod.Post, "/users/register", body = request, authorized = false)

        suspend fun create(request: CreateUserRequest): UserResponse =
            call(HttpMethod.Post, "/users", body = request)

        suspend fun list(query: ListUsersQuery = ListUsersQuery()): PagedResponse<UserResponse> =
            call(
                HttpMethod.Get,
                "/users",
                query = query.toQueryMap(),
            )

        suspend fun me(): UserResponse = call(HttpMethod.Get, "/users/me")

        suspend fun getById(id: String): UserResponse =
            call(HttpMethod.Get, "/users/profile/$id")

        suspend fun updateMe(request: UpdateUserRequest): UserResponse =
            call(HttpMethod.Patch, "/users/me", body = request)

        suspend fun uploadMeImage(
            file: UploadFileRequest,
            descricao: String? = null,
        ): FotoResponse =
            multipartCall(HttpMethod.Post, "/users/me/imagem") {
                appendFile("file", file)
                appendText("descricao", descricao)
            }

        suspend fun solicitarAnalise(
            request: SolicitarAnaliseRequest,
            cnhImage: UploadFileRequest? = null,
            identidadeImage: UploadFileRequest? = null,
            comprovanteResidenciaImage: UploadFileRequest? = null,
        ): ClienteResponse =
            multipartCall(HttpMethod.Post, "/users/analises/solicitar") {
                appendText("cnh", request.cnh)
                appendText("cnhCategoria", request.cnhCategoria)
                appendText("cnhUrl", request.cnhUrl)
                appendText("comprovanteResidenciaUrl", request.comprovanteResidenciaUrl)
                appendText("comprovanteData", request.comprovanteData)
                appendText("enderecoParente", request.enderecoParente)
                appendText("telefoneEmergencia1", request.telefoneEmergencia1)
                appendText("telefoneEmergencia2", request.telefoneEmergencia2)
                appendText("observacoes", request.observacoes)
                appendText("endereco", request.endereco)
                appendText("cidade", request.cidade)
                appendText("estado", request.estado)
                appendText("cep", request.cep)
                appendText("planoId", request.planoId)
                appendText("modelo", request.modelo)
                appendText("dataInicio", request.dataInicio)
                if (cnhImage != null) appendFile("cnhImage", cnhImage)
                if (identidadeImage != null) appendFile("identidadeImage", identidadeImage)
                if (comprovanteResidenciaImage != null) appendFile("comprovanteResidenciaImage", comprovanteResidenciaImage)
            }

        suspend fun updateById(id: String, request: UpdateUserRequest): UserResponse =
            call(HttpMethod.Patch, "/users/profile/$id", body = request)

        suspend fun deleteMe(): UserResponse =
            call(HttpMethod.Delete, "/users/me")

        suspend fun deleteById(id: String): UserResponse =
            call(HttpMethod.Delete, "/users/profile/$id")
    }

    inner class ClientesEndpoints {
        suspend fun solicitarAnalise(request: CreateClienteRequest): ClienteResponse =
            call(HttpMethod.Post, "/clientes/solicitar-analise", body = request, authorized = false)

        suspend fun create(request: CreateClienteRequest): ClienteResponse =
            call(HttpMethod.Post, "/clientes", body = request)

        suspend fun list(query: ListClientesQuery = ListClientesQuery()): PagedResponse<ClienteResponse> =
            call(HttpMethod.Get, "/clientes", query = query.toQueryMap())

        suspend fun get(id: String): ClienteResponse =
            call(HttpMethod.Get, "/clientes/$id")

        suspend fun decisionHistory(id: String): List<DecisaoAprovacaoClienteResponse> =
            call(HttpMethod.Get, "/clientes/$id/historico-status")

        suspend fun update(id: String, request: UpdateClienteRequest): ClienteResponse =
            call(HttpMethod.Patch, "/clientes/$id", body = request)

        suspend fun decide(id: String, request: DecisaoClienteRequest): DecisaoAprovacaoClienteResponse =
            call(HttpMethod.Patch, "/clientes/$id/decisao", body = request)
    }

    inner class PlanosEndpoints {
        suspend fun create(request: CreatePlanoRequest): PlanoResponse =
            call(HttpMethod.Post, "/planos", body = request)

        suspend fun list(query: ListPlanosQuery = ListPlanosQuery()): List<PlanoResponse> =
            call(HttpMethod.Get, "/planos", query = query.toQueryMap())

        suspend fun get(id: String): PlanoResponse =
            call(HttpMethod.Get, "/planos/$id")

        suspend fun update(id: String, request: UpdatePlanoRequest): PlanoResponse =
            call(HttpMethod.Patch, "/planos/$id", body = request)

        suspend fun deactivate(id: String): PlanoResponse =
            call(HttpMethod.Patch, "/planos/$id/desativar")
    }

    inner class MotosEndpoints {
        suspend fun create(request: CreateMotoRequest): MotoResponse =
            call(HttpMethod.Post, "/motos", body = request)

        suspend fun list(query: ListMotosQuery = ListMotosQuery()): PagedResponse<MotoResponse> =
            call(HttpMethod.Get, "/motos", query = query.toQueryMap())

        suspend fun listDisponiveis(query: ListMotosQuery = ListMotosQuery()): PagedResponse<MotoResponse> =
            call(HttpMethod.Get, "/motos/disponiveis", query = query.toQueryMap())

        suspend fun modelos(): List<MotoModeloResponse> =
            call(HttpMethod.Get, "/motos/modelos", authorized = false)

        suspend fun get(id: String): MotoResponse =
            call(HttpMethod.Get, "/motos/$id")

        suspend fun update(id: String, request: UpdateMotoRequest): MotoResponse =
            call(HttpMethod.Patch, "/motos/$id", body = request)

        suspend fun delete(id: String): MotoResponse =
            call(HttpMethod.Delete, "/motos/$id")
    }

    inner class ContratosEndpoints {
        suspend fun create(request: CreateContratoRequest): ContratoResponse =
            call(HttpMethod.Post, "/contratos", body = request)

        suspend fun previewTemplate(
            planoId: String,
            modelo: String,
            adicionalIds: List<String> = emptyList(),
        ): ContratoTemplatePreviewResponse =
            call(
                HttpMethod.Get,
                "/contratos/template",
                query = mapOf(
                    "planoId" to planoId,
                    "modelo" to modelo,
                    "adicionalIds" to adicionalIds,
                ),
                authorized = false,
            )

        suspend fun approve(id: String): ContratoResponse =
            call(HttpMethod.Patch, "/contratos/$id/aprovar")

        suspend fun list(query: ListContratosQuery = ListContratosQuery()): PagedResponse<ContratoResponse> =
            call(HttpMethod.Get, "/contratos", query = query.toQueryMap())

        suspend fun get(id: String): ContratoResponse =
            call(HttpMethod.Get, "/contratos/$id")

        suspend fun parcelas(id: String, query: ListParcelasQuery = ListParcelasQuery()): PagedResponse<ParcelaResponse> =
            call(HttpMethod.Get, "/contratos/$id/parcelas", query = query.toQueryMap())
    }

    inner class PagamentosEndpoints {
        suspend fun create(request: CreatePagamentoRequest): PagamentoResponse =
            call(HttpMethod.Post, "/pagamentos", body = request)

        suspend fun list(query: ListPagamentosQuery = ListPagamentosQuery()): PagedResponse<PagamentoResponse> =
            call(HttpMethod.Get, "/pagamentos", query = query.toQueryMap())

        suspend fun get(id: String): PagamentoResponse =
            call(HttpMethod.Get, "/pagamentos/$id")

        suspend fun update(id: String, request: UpdatePagamentoRequest): PagamentoResponse =
            call(HttpMethod.Patch, "/pagamentos/$id", body = request)
    }

    inner class FinanceiroEndpoints {
        suspend fun criarCobranca(request: CreateCobrancaRequest): CobrancaResponse =
            call(HttpMethod.Post, "/financeiro/cobrancas", body = request)

        suspend fun emitirBoleto(request: EmitirBoletoRequest): SantanderBoletoResponse =
            call(HttpMethod.Post, "/financeiro/boletos", body = request)

        suspend fun baixarBoleto(id: String, request: BaixarBoletoRequest): SantanderBoletoResponse =
            call(HttpMethod.Patch, "/financeiro/boletos/$id/baixar", body = request)

        suspend fun conciliarPagamento(request: ConciliarPagamentoRequest): PagamentoResponse =
            call(HttpMethod.Post, "/financeiro/pagamentos/conciliar", body = request)

        suspend fun registrarDespesa(request: RegistrarDespesaRequest): DespesaResponse =
            call(HttpMethod.Post, "/financeiro/despesas", body = request)
    }

    inner class TicketsEndpoints {
        suspend fun create(request: CreateTicketRequest): TicketResponse =
            call(HttpMethod.Post, "/tickets", body = request)

        suspend fun list(query: ListTicketsQuery = ListTicketsQuery()): PagedResponse<TicketResponse> =
            call(HttpMethod.Get, "/tickets", query = query.toQueryMap())

        suspend fun get(id: String): TicketResponse =
            call(HttpMethod.Get, "/tickets/$id")

        suspend fun update(id: String, request: UpdateTicketRequest): TicketResponse =
            call(HttpMethod.Patch, "/tickets/$id", body = request)

        suspend fun updateStatus(id: String, request: UpdateTicketStatusRequest): TicketResponse =
            call(HttpMethod.Patch, "/tickets/$id/status", body = request)

        suspend fun addComment(id: String, request: CreateTicketCommentRequest): TicketCommentResponse =
            call(HttpMethod.Post, "/tickets/$id/comentarios", body = request)

        suspend fun listComments(id: String): List<TicketCommentResponse> =
            call(HttpMethod.Get, "/tickets/$id/comentarios")
    }

    inner class ManutencoesEndpoints {
        suspend fun create(request: CreateManutencaoRequest): ManutencaoResponse =
            call(HttpMethod.Post, "/manutencoes", body = request)

        suspend fun list(query: ListManutencoesQuery = ListManutencoesQuery()): PagedResponse<ManutencaoResponse> =
            call(HttpMethod.Get, "/manutencoes", query = query.toQueryMap())

        suspend fun get(id: String): ManutencaoResponse =
            call(HttpMethod.Get, "/manutencoes/$id")

        suspend fun update(id: String, request: UpdateManutencaoRequest): ManutencaoResponse =
            call(HttpMethod.Patch, "/manutencoes/$id", body = request)

        suspend fun updateStatus(id: String, request: UpdateManutencaoStatusRequest): ManutencaoResponse =
            call(HttpMethod.Patch, "/manutencoes/$id/status", body = request)

        suspend fun history(id: String): List<ManutencaoStatusHistoricoResponse> =
            call(HttpMethod.Get, "/manutencoes/$id/historico-status")
    }

    inner class RelatoriosEndpoints {
        suspend fun resumo(periodo: RelatorioPeriodoQuery = RelatorioPeriodoQuery()): RelatorioResumoResponse =
            call(HttpMethod.Get, "/relatorios/resumo", query = periodo.toQueryMap())

        suspend fun clientes(periodo: RelatorioPeriodoQuery = RelatorioPeriodoQuery()): List<RelatorioSecaoItemResponse> =
            call(HttpMethod.Get, "/relatorios/clientes", query = periodo.toQueryMap())

        suspend fun contratos(periodo: RelatorioPeriodoQuery = RelatorioPeriodoQuery()): List<RelatorioSecaoItemResponse> =
            call(HttpMethod.Get, "/relatorios/contratos", query = periodo.toQueryMap())

        suspend fun tickets(periodo: RelatorioPeriodoQuery = RelatorioPeriodoQuery()): List<RelatorioSecaoItemResponse> =
            call(HttpMethod.Get, "/relatorios/tickets", query = periodo.toQueryMap())

        suspend fun manutencoes(periodo: RelatorioPeriodoQuery = RelatorioPeriodoQuery()): List<RelatorioSecaoItemResponse> =
            call(HttpMethod.Get, "/relatorios/manutencoes", query = periodo.toQueryMap())
    }

    private suspend inline fun <reified T> multipartCall(
        method: HttpMethod,
        path: String,
        authorized: Boolean = true,
        build: FormBuilder.() -> Unit,
    ): T {
        logRequest(method, path, emptyMap(), "[multipart]", authorized)

        val response: HttpResponse = client.submitFormWithBinaryData(
            url = config.baseUrl.trimEnd('/') + "/" + path.trimStart('/'),
            formData = formData {
                build()
            },
        ) {
            this.method = method
            config.defaultHeaders.forEach { (key, value) ->
                header(key, value)
            }
            if (authorized) {
                config.accessTokenProvider()?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }

        val rawBody = response.bodyAsText()
        if (response.status.value !in 200..299) {
            logResponseError(method, path, response.status.value, rawBody)
            throw mapException(response.status.value, rawBody)
        }

        if (T::class == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            return Unit as T
        }

        return runCatching {
            decodeBody<T>(rawBody)
        }.onFailure { error ->
            logDecodeFailure(method, path, response.status.value, rawBody, error)
        }.getOrThrow()
    }

    private inline fun <reified T> decodeBody(rawBody: String): T {
        return BillioneMotosJson.decodeFromString(rawBody)
    }

    private fun logRequest(
        method: HttpMethod,
        path: String,
        query: Map<String, Any?>,
        body: Any?,
        authorized: Boolean,
    ) {
        val queryText = if (query.isEmpty()) "" else query.entries.joinToString(prefix = " query=", separator = ", ") { (key, value) ->
            "$key=${valuePreview(value)}"
        }
        val bodyText = if (body == null) "body=<none>" else "body=${body::class.simpleName ?: body.toString()}"
        println("[BillioneMotosApi] -> ${method.value} $path$queryText $bodyText authorized=$authorized")
    }

    private fun logResponseError(
        method: HttpMethod,
        path: String,
        statusCode: Int,
        rawBody: String,
    ) {
        println("[BillioneMotosApi] <- ${method.value} $path status=$statusCode body=${rawBody.preview(2000)}")
    }

    private fun logDecodeFailure(
        method: HttpMethod,
        path: String,
        statusCode: Int,
        rawBody: String,
        error: Throwable,
    ) {
        println(
            "[BillioneMotosApi] !! decode failed for ${method.value} $path status=$statusCode error=${error::class.simpleName}: ${error.message}"
        )
        println("[BillioneMotosApi] !! raw body for ${method.value} $path: ${rawBody.preview(4000)}")
    }

    private fun valuePreview(value: Any?): String {
        return when (value) {
            null -> "null"
            is Iterable<*> -> value.joinToString(prefix = "[", postfix = "]") { item -> item.previewValue() }
            else -> value.previewValue()
        }
    }

    private fun Any?.previewValue(): String {
        return when (this) {
            null -> "null"
            is String -> preview(80)
            else -> toString()
        }
    }

    private fun String.preview(maxChars: Int): String {
        val normalized = replace("\n", "\\n")
        return if (normalized.length <= maxChars) normalized else normalized.take(maxChars) + "...(truncated)"
    }
}

data class UploadFileRequest(
    val bytes: ByteArray,
    val fileName: String,
    val contentType: String,
)

private fun FormBuilder.appendText(name: String, value: String?) {
    if (!value.isNullOrBlank()) {
        append(name, value)
    }
}

private fun FormBuilder.appendFile(name: String, file: UploadFileRequest) {
    append(
        name,
        file.bytes,
        Headers.build {
            append(HttpHeaders.ContentType, file.contentType)
            append(HttpHeaders.ContentDisposition, "filename=\"${file.fileName}\"")
        },
    )
}

private fun ListUsersQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "q" to q,
    "kind" to kind,
    "status" to status,
    "role" to role,
    "clienteId" to clienteId,
    "hasProfile" to hasProfile,
    "page" to page,
    "limit" to limit,
    "sortBy" to sortBy,
    "sortOrder" to sortOrder,
)

private fun ListClientesQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "q" to q,
    "statusAprovacao" to statusAprovacao,
    "cidade" to cidade,
    "estado" to estado,
    "hasUser" to hasUser,
    "page" to page,
    "limit" to limit,
    "createdFrom" to createdFrom,
    "createdTo" to createdTo,
)

private fun ListPlanosQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "includeInactive" to includeInactive,
)

private fun ListMotosQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "q" to q,
    "status" to status,
    "hasTracker" to hasTracker,
    "page" to page,
    "limit" to limit,
)

private fun ListContratosQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "q" to q,
    "status" to status,
    "tipo" to tipo,
    "modalidade" to modalidade,
    "periodicidade" to periodicidade,
    "clienteId" to clienteId,
    "motoId" to motoId,
    "planoId" to planoId,
    "createdFrom" to createdFrom,
    "createdTo" to createdTo,
    "page" to page,
    "limit" to limit,
    "includeRelations" to includeRelations,
)

private fun ListParcelasQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "status" to status,
    "conciliada" to conciliada,
    "dataFrom" to dataFrom,
    "dataTo" to dataTo,
    "page" to page,
    "limit" to limit,
)

private fun ListPagamentosQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "q" to q,
    "status" to status,
    "clienteId" to clienteId,
    "contratoId" to contratoId,
    "parcelaId" to parcelaId,
    "createdFrom" to createdFrom,
    "createdTo" to createdTo,
    "page" to page,
    "limit" to limit,
    "includeRelations" to includeRelations,
)

private fun ListTicketsQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "page" to page,
    "limit" to limit,
    "status" to status,
    "origem" to origem,
    "categoria" to categoria,
    "prioridade" to prioridade,
    "clienteId" to clienteId,
    "contratoId" to contratoId,
    "createdFrom" to createdFrom,
    "createdTo" to createdTo,
    "q" to q,
)

private fun ListManutencoesQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "page" to page,
    "limit" to limit,
    "status" to status,
    "tipo" to tipo,
    "motoId" to motoId,
    "clienteId" to clienteId,
    "contratoId" to contratoId,
    "createdFrom" to createdFrom,
    "createdTo" to createdTo,
    "q" to q,
)

private fun RelatorioPeriodoQuery.toQueryMap(): Map<String, Any?> = mapOf(
    "from" to from,
    "to" to to,
)
