package com.example.billionemotosappkt.shared.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class AppRole { CLIENTE, ADMIN, FINANCEIRO, MECANICO, OFICINA }

@Serializable
enum class UserKind { INTERNAL, CLIENT }

@Serializable
enum class UserStatus { ACTIVE, INVITED, PENDING_PASSWORD, SUSPENDED, ARCHIVED }

@Serializable
enum class ClienteAprovacaoStatus { PENDENTE, EM_ANALISE, APROVADO, APROVADO_COM_RESSALVA, REPROVADO }

@Serializable
enum class ClienteAnalisePedidoStatus { PENDENTE, EM_ANALISE, APROVADO, REPROVADO, CANCELADO }

@Serializable
enum class ContratoPeriodicidade { DIARIA, SEMANAL, QUINZENAL, MENSAL }

@Serializable
enum class ContratoStatus { PENDENTE_ASSINATURA, ATIVO, ENCERRADO, INADIMPLENTE, CANCELADO }

@Serializable
enum class ContratoTipo { LOCACAO, COMPRA_36M }

@Serializable
enum class ModalidadeContrato { ROTATIVO, ASSINATURA }

@Serializable
enum class ParcelaStatus { PENDENTE, PAGO, ATRASADO, CANCELADO }

@Serializable
enum class PagamentoStatus { PENDENTE, CONFIRMADO, CANCELADO, ESTORNADO }

@Serializable
enum class CobrancaTipo { GERACAO, LEMBRETE, ATRASO, MANUAL }

@Serializable
enum class CobrancaCanal { SISTEMA, EMAIL, WHATSAPP, SMS, TELEFONE }

@Serializable
enum class CobrancaStatus { ATIVA, ENCERRADA, CANCELADA }

@Serializable
enum class OrigemConciliacao { AUTOMATICA, MANUAL }

@Serializable
enum class HistoricoFinanceiroEvento {
    PARCELA_CRIADA,
    PARCELA_A_VENCER,
    PARCELA_VENCIDA,
    COBRANCA_GERADA,
    BOLETO_GERADO,
    BOLETO_REEMITIDO,
    BOLETO_CANCELADO,
    PAGAMENTO_CONCILIADO,
    PAGAMENTO_CONCILIADO_MANUALMENTE,
    PAGAMENTO_CONFIRMADO,
    NOTIFICACAO_ENVIADA,
}

@Serializable
enum class DespesaStatus { PENDENTE, PAGO, ATRASADO, CANCELADO }

@Serializable
enum class TicketStatus { ABERTO, EM_ANALISE, AGUARDANDO_CLIENTE, RESOLVIDO, CANCELADO }

@Serializable
enum class TicketPrioridade { BAIXA, MEDIA, ALTA, URGENTE }

@Serializable
enum class TicketOrigem { INTERNO, CLIENTE }

@Serializable
enum class TicketCategoria {
    DIVERGENCIA_VALOR,
    CONTESTACAO_COBRANCA,
    PEDIDO_ESTORNO,
    SEGUNDA_VIA_BOLETO,
    NEGOCIACAO,
    COMPROVANTE_PAGAMENTO,
    OUTROS,
}

@Serializable
enum class AgendamentoStatus { AGENDADA, CONCLUIDA, CANCELADA }

@Serializable
enum class ManutencaoTipo { REVISAO, CORRETIVA, PREVENTIVA, SINISTRO }

@Serializable
enum class ManutencaoStatus { ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA }

@Serializable
enum class ManutencaoItemTipo { PECA, SERVICO }

@Serializable
enum class ManutencaoItemCobranca { GARANTIA, CLIENTE }

@Serializable
enum class MotoStatus {
    DISPONIVEL,
    PENDENTE_CONTRATO,
    CONTRATADA,
    ALUGADA,
    AGUARDANDO_DEVOLUCAO,
    BLOQUEADA,
    MANUTENCAO,
}

@Serializable
enum class SantanderAmbiente { HOMOLOGACAO, PRODUCAO }

@Serializable
enum class SantanderRemessaStatus { GERADA, ENVIADA, PROCESSADA, ERRO, CANCELADA }

@Serializable
enum class SantanderBoletoStatus {
    PENDENTE_ENVIO,
    ENVIADO,
    REGISTRADO,
    LIQUIDADO,
    BAIXADO,
    REJEITADO,
    ALTERADO,
    PROTESTADO,
}

@Serializable
enum class SantanderRetornoStatus { PROCESSADO, PARCIAL, ERRO }

@Serializable
enum class ConsultaTipo { SPC_SERASA, CRIMINAL, BASE_COMPARTILHADA }

@Serializable
enum class ConsultaResultado { APROVADO, RESTRICAO_LEVE, RESTRICAO_GRAVE, REPROVADO, ERRO }

@Serializable
data class PagedResponse<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val limit: Int,
)

@Serializable
data class ApiMessageResponse(
    val message: String,
)

@Serializable
data class ApiErrorResponse(
    val statusCode: Int? = null,
    val message: JsonElement? = null,
    val error: String? = null,
)

@Serializable
data class ProfilePreferenceResponse(
    val id: String? = null,
    val profileId: String? = null,
    val key: String,
    val value: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ProfileResponse(
    val userId: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val timezone: String? = null,
    val language: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val preferences: ProfilePreferenceResponse? = null,
)

@Serializable
data class UserRoleResponse(
    val id: String? = null,
    val userId: String? = null,
    val role: AppRole,
    val createdAt: String? = null,
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val passwordHash: String? = null,
    val nome: String,
    val cpf: String? = null,
    val telefone: String? = null,
    val avatarUrl: String? = null,
    val kind: UserKind,
    val status: UserStatus,
    val emailVerifiedAt: String? = null,
    val lastLoginAt: String? = null,
    val passwordUpdatedAt: String? = null,
    val mustChangePassword: Boolean = false,
    val deletedAt: String? = null,
    val clienteId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val profile: ProfileResponse? = null,
    val roles: List<UserRoleResponse> = emptyList(),
)

@Serializable
data class AuthJourneyResponse(
    val state: String,
    val screen: String,
    val title: String,
    val message: String,
    val nextAction: String,
    val clienteId: String? = null,
    val clienteStatus: ClienteAprovacaoStatus? = null,
    val contractId: String? = null,
    val contractStatus: ContratoStatus? = null,
    val rejectionReason: String? = null,
    val roles: List<AppRole> = emptyList(),
)

@Serializable
data class AuthTokensResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class AuthSessionResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: UserResponse,
    val journey: AuthJourneyResponse,
)

@Serializable
data class AuthenticationContextResponse(
    val user: UserResponse,
    val journey: AuthJourneyResponse,
)

@Serializable
data class CreateLoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class CreateProfilePreferenceRequest(
    val key: String,
    val value: String,
)

@Serializable
data class CreateProfileRequest(
    val avatarUrl: String? = null,
    val bio: String? = null,
    val timezone: String? = null,
    val language: String? = null,
    val preference: CreateProfilePreferenceRequest? = null,
)

@Serializable
data class UpdateProfilePreferenceRequest(
    val key: String,
    val value: String,
)

@Serializable
data class UpdateProfileRequest(
    val avatarUrl: String? = null,
    val bio: String? = null,
    val timezone: String? = null,
    val language: String? = null,
    val preference: UpdateProfilePreferenceRequest? = null,
)

@Serializable
data class CreateUserRequest(
    val email: String,
    val cpf: String,
    val password: String,
    val nome: String,
    val telefone: String? = null,
    val kind: UserKind? = null,
    val status: UserStatus? = null,
    val clienteId: String? = null,
    val roles: List<AppRole>? = null,
    val profile: CreateProfileRequest? = null,
    
)

@Serializable
data class UpdateUserRequest(
    val nome: String? = null,
    val telefone: String? = null,
    val kind: UserKind? = null,
    val status: UserStatus? = null,
    val clienteId: String? = null,
    val roles: List<AppRole>? = null,
    val profile: UpdateProfileRequest? = null,
    val email: String? = null,
)

@Serializable
data class ListUsersQuery(
    val q: String? = null,
    val kind: UserKind? = null,
    val status: UserStatus? = null,
    val role: AppRole? = null,
    val clienteId: String? = null,
    val hasProfile: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val sortBy: String? = null,
    val sortOrder: String? = null,
)

@Serializable
data class CreateClienteRequest(
    val nome: String,
    val cpf: String,
    val cnh: String? = null,
    val cnhCategoria: String? = null,
    val cnhUrl: String? = null,
    val comprovanteResidenciaUrl: String? = null,
    val comprovanteData: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val endereco: String? = null,
    val enderecoParente: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val cep: String? = null,
    val telefoneEmergencia1: String? = null,
    val telefoneEmergencia2: String? = null,
    val observacoes: String? = null,
    val planoId: String? = null,
    val motoId: String? = null,
)

@Serializable
data class SolicitarAnaliseRequest(
    val cnh: String,
    val cnhCategoria: String,
    val cnhUrl: String,
    val comprovanteResidenciaUrl: String,
    val comprovanteData: String,
    val enderecoParente: String,
    val telefoneEmergencia1: String,
    val telefoneEmergencia2: String,
    val observacoes: String,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val cep: String,
    val planoId: String,
    val modeloMotoId: String,
    val dataInicio: String? = null,
)

@Serializable
data class UpdateClienteRequest(
    val nome: String? = null,
    val cpf: String? = null,
    val cnh: String? = null,
    val cnhCategoria: String? = null,
    val cnhUrl: String? = null,
    val identidadeUrl: String? = null,
    val comprovanteResidenciaUrl: String? = null,
    val comprovanteData: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val endereco: String? = null,
    val enderecoParente: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val planoId: String,
    // val status: ClienteAprovacaoStatus,
    val cep: String? = null,
    val telefoneEmergencia1: String? = null,
    val telefoneEmergencia2: String? = null,
    val observacoes: String? = null,
)


@Serializable
data class DecisaoClienteRequest(
    val decisao: ClienteAprovacaoStatus,
    val justificativa: String,
    val motoId: String? = null,
)

@Serializable
data class ListClientesQuery(
    val q: String? = null,
    val statusAprovacao: ClienteAprovacaoStatus? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val hasUser: Boolean? = null,
    val planoId: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
)

@Serializable
data class ListAnalisesQuery(
    val q: String? = null,
    val statusAprovacao: ClienteAprovacaoStatus? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val hasUser: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val sortBy: String? = null,
    val sortOrder: String? = null,
)

@Serializable
data class ClienteResponse(
    val id: String,
    val nome: String? = null,
    val cpf: String? = null,
    val cnh: String? = null,
    val cnhCategoria: String? = null,
    val cnhUrl: String? = null,
    val identidadeUrl: String? = null,
    val comprovanteResidenciaUrl: String? = null,
    val comprovanteData: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val planoId: String? = null,
    val endereco: String? = null,
    val enderecoParente: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val cep: String? = null,
    val telefoneEmergencia1: String? = null,
    val telefoneEmergencia2: String? = null,
    val statusAprovacao: ClienteAprovacaoStatus,
    val usuario: UserResponse? = null,
    val plano: PlanoResponse? = null,
    val contrato: ContratoResponse? = null,
    val contratos: List<ContratoResponse> = emptyList(),
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ClienteAnalisePedidoResponse(
    val id: String,
    val userId: String? = null,
    val status: ClienteAnalisePedidoStatus,
    val payload: JsonElement? = null,
    val cooldownUntil: String? = null,
    val decisaoJustificativa: String? = null,
    val decididoPor: String? = null,
    val decididoEm: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val usuario: UserResponse? = null,
    val plano: PlanoResponse? = null,
)

@Serializable
data class DecisaoAprovacaoClienteResponse(
    val id: String,
    val clienteId: String,
    val decisao: ClienteAprovacaoStatus,
    val justificativa: String,
    val decididoPor: String? = null,
    val decididoPorNome: String? = null,
    val decididoPorEmail: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class ListPlanosQuery(
    val includeInactive: Boolean? = null,
)

@Serializable
data class PlanoResponse(
    val id: String,
    val nome: String,
    val nivel: String,
    val valor: String,
    val tags: List<String> = emptyList(),
    val descricao: String? = null,
    val ativo: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class PlanoVersaoResponse(
    val id: String,
    val planoId: String,
    val versao: Int,
    val nome: String,
    val nivel: String,
    val valor: String,
    val tags: List<String> = emptyList(),
    val descricao: String? = null,
    val ativo: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class CreatePlanoRequest(
    val nome: String,
    val nivel: String,
    val valor: Double,
    val tags: List<String>? = null,
    val descricao: String? = null,
)

@Serializable
data class UpdatePlanoRequest(
    val nome: String? = null,
    val nivel: String? = null,
    val valor: Double? = null,
    val tags: List<String>? = null,
    val descricao: String? = null,
    val ativo: Boolean? = null,
)

@Serializable
data class ListMotosQuery(
    val q: String? = null,
    val status: MotoStatus? = null,
    val hasTracker: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
)

@Serializable
data class RastreadorResponse(
    val id: String,
    val motoId: String,
    val provider: String = "generico",
    val deviceId: String,
    val apiUrl: String? = null,
    val ativo: Boolean = true,
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class MotoCountsResponse(
    val contratos: Int = 0,
    val manutencoes: Int = 0,
    val documentos: Int = 0,
    val agendamentos: Int = 0,
)

@Serializable
data class MotoModeloResponse(
    val id: String? = null,
    val marca: String? = null,
    val nome: String? = null,
    val cilindrada: Int? = null,
    val precoInicial: String? = null,
    val combustivel: String? = null,
    val categoria: String? = null,
    val tipo: String? = null,
    val ano: Int? = null,
    val codigoFipe: String? = null,
    val descricao: String? = null,
    val imagemReferenciaUrl: String? = null,
    val fotoUrls: List<String> = emptyList(),
    val modelo: String? = null,
    val totalMotos: Int = 0,
    val marcas: List<String> = emptyList(),
    val anos: List<Int> = emptyList(),
    val cores: List<String> = emptyList(),
    val statusDisponiveis: List<MotoStatus> = emptyList(),
    val possuiRastreador: Boolean = false,
)

@Serializable
data class CreateMotoModeloRequest(
    val marca: String,
    val nome: String,
    val cilindrada: Int? = null,
    val precoInicial: String? = null,
    val combustivel: String? = null,
    val categoria: String? = null,
    val tipo: String? = null,
    val ano: Int? = null,
    val codigoFipe: String? = null,
    val descricao: String? = null,
    val fotoUrls: List<String> = emptyList(),
)

@Serializable
data class UpdateMotoModeloRequest(
    val marca: String? = null,
    val nome: String? = null,
    val cilindrada: Int? = null,
    val precoInicial: String? = null,
    val combustivel: String? = null,
    val categoria: String? = null,
    val tipo: String? = null,
    val ano: Int? = null,
    val codigoFipe: String? = null,
    val descricao: String? = null,
    val imagemReferenciaUrl: String? = null,
    val fotoUrls: List<String>? = null,
)

@Serializable
data class MotoResponse(
    val id: String,
    val modeloMotoId: String? = null,
    val modelo: String,
    val marca: String? = null,
    val placa: String,
    val chassi: String? = null,
    val renavam: String? = null,
    val ano: Int? = null,
    val cor: String? = null,
    val kmAtual: Int = 0,
    val status: MotoStatus,
    val logicaVeiculoId: String? = null,
    val observacoes: String? = null,
    val fipePayload: JsonElement? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val rastreador: RastreadorResponse? = null,
    val modeloMoto: MotoModeloResponse? = null,
    val fotoUrls: List<String> = emptyList(),
    val fotoPrincipalUrl: String? = null,
    val count: MotoCountsResponse? = null,
)

@Serializable
data class FotoOwnerResponse(
    val id: String,
    val nome: String? = null,
    val cpf: String? = null,
    val email: String? = null,
)

@Serializable
data class FotoResponse(
    val id: String,
    val tipo: String,
    val subtipo: String,
    val nomeArquivo: String,
    val mimeType: String,
    val tamanhoBytes: Long,
    val hashSha256: String? = null,
    val descricao: String? = null,
    val userId: String? = null,
    val clienteId: String? = null,
    val deletedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val user: FotoOwnerResponse? = null,
    val cliente: FotoOwnerResponse? = null,
)

@Serializable
data class CreateMotoRequest(
    val modelo: String,
    val placa: String,
    val marca: String? = null,
    val chassi: String? = null,
    val renavam: String? = null,
    val ano: Int? = null,
    val cor: String? = null,
    val kmAtual: Int? = null,
    val status: MotoStatus? = null,
    val logicaVeiculoId: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class UpdateMotoRequest(
    val modelo: String? = null,
    val placa: String? = null,
    val marca: String? = null,
    val chassi: String? = null,
    val renavam: String? = null,
    val ano: Int? = null,
    val cor: String? = null,
    val kmAtual: Int? = null,
    val status: MotoStatus? = null,
    val logicaVeiculoId: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class CreateContratoRequest(
    val clienteId: String,
    val motoId: String,
    val planoId: String,
    val tipo: ContratoTipo? = null,
    val status: ContratoStatus? = null,
    val modalidade: ModalidadeContrato? = null,
    val periodicidade: ContratoPeriodicidade? = null,
    val dataInicio: String,
    val dataFim: String? = null,
    val valorParcela: Double,
    val numParcelas: Int? = null,
    val valorEntrada: Double? = null,
    val valorCaucao: Double? = null,
    val diaVencimento: Int? = null,
    val carenciaDias: Int? = null,
    val diasBloqueio: Int? = null,
    val diasAlertaDevolucao: Int? = null,
    val diasRescisaoAutomatica: Int? = null,
    val motivoBloqueio: String? = null,
    val motivoRescisao: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class UpdateContratoRequest(
    val clienteId: String? = null,
    val motoId: String? = null,
    val planoId: String? = null,
    val tipo: ContratoTipo? = null,
    val status: ContratoStatus? = null,
    val modalidade: ModalidadeContrato? = null,
    val periodicidade: ContratoPeriodicidade? = null,
    val dataInicio: String? = null,
    val dataFim: String? = null,
    val valorParcela: Double? = null,
    val numParcelas: Int? = null,
    val valorEntrada: Double? = null,
    val valorCaucao: Double? = null,
    val diaVencimento: Int? = null,
    val carenciaDias: Int? = null,
    val diasBloqueio: Int? = null,
    val diasAlertaDevolucao: Int? = null,
    val diasRescisaoAutomatica: Int? = null,
    val motivoBloqueio: String? = null,
    val motivoRescisao: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class ListContratosQuery(
    val q: String? = null,
    val status: ContratoStatus? = null,
    val tipo: ContratoTipo? = null,
    val modalidade: ModalidadeContrato? = null,
    val periodicidade: ContratoPeriodicidade? = null,
    val clienteId: String? = null,
    val motoId: String? = null,
    val planoId: String? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val includeRelations: Boolean? = null,
)

@Serializable
data class ListParcelasQuery(
    val status: ParcelaStatus? = null,
    val conciliada: Boolean? = null,
    val dataFrom: String? = null,
    val dataTo: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
)

@Serializable
data class CobrancaResponse(
    val id: String,
    val parcelaId: String,
    val contratoId: String,
    val tipo: CobrancaTipo,
    val canal: CobrancaCanal,
    val status: CobrancaStatus,
    val valor: String,
    val mensagem: String? = null,
    val enviadaEm: String? = null,
    val encerradaEm: String? = null,
    val encerradaPorId: String? = null,
    val encerradaPorNome: String? = null,
    val encerradaMotivo: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class PagamentoResponse(
    val id: String,
    val contratoId: String,
    val parcelaId: String,
    val clienteId: String,
    val valor: String,
    val dataPagamento: String,
    val formaPagamento: String,
    val status: PagamentoStatus,
    val origemConciliacao: OrigemConciliacao? = null,
    val conciliadoPorId: String? = null,
    val conciliadoEm: String? = null,
    val observacaoConciliacao: String? = null,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
    val criadoPor: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class SantanderBoletoResponse(
    val id: String,
    val parcelaId: String,
    val cobrancaId: String? = null,
    val remessaId: String? = null,
    val nossoNumero: String,
    val nossoNumeroDv: String,
    val nossoNumeroFormatado: String,
    val carteira: String,
    val especieTitulo: String,
    val aceite: String,
    val valorTitulo: String,
    val dataVencimento: String,
    val dataEmissao: String,
    val linhaDigitavel: String? = null,
    val codigoBarras: String? = null,
    val status: SantanderBoletoStatus,
    val valorPago: String? = null,
    val valorJuros: String? = null,
    val valorDesconto: String? = null,
    val valorTarifa: String? = null,
    val dataCredito: String? = null,
    val codigoMovimentoAtual: String? = null,
    val descricaoMovimentoAtual: String? = null,
    val pdfStoragePath: String? = null,
    val pagadorNome: String? = null,
    val pagadorDocumento: String? = null,
    val pagadorEnderecoCompleto: String? = null,
    val ambiente: SantanderAmbiente,
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class PlanoSnapshotResponse(
    val id: String,
    val nome: String,
    val nivel: String,
    val valor: String,
    val descricao: String? = null,
    val tags: List<String> = emptyList(),
    val versao: PlanoVersaoResponse? = null,
)

@Serializable
data class ClienteCompactResponse(
    val id: String,
    val nome: String,
    val cpf: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val statusAprovacao: ClienteAprovacaoStatus? = null,
)

@Serializable
data class ParcelaResponse(
    val id: String,
    val contratoId: String,
    val numero: Int,
    val dataVencimento: String,
    val valor: String,
    val valorPago: String = "0",
    val dataPagamento: String? = null,
    val formaPagamento: String? = null,
    val status: ParcelaStatus,
    val observacoes: String? = null,
    val pixCopiaCola: String? = null,
    val boletoLinhaDigitavel: String? = null,
    val boletoUrl: String? = null,
    val pagamentoGeradoEm: String? = null,
    val conciliada: Boolean = false,
    val conciliadaEm: String? = null,
    val conciliadaPor: String? = null,
    val conciliadaPorNome: String? = null,
    val conciliadaPorEmail: String? = null,
    val observacoesConciliacao: String? = null,
    val comprovanteConciliacaoUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val santanderBoleto: SantanderBoletoResponse? = null,
    val cobrancas: List<CobrancaResponse> = emptyList(),
    val pagamentos: List<PagamentoResponse> = emptyList(),
)

@Serializable
data class ContratoCountResponse(
    val parcelas: Int = 0,
    val cobrancas: Int = 0,
    val manutencoes: Int = 0,
    val tickets: Int = 0,
    val comandosRastreador: Int = 0,
)

@Serializable
data class ContratoResponse(
    val id: String,
    val clienteId: String,
    val motoId: String,
    val tipo: ContratoTipo,
    val status: ContratoStatus,
    val modalidade: ModalidadeContrato,
    val planoId: String,
    val planoVersaoId: String,
    val planoSnapshotNome: String,
    val planoSnapshotNivel: String,
    val planoSnapshotValor: String,
    val planoSnapshotTags: List<String> = emptyList(),
    val planoSnapshotDescricao: String? = null,
    val periodicidade: ContratoPeriodicidade,
    val dataInicio: String,
    val dataFim: String? = null,
    val valorParcela: String,
    val numParcelas: Int,
    val valorEntrada: String = "0",
    val valorCaucao: String = "0",
    val diaVencimento: Int? = null,
    val carenciaDias: Int = 7,
    val diasBloqueio: Int = 3,
    val diasAlertaDevolucao: Int = 6,
    val diasRescisaoAutomatica: Int = 30,
    val bloqueadaEm: String? = null,
    val motivoBloqueio: String? = null,
    val alertaDevolucaoEm: String? = null,
    val rescindidoEm: String? = null,
    val motivoRescisao: String? = null,
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val pdfUrl: String? = null,
    val cliente: ClienteResponse? = null,
    val moto: MotoResponse? = null,
    val plano: PlanoResponse? = null,
    val planoVersao: PlanoVersaoResponse? = null,
    val parcelas: List<ParcelaResponse> = emptyList(),
    val count: ContratoCountResponse? = null,
)

@Serializable
data class ContratoTemplatePreviewResponse(
    val plano: PlanoSnapshotResponse,
    val moto: MotoResponse,
    val contrato: ContratoTemplatePreviewContractResponse,
    val orientacao: String,
)

@Serializable
data class ContratoTemplatePreviewContractResponse(
    val tipo: ContratoTipo,
    val modalidade: ModalidadeContrato,
    val periodicidade: ContratoPeriodicidade,
    val dataInicio: String,
    val numParcelas: Int,
    val valorParcela: String,
    val valorTotal: String,
    val status: String,
)

@Serializable
data class CreatePagamentoRequest(
    val contratoId: String,
    val parcelaId: String,
    val clienteId: String,
    val dataPagamento: String,
    val formaPagamento: String,
    val status: PagamentoStatus? = null,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class UpdatePagamentoRequest(
    val contratoId: String? = null,
    val parcelaId: String? = null,
    val clienteId: String? = null,
    val valor: Double? = null,
    val dataPagamento: String? = null,
    val formaPagamento: String? = null,
    val status: PagamentoStatus? = null,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class ListPagamentosQuery(
    val q: String? = null,
    val status: PagamentoStatus? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaId: String? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val includeRelations: Boolean? = null,
)

@Serializable
data class CreateCobrancaRequest(
    val parcelaId: String,
    val contratoId: String,
    val tipo: CobrancaTipo? = null,
    val canal: CobrancaCanal? = null,
    val mensagem: String? = null,
    val valor: Double? = null,
)

@Serializable
data class EmitirBoletoRequest(
    val parcelaId: String,
    val remessaId: String? = null,
    val ambiente: SantanderAmbiente? = null,
    val observacoes: String? = null,
)

@Serializable
data class BaixarBoletoRequest(
    val dataCredito: String? = null,
    val valorPago: Double? = null,
    val valorJuros: Double? = null,
    val valorDesconto: Double? = null,
    val valorTarifa: Double? = null,
    val status: SantanderBoletoStatus? = null,
    val codigoMovimentoAtual: String? = null,
    val descricaoMovimentoAtual: String? = null,
)

@Serializable
data class ConciliarPagamentoRequest(
    val parcelaId: String,
    val contratoId: String,
    val clienteId: String,
    val valor: Double,
    val dataPagamento: String,
    val formaPagamento: String,
    val status: PagamentoStatus? = null,
    val boletoId: String? = null,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class RegistrarDespesaRequest(
    val descricao: String,
    val fornecedorId: String? = null,
    val categoriaId: String? = null,
    val centroCusto: String? = null,
    val valor: Double,
    val dataVencimento: String,
    val dataPagamento: String? = null,
    val formaPagamento: String? = null,
    val status: DespesaStatus? = null,
    val recorrente: Boolean? = null,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
)

@Serializable
data class DespesaResponse(
    val id: String,
    val descricao: String,
    val fornecedorId: String? = null,
    val categoriaId: String? = null,
    val centroCusto: String? = null,
    val valor: String,
    val dataVencimento: String,
    val dataPagamento: String? = null,
    val formaPagamento: String? = null,
    val status: DespesaStatus,
    val recorrente: Boolean = false,
    val comprovanteUrl: String? = null,
    val observacoes: String? = null,
    val createdBy: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ListTicketsQuery(
    val page: Int? = null,
    val limit: Int? = null,
    val status: TicketStatus? = null,
    val origem: TicketOrigem? = null,
    val categoria: TicketCategoria? = null,
    val prioridade: TicketPrioridade? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val q: String? = null,
)

@Serializable
data class CreateTicketRequest(
    val titulo: String,
    val descricao: String? = null,
    val origem: TicketOrigem? = null,
    val categoria: TicketCategoria? = null,
    val prioridade: TicketPrioridade? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaId: String? = null,
    val despesaId: String? = null,
    val valorEnvolvido: Double? = null,
)

@Serializable
data class UpdateTicketRequest(
    val titulo: String? = null,
    val descricao: String? = null,
    val origem: TicketOrigem? = null,
    val categoria: TicketCategoria? = null,
    val prioridade: TicketPrioridade? = null,
    val status: TicketStatus? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaId: String? = null,
    val despesaId: String? = null,
    val valorEnvolvido: Double? = null,
)

@Serializable
data class UpdateTicketStatusRequest(
    val status: TicketStatus,
    val resolucao: String? = null,
)

@Serializable
data class CreateTicketCommentRequest(
    val mensagem: String,
    val anexoUrl: String? = null,
)

@Serializable
data class TicketCommentResponse(
    val id: String,
    val ticketId: String,
    val autorId: String? = null,
    val autorNome: String? = null,
    val autorTipo: String = "interno",
    val mensagem: String,
    val anexoUrl: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class TicketResponse(
    val id: String,
    val numero: Int,
    val titulo: String,
    val descricao: String? = null,
    val origem: TicketOrigem,
    val categoria: TicketCategoria,
    val prioridade: TicketPrioridade,
    val status: TicketStatus,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaId: String? = null,
    val despesaId: String? = null,
    val responsavelId: String? = null,
    val responsavelNome: String? = null,
    val valorEnvolvido: String? = null,
    val abertoPor: String? = null,
    val abertoPorNome: String? = null,
    val resolvidoEm: String? = null,
    val resolucao: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val cliente: ClienteResponse? = null,
    val contrato: ContratoResponse? = null,
    val parcela: ParcelaResponse? = null,
    val despesa: DespesaResponse? = null,
    val comentarios: List<TicketCommentResponse> = emptyList(),
)

@Serializable
data class ListManutencoesQuery(
    val page: Int? = null,
    val limit: Int? = null,
    val status: ManutencaoStatus? = null,
    val tipo: ManutencaoTipo? = null,
    val motoId: String? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val q: String? = null,
)

@Serializable
data class CreateManutencaoRequest(
    val motoId: String,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaCobrancaId: String? = null,
    val tipo: ManutencaoTipo? = null,
    val descricao: String,
    val kmRegistrado: Int? = null,
    val oficina: String? = null,
    val responsavel: String? = null,
    val custoPecas: Double? = null,
    val custoMaoObra: Double? = null,
    val custoGarantia: Double? = null,
    val custoCliente: Double? = null,
    val observacoes: String? = null,
)

@Serializable
data class UpdateManutencaoRequest(
    val motoId: String? = null,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaCobrancaId: String? = null,
    val tipo: ManutencaoTipo? = null,
    val status: ManutencaoStatus? = null,
    val descricao: String? = null,
    val kmRegistrado: Int? = null,
    val oficina: String? = null,
    val responsavel: String? = null,
    val custoPecas: Double? = null,
    val custoMaoObra: Double? = null,
    val custoGarantia: Double? = null,
    val custoCliente: Double? = null,
    val observacoes: String? = null,
)

@Serializable
data class UpdateManutencaoStatusRequest(
    val status: ManutencaoStatus,
    val observacao: String? = null,
)

@Serializable
data class ManutencaoItemResponse(
    val id: String,
    val manutencaoId: String,
    val tipo: ManutencaoItemTipo,
    val descricao: String,
    val quantidade: String = "1",
    val valorUnitario: String = "0",
    val descontoPercentual: String = "0",
    val total: String? = null,
    val cobranca: ManutencaoItemCobranca,
    val observacao: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class ManutencaoStatusHistoricoResponse(
    val id: String,
    val manutencaoId: String,
    val statusAnterior: ManutencaoStatus? = null,
    val statusNovo: ManutencaoStatus,
    val responsavel: String? = null,
    val responsavelId: String? = null,
    val observacao: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class ManutencaoAgendamentoResponse(
    val id: String,
    val motoId: String,
    val tipo: ManutencaoTipo,
    val descricao: String,
    val dataPrevista: String? = null,
    val kmPrevisto: Int? = null,
    val intervaloDias: Int? = null,
    val intervaloKm: Int? = null,
    val alertaDiasAntes: Int = 7,
    val status: AgendamentoStatus,
    val manutencaoId: String? = null,
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ManutencaoResponse(
    val id: String,
    val numeroOs: Int,
    val motoId: String,
    val clienteId: String? = null,
    val contratoId: String? = null,
    val parcelaCobrancaId: String? = null,
    val tipo: ManutencaoTipo,
    val status: ManutencaoStatus,
    val descricao: String,
    val dataEntrada: String? = null,
    val dataSaida: String? = null,
    val kmRegistrado: Int? = null,
    val oficina: String? = null,
    val responsavel: String? = null,
    val custoPecas: String = "0",
    val custoMaoObra: String = "0",
    val custoGarantia: String = "0",
    val custoCliente: String = "0",
    val custoTotal: String? = null,
    val cobrancaGeradaEm: String? = null,
    val assinaturaClienteEm: String? = null,
    val assinaturaClienteStoragePath: String? = null,
    val assinaturaClienteUrl: String? = null,
    val pdfGeradoEm: String? = null,
    val pdfStoragePath: String? = null,
    val pdfUrl: String? = null,
    val observacoes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val moto: MotoResponse? = null,
    val cliente: ClienteResponse? = null,
    val contrato: ContratoResponse? = null,
    val itens: List<ManutencaoItemResponse> = emptyList(),
    val historicoStatus: List<ManutencaoStatusHistoricoResponse> = emptyList(),
    val agendamentos: List<ManutencaoAgendamentoResponse> = emptyList(),
)

@Serializable
data class RelatorioPeriodoQuery(
    val from: String? = null,
    val to: String? = null,
)

@Serializable
data class RelatorioSecaoItemResponse(
    val label: String,
    val value: Int,
)

@Serializable
data class RelatorioResumoResponse(
    val usuarios: RelatorioResumoUsuariosResponse,
    val clientes: RelatorioResumoClientesResponse,
    val motos: RelatorioResumoMotosResponse,
    val contratos: RelatorioResumoContratosResponse,
    val financeiro: RelatorioResumoFinanceiroResponse,
    val manutencoes: RelatorioResumoManutencoesResponse,
    val tickets: RelatorioResumoTicketsResponse,
)

@Serializable
data class RelatorioResumoUsuariosResponse(
    val total: Int,
    val ativos: Int,
    val administradores: Int,
)

@Serializable
data class RelatorioResumoClientesResponse(
    val total: Int,
    val pendentes: Int,
    val aprovados: Int,
    val aprovadosComRessalva: Int,
    val reprovados: Int,
)

@Serializable
data class RelatorioResumoMotosResponse(
    val total: Int,
    val disponiveis: Int,
    val alugadas: Int,
    val bloqueadas: Int,
    val manutencao: Int,
)

@Serializable
data class RelatorioResumoContratosResponse(
    val total: Int,
    val ativos: Int,
    val encerrados: Int,
    val inadimplentes: Int,
    val cancelados: Int,
)

@Serializable
data class RelatorioResumoFinanceiroResponse(
    val pagamentosConfirmados: Int,
    val despesasEmAberto: Int,
    val totalRecebido: Int,
    val totalDespesas: Int,
    val saldoEstimado: Int,
)

@Serializable
data class RelatorioResumoManutencoesResponse(
    val total: Int,
    val abertas: Int,
    val emAndamento: Int,
    val concluidas: Int,
    val canceladas: Int,
)

@Serializable
data class RelatorioResumoTicketsResponse(
    val total: Int,
    val abertos: Int,
    val emAnalise: Int,
    val aguardandoCliente: Int,
    val resolvidos: Int,
    val cancelados: Int,
)
