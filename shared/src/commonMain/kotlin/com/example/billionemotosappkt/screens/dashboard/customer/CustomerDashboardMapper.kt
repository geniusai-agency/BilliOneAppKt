package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.data.dashboard.DashboardOverviewData
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.ClienteAprovacaoStatus
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.ParcelaResponse
import com.example.billionemotosappkt.shared.api.PagamentoResponse
import com.example.billionemotosappkt.shared.api.PagamentoStatus
import com.example.billionemotosappkt.shared.api.RelatorioResumoResponse
import com.example.billionemotosappkt.shared.utils.formatBrDate
import com.example.billionemotosappkt.shared.utils.formatDayMonth

fun CustomerDashboardSnapshot.toCustomerDashboardData(authState: AuthUiState): CustomerDashboardData {
	val contracts = contratos.map { it.toCustomerContract() }
	val contractParcelas = contratos.flatMap { contrato ->
		contrato.parcelas.map { parcela ->
			parcela.toCustomerPayment(contrato.id)
		}
	}

	val payments = if (contractParcelas.isNotEmpty()) {
		contractParcelas.sortedByDescending { parseApiDate(it.dueDate) ?: 0L }
	} else if (pagamentos.isNotEmpty()) {
		pagamentos.map { it.toCustomerPayment() }
	} else {
		emptyList()
	}

	val activeCount = contracts.count { it.contractStatus.equals("ATIVO", ignoreCase = true) }
	val overdueCount = payments.count { it.status == CustomerPaymentStatus.OVERDUE }
	val firstPlan = planos.firstOrNull()

	return CustomerDashboardData(
		contracts = contracts,
		quickActions = defaultQuickActions(),
		summaryChips = listOf(
			CustomerSummaryChip("Contratos", contracts.size.toString()),
			CustomerSummaryChip("Ativos", activeCount.toString()),
			CustomerSummaryChip("Atrasos", overdueCount.toString()),
			CustomerSummaryChip("Modelos", modelos.size.toString()),
		),
		payments = payments,
		documents = defaultDocuments(authState),
		supportMenu = defaultSupportMenu(),
		faqs = defaultFaqs(),
		supportContacts = defaultSupportContacts(),
		upgradeBanner = defaultUpgradeBanner(contracts.firstOrNull(), firstPlan?.nome),
		chatWelcome = dashboardGreeting(authState),
	)
}

fun DashboardOverviewData.toCustomerDashboardData(authState: AuthUiState): CustomerDashboardData {
	val contracts = contratosVencendo.map { it.toCustomerContract() }
	val payments = parcelasFromContracts(contratosVencendo)
		.ifEmpty { pagamentosRecentes.map { it.toCustomerPayment() } }

	val summaryChips = resumo.toSummaryChips(
		openCount = payments.count { it.status != CustomerPaymentStatus.PAID },
	)

	return CustomerDashboardData(
		contracts = contracts,
		quickActions = defaultQuickActions(),
		summaryChips = summaryChips,
		payments = payments,
		documents = defaultDocuments(authState),
		supportMenu = defaultSupportMenu(),
		faqs = defaultFaqs(),
		supportContacts = defaultSupportContacts(),
		upgradeBanner = defaultUpgradeBanner(contracts.firstOrNull(), null),
		chatWelcome = dashboardGreeting(authState),
	)
}

private fun RelatorioResumoResponse.toSummaryChips(
	openCount: Int,
): List<CustomerSummaryChip> {
	return listOf(
		CustomerSummaryChip("Contratos ativos", contratos.ativos.toString()),
		CustomerSummaryChip("Pagamentos confirmados", financeiro.pagamentosConfirmados.toString()),
		CustomerSummaryChip("Em aberto", openCount.toString()),
		CustomerSummaryChip("Motos disponíveis", motos.disponiveis.toString()),
	)
}

private fun parcelasFromContracts(contracts: List<ContratoResponse>): List<CustomerPayment> {
	return contracts
		.flatMap { contrato ->
			contrato.parcelas.map { parcela ->
				parcela.toCustomerPayment(contrato.id)
			}
		}
		.sortedByDescending { parseApiDate(it.dueDate) ?: 0L }
}

private fun ContratoResponse.toCustomerContract(): CustomerContract {
	val motoName = moto?.modelo ?: planoSnapshotNome.ifBlank { "Moto" }
	return CustomerContract(
		id = id,
		planName = plano?.nome ?: planoSnapshotNome,
		motoName = motoName,
		motoSubtitle = when {
			moto?.marca.isNullOrBlank().not() && moto?.ano != null -> "${moto?.marca} ${moto?.ano}"
			moto?.marca.isNullOrBlank().not() -> moto?.marca.orEmpty()
			else -> "Contrato ${status.name.lowercase().replaceFirstChar { it.uppercaseChar() }}"
		},
		mileage = moto?.kmAtual?.takeIf { it > 0 }?.let { "$it km rodados" } ?: "Quilometragem indisponivel",
		plate = moto?.placa ?: "Sem placa",
		monthlyValue = formatCurrency(valorParcela),
		nextDue = nextDueLabel(),
		nextDueSubtitle = nextDueSubtitle(),
		contractStatus = status.name,
		contractCode = "CTR-${id.takeLast(6).uppercase()}",
		startDate = formatApiDate(dataInicio),
		endDate = formatApiDate(dataFim),
		caution = formatCurrency(valorCaucao),
		dueDay = diaVencimento?.let { "Dia $it" } ?: "Dia nao informado",
		imageRes = moto?.imageRes() ?: "moto_premium.png",
	)
}

private fun ContratoResponse.nextDueLabel(): String {
	val parcelaEmAberto = parcelas.firstOrNull { it.status != com.example.billionemotosappkt.shared.api.ParcelaStatus.PAGO }
	return parcelaEmAberto?.dataVencimento?.let { formatShortDate(it) }
		?: formatShortDate(dataFim)
		?: "Sem vencimento"
}

private fun ContratoResponse.nextDueSubtitle(): String {
	val parcelaEmAberto = parcelas.firstOrNull { it.status != com.example.billionemotosappkt.shared.api.ParcelaStatus.PAGO }
	return parcelaEmAberto?.dataVencimento?.let { "Vencimento em ${formatApiDate(it)}" }
		?: dataFim?.let { "Vigência até ${formatApiDate(it)}" }
		?: "Sem próxima data"
}

private fun ParcelaResponse.toCustomerPayment(contractId: String): CustomerPayment {
	return CustomerPayment(
		month = formatShortDate(dataVencimento) ?: "Parcela $numero",
		value = formatCurrency(valor),
		status = when (status) {
			com.example.billionemotosappkt.shared.api.ParcelaStatus.PAGO -> CustomerPaymentStatus.PAID
			com.example.billionemotosappkt.shared.api.ParcelaStatus.ATRASADO -> CustomerPaymentStatus.OVERDUE
			else -> CustomerPaymentStatus.PENDING
		},
		dueDate = formatApiDate(dataVencimento),
		subtitle = observacoes ?: "Contrato $contractId",
	)
}

private fun PagamentoResponse.toCustomerPayment(): CustomerPayment {
	return CustomerPayment(
		month = formatShortDate(dataPagamento) ?: "Pagamento",
		value = formatCurrency(valor),
		status = when (status) {
			PagamentoStatus.CONFIRMADO -> CustomerPaymentStatus.PAID
			PagamentoStatus.CANCELADO, PagamentoStatus.ESTORNADO -> CustomerPaymentStatus.OVERDUE
			PagamentoStatus.PENDENTE -> CustomerPaymentStatus.PENDING
		},
		dueDate = formatApiDate(dataPagamento),
		subtitle = formaPagamento,
	)
}

private fun MotoResponse.imageRes(): String {
	val candidate = listOfNotNull(modelo, marca, observacoes).joinToString(" ").lowercase()
	return when {
		candidate.contains("eletr") -> "moto_eletrica_new.png"
		candidate.contains("sport") -> "moto_sport_updated.png"
		candidate.contains("premium") -> "moto_premium.png"
		candidate.contains("avelloz") -> "avelloz_160_black_new.png"
		candidate.contains("detail") -> "moto_detail_engine.png"
		else -> "moto_premium.png"
	}
}

private fun defaultQuickActions(): List<CustomerAction> {
	return listOf(
		CustomerAction(icon = "credit-card", label = "Pagar mensalidade", accent = true, tabTarget = CustomerDashboardTab.PAGAMENTOS),
		CustomerAction(icon = "document", label = "2a via boleto", tabTarget = CustomerDashboardTab.PAGAMENTOS),
		CustomerAction(icon = "headset", label = "Suporte", tabTarget = CustomerDashboardTab.SUPORTE),
		CustomerAction(icon = "trending-up", label = "Upgrade de plano", tabTarget = CustomerDashboardTab.CONTRATO),
	)
}

private fun defaultDocuments(authState: AuthUiState): List<String> {
	return listOfNotNull(
		"Contrato principal",
		if (authState.clienteStatus == ClienteAprovacaoStatus.EM_ANALISE || authState.journeyScreen == "analysis-pending") "Cadastro em análise" else "Status do cadastro",
		"Comprovantes de pagamento",
		"Documentos da moto",
	)
}

private fun defaultSupportMenu(): List<CustomerSupportMenuItem> {
	return listOf(
		CustomerSupportMenuItem(icon = "build", label = "Meus chamados", view = CustomerSupportView.TICKETS, accent = true),
		CustomerSupportMenuItem(icon = "message", label = "Chat com assistente", view = CustomerSupportView.CHAT),
		CustomerSupportMenuItem(icon = "help", label = "Perguntas frequentes", view = CustomerSupportView.FAQ),
		CustomerSupportMenuItem(icon = "mail", label = "Formulario de contato", view = CustomerSupportView.CONTACT),
	)
}

private fun defaultFaqs(): List<CustomerFaqItem> {
	return listOf(
		CustomerFaqItem(
			question = "Como acompanho meu contrato?",
			answer = "Abra a aba Contrato para ver vigencia, plano, moto e alternar entre vinculos.",
		),
		CustomerFaqItem(
			question = "Onde vejo minhas parcelas?",
			answer = "Na aba Pagamentos voce encontra as parcelas, vencimentos e status de cada boleto.",
		),
		CustomerFaqItem(
			question = "Como falar com suporte?",
			answer = "Use o chat rapido ou o formulario de contato nesta mesma aba.",
		),
		CustomerFaqItem(
			question = "Posso antecipar parcelas?",
			answer = "Sim. A equipe pode orientar antecipacao, segunda via e demais ajustes do contrato.",
		),
	)
}

private fun defaultSupportContacts(): List<CustomerContactLine> {
	return listOf(
		CustomerContactLine(icon = "phone", label = "Telefone", value = "(75) 4000-0000"),
		CustomerContactLine(icon = "mail", label = "E-mail", value = "suporte@billione.com"),
		CustomerContactLine(icon = "map", label = "Unidade", value = "Feira de Santana, BA"),
	)
}

private fun defaultUpgradeBanner(contract: CustomerContract?, planNameOverride: String?): CustomerUpgradeBanner {
	val planName = planNameOverride ?: contract?.planName ?: "Billione Conquista"
	return CustomerUpgradeBanner(
		label = "Upgrade disponivel",
		title = "Migre para um plano superior e mantenha seu ritmo com ${planName}.",
		description = "Veja opções com parcelas diferentes e possibilidades de compra no final do contrato.",
		buttonText = "Conhecer plano",
	)
}

private fun dashboardGreeting(authState: AuthUiState): String {
	val name = authState.userName?.takeIf { it.isNotBlank() } ?: "cliente"
	return when (authState.journeyScreen) {
		"analysis-pending" -> "Olá, $name. Sua solicitação está em análise e a equipe ainda não liberou o contrato."
		"analysis-rejected" -> "Olá, $name. Você pode revisar os dados e reenviar sua solicitação quando estiver liberado."
		"contract-required" -> "Olá, $name. Falta concluir a etapa de contrato para liberar a conta."
		"contract-signature" -> "Olá, $name. Seu contrato está pronto para assinatura."
		else -> "Olá, $name! Posso ajudar com contrato, pagamento, perfil e suporte."
	}
}

private fun formatApiDate(value: String?): String {
	if (value.isNullOrBlank()) return "Indisponivel"
	return formatBrDate(value) ?: value
}

private fun formatShortDate(value: String?): String? = formatDayMonth(value)

private fun parseApiDate(value: String?): Long? {
	if (value.isNullOrBlank()) return null
	val parts = value.take(10).split('-')
	if (parts.size != 3) return null
	val year = parts[0].toIntOrNull() ?: return null
	val month = parts[1].toIntOrNull() ?: return null
	val day = parts[2].toIntOrNull() ?: return null
	return year * 10000L + month * 100L + day
}
