package com.example.billionemotosappkt.screens.dashboard.customer

fun demoCustomerDashboardData(): CustomerDashboardData {
	return CustomerDashboardData(
		contracts = listOf(
			CustomerContract(
				id = "contrato-ativo",
				planName = "Billione Conquiste",
				motoName = "Sport 160",
				motoSubtitle = "A mais procurada da linha",
				mileage = "12.480 km rodados",
				plate = "BIL-2A60",
				monthlyValue = "R$ 660,00",
				nextDue = "05 Jul",
				nextDueSubtitle = "Vencimento em 05/07/2026",
				contractStatus = "ATIVO",
				contractCode = "CTR-2026-0481",
				startDate = "12/02/2026",
				endDate = "12/02/2027",
				caution = "R$ 1.320,00",
				dueDay = "Dia 5",
				imageRes = "moto_sport_updated.png",
			),
			CustomerContract(
				id = "contrato-esd",
				planName = "Billione Start",
				motoName = "Sport ESD",
				motoSubtitle = "Visual urbano com freio a disco",
				mileage = "8.210 km rodados",
				plate = "BIL-5K11",
				monthlyValue = "R$ 620,00",
				nextDue = "14 Jul",
				nextDueSubtitle = "Vencimento em 14/07/2026",
				contractStatus = "ATIVO",
				contractCode = "CTR-2025-9921",
				startDate = "21/09/2025",
				endDate = "21/09/2026",
				caution = "R$ 1.240,00",
				dueDay = "Dia 14",
				imageRes = "avelloz_160_black_new.png",
			),
		),
		quickActions = listOf(
			CustomerAction(icon = "credit-card", label = "Pagar mensalidade", accent = true, tabTarget = CustomerDashboardTab.PAGAMENTOS),
			CustomerAction(icon = "document", label = "2a via boleto", tabTarget = CustomerDashboardTab.PAGAMENTOS),
			CustomerAction(icon = "headset", label = "Suporte", tabTarget = CustomerDashboardTab.SUPORTE),
			CustomerAction(icon = "trending-up", label = "Upgrade de plano", tabTarget = CustomerDashboardTab.SUPORTE),
		),
		summaryChips = listOf(
			CustomerSummaryChip("Proximo vencimento", "05/07"),
			CustomerSummaryChip("Plano atual", "Billione Conquiste"),
			CustomerSummaryChip("Moto", "Sport 160"),
			CustomerSummaryChip("Parcelas", "12"),
		),
		payments = listOf(
			CustomerPayment("Jul 2026", "R$ 660,00", CustomerPaymentStatus.PENDING, "05/07/2026", "Parcela do contrato"),
			CustomerPayment("Jun 2026", "R$ 660,00", CustomerPaymentStatus.PAID, "05/06/2026", "Boleto pago no prazo"),
			CustomerPayment("Mai 2026", "R$ 660,00", CustomerPaymentStatus.PAID, "05/05/2026", "Boleto pago no prazo"),
			CustomerPayment("Abr 2026", "R$ 660,00", CustomerPaymentStatus.OVERDUE, "05/04/2026", "Em atraso"),
		),
		documents = listOf(
			"Contrato principal",
			"Termos de uso",
			"Comprovantes de pagamento",
			"Documentos da moto",
		),
		supportMenu = listOf(
			CustomerSupportMenuItem(icon = "message", label = "Chat com assistente", view = CustomerSupportView.CHAT, accent = true),
			CustomerSupportMenuItem(icon = "help", label = "Perguntas frequentes", view = CustomerSupportView.FAQ),
			CustomerSupportMenuItem(icon = "mail", label = "Formulario de contato", view = CustomerSupportView.CONTACT),
		),
		faqs = listOf(
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
		),
		supportContacts = listOf(
			CustomerContactLine(icon = "phone", label = "Telefone", value = "(75) 4000-0000"),
			CustomerContactLine(icon = "mail", label = "E-mail", value = "suporte@billione.com"),
			CustomerContactLine(icon = "map", label = "Unidade", value = "Feira de Santana, BA"),
		),
		upgradeBanner = CustomerUpgradeBanner(
			label = "Upgrade disponivel",
			title = "Migre para o Billione Conquiste e a moto pode ser sua.",
			description = "A partir de R$ 660/mes com opcao de compra no final do contrato.",
			buttonText = "Conhecer plano",
		),
		chatWelcome = "Ola! Eu posso ajudar com contrato, parcelas e suporte.",
	)
}
