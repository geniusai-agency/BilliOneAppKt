package com.example.billionemotosappkt.screens.dashboard.customer

import androidx.annotation.DrawableRes

enum class CustomerDashboardTab {
	RESUMO,
	PAGAMENTOS,
	CONTRATO,
	SUPORTE,
}

enum class CustomerSupportView {
	MENU,
	CHAT,
	FAQ,
	CONTACT,
}

enum class CustomerPaymentStatus {
	PAID,
	PENDING,
	OVERDUE,
}

enum class CustomerChatRole {
	USER,
	BOT,
}

data class CustomerDashboardData(
	val contracts: List<CustomerContract>,
	val quickActions: List<CustomerAction>,
	val summaryChips: List<CustomerSummaryChip>,
	val payments: List<CustomerPayment>,
	val documents: List<String>,
	val supportMenu: List<CustomerSupportMenuItem>,
	val faqs: List<CustomerFaqItem>,
	val supportContacts: List<CustomerContactLine>,
	val upgradeBanner: CustomerUpgradeBanner,
	val chatWelcome: String,
)

data class CustomerContract(
	val id: String,
	val planName: String,
	val motoName: String,
	val motoSubtitle: String,
	val mileage: String,
	val plate: String,
	val monthlyValue: String,
	val nextDue: String,
	val nextDueSubtitle: String,
	val contractStatus: String,
	val contractCode: String,
	val startDate: String,
	val endDate: String,
	val caution: String,
	val dueDay: String,
	@param:DrawableRes val imageRes: Int,
)

data class CustomerAction(
	val icon: String,
	val label: String,
	val accent: Boolean = false,
	val tabTarget: CustomerDashboardTab? = null,
)

data class CustomerSummaryChip(
	val label: String,
	val value: String,
)

data class CustomerPayment(
	val month: String,
	val value: String,
	val status: CustomerPaymentStatus,
	val dueDate: String,
	val subtitle: String,
)

data class CustomerSupportMenuItem(
	val icon: String,
	val label: String,
	val view: CustomerSupportView,
	val accent: Boolean = false,
)

data class CustomerFaqItem(
	val question: String,
	val answer: String,
)

data class CustomerContactLine(
	val icon: String,
	val label: String,
	val value: String,
)

data class CustomerUpgradeBanner(
	val label: String,
	val title: String,
	val description: String,
	val buttonText: String,
)

data class CustomerChatMessage(
	val id: String,
	val role: CustomerChatRole,
	val text: String,
)

data class CustomerContactFormState(
	val name: String = "",
	val email: String = "",
	val message: String = "",
)
