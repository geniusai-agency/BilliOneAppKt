package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.R

internal object CustomerDashboardPreviewData {
	val contract = CustomerContract(
		id = "contract-1",
		planName = "Billi One Conquista",
		motoName = "Sport 160",
		motoSubtitle = "4 anos, pagamento da linha",
		mileage = "2.140 km rodados",
		plate = "ABC1D23",
		monthlyValue = "R$ 660,00",
		nextDue = "05 Jul",
		nextDueSubtitle = "Vence em 05/07/2026",
		contractStatus = "ATIVO",
		contractCode = "CTR-2026-001",
		startDate = "10/01/2026",
		endDate = "10/01/2030",
		caution = "R$ 1.500,00",
		dueDay = "05",
		imageRes = R.drawable.moto_sport_updated,
	)

	val secondContract = CustomerContract(
		id = "contract-2",
		planName = "Billi One Flex",
		motoName = "Moto Elétrica",
		motoSubtitle = "Plano alternativo com entrada reduzida",
		mileage = "860 km rodados",
		plate = "XYZ9K10",
		monthlyValue = "R$ 490,00",
		nextDue = "12 Jul",
		nextDueSubtitle = "Vence em 12/07/2026",
		contractStatus = "ATIVO",
		contractCode = "CTR-2026-002",
		startDate = "20/02/2026",
		endDate = "20/02/2030",
		caution = "R$ 900,00",
		dueDay = "12",
		imageRes = R.drawable.moto_eletrica_new,
	)

	val dashboardData = CustomerDashboardData(
		contracts = listOf(contract, secondContract),
		quickActions = listOf(
			CustomerAction("trending-up", "Compra final da moto", accent = true, tabTarget = CustomerDashboardTab.CONTRATO),
			CustomerAction("trending-up", "Trocar de moto"),
			CustomerAction("document", "Meus contratos", tabTarget = CustomerDashboardTab.CONTRATO),
			CustomerAction("headset", "Central de ajuda", tabTarget = CustomerDashboardTab.SUPORTE),
		),
		summaryChips = listOf(
			CustomerSummaryChip("Contrato", "2 ativos"),
			CustomerSummaryChip("Pagamentos", "9 quitados"),
			CustomerSummaryChip("Em aberto", "3 pendentes"),
		),
		payments = listOf(
			CustomerPayment("Junho", "R$ 660,00", CustomerPaymentStatus.PAID, "05/06/2026", "Quitado"),
			CustomerPayment("Julho", "R$ 660,00", CustomerPaymentStatus.PENDING, "05/07/2026", "Aguardando pagamento"),
			CustomerPayment("Agosto", "R$ 660,00", CustomerPaymentStatus.OVERDUE, "05/08/2026", "Em atraso"),
		),
		documents = listOf(
			"Contrato principal",
			"Termos de uso",
			"Comprovantes de pagamento",
			"Documentos da moto",
		),
		supportMenu = listOf(
			CustomerSupportMenuItem("chat", "Chat rápido", CustomerSupportView.CHAT, accent = true),
			CustomerSupportMenuItem("help", "Perguntas frequentes", CustomerSupportView.FAQ),
			CustomerSupportMenuItem("mail", "Contato direto", CustomerSupportView.CONTACT),
		),
		faqs = listOf(
			CustomerFaqItem("Como vejo meu vencimento?", "Abra o resumo ou a aba pagamentos."),
			CustomerFaqItem("Como falo com suporte?", "Use o chat rápido ou o formulário."),
		),
		supportContacts = listOf(
			CustomerContactLine("whatsapp", "WhatsApp", "(71) 99999-0000"),
			CustomerContactLine("email", "E-mail", "suporte@billione.com"),
		),
		upgradeBanner = CustomerUpgradeBanner(
			label = "Upgrade",
			title = "Troque para um plano superior",
			description = "Veja opções com parcelas menores ou motos mais completas.",
			buttonText = "Quero trocar",
		),
		chatWelcome = "Olá! Posso ajudar com contrato, pagamento ou suporte.",
	)

	val chatMessages = listOf(
		CustomerChatMessage("bot-1", CustomerChatRole.BOT, "Olá! Como posso ajudar?"),
		CustomerChatMessage("user-2", CustomerChatRole.USER, "Quero ver meu contrato."),
	)

	val contactForm = CustomerContactFormState(
		name = "Rafael Souza",
		email = "rafael@billione.com",
		message = "Quero tirar uma dúvida sobre o contrato.",
	)
}
