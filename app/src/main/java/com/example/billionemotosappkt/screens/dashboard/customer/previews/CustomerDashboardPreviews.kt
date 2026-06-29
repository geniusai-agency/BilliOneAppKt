package com.example.billionemotosappkt.screens.dashboard.customer

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerContractTab
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardBottomNav
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardContent
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardHeader
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardHomeTab
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardTabs
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerFaqAccordion
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerMetricCards
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerMotoCard
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerPaymentsTab
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerPlanCard
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerQuickActions
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerSectionHeader
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerSummaryChips
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerSupportTab
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerUpgradeBanner
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

private val previewData = demoCustomerDashboardData()

@Preview(showBackground = true, widthDp = 420, heightDp = 920, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerDashboardContentPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardContent(
			authState = AuthUiState(isAuthenticated = true, userName = "Rafael Souza", userEmail = "rafael@billione.com"),
			dashboardData = previewData,
			showPlanAlert = true,
			onPlanosClick = {},
			activeTab = CustomerDashboardTab.RESUMO,
			supportView = CustomerSupportView.MENU,
			selectedContractId = previewData.contracts.first().id,
			openFaqIndex = null,
			chatMessages = listOf(
				CustomerChatMessage("bot-1", CustomerChatRole.BOT, previewData.chatWelcome),
			),
			chatInput = "",
			contactForm = CustomerContactFormState(),
			contactSent = false,
			onTabChange = {},
			onSupportViewChange = {},
			onSelectedContractChange = {},
			onFaqToggle = {},
			onChatInputChange = {},
			onSendMessage = {},
			onContactFormChange = {},
			onContactSubmit = {},
			onContactDismiss = {},
			onEditProfile = {},
			onRequestAnalysis = {},
			onLogout = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerHeaderPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardHeader(
			userName = "Rafael Souza",
			userEmail = "rafael@billione.com",
			onLogout = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerTabsPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardTabs(
			activeTab = CustomerDashboardTab.RESUMO,
			onTabChange = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerBottomNavPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardBottomNav(
			activeTab = CustomerDashboardTab.PAGAMENTOS,
			onTabChange = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerHomePreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardHomeTab(
			contract = previewData.contracts.first(),
			data = previewData,
			openCount = 3,
			paidCount = 9,
			activeTab = CustomerDashboardTab.RESUMO,
			onTabChange = {},
			onActionClick = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerPaymentsPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerPaymentsTab(payments = previewData.payments)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerContractPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerContractTab(
			selectedContract = previewData.contracts.first(),
			contracts = previewData.contracts,
			documents = previewData.documents,
			onSelectContract = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSupportMenuPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSupportTab(
			view = CustomerSupportView.MENU,
			onViewChange = {},
			openFaqIndex = null,
			onFaqToggle = {},
			chatMessages = emptyList(),
			chatInput = "",
			onChatInputChange = {},
			onSendMessage = {},
			contactForm = CustomerContactFormState(),
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = previewData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSupportChatPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSupportTab(
			view = CustomerSupportView.CHAT,
			onViewChange = {},
			openFaqIndex = null,
			onFaqToggle = {},
			chatMessages = listOf(
				CustomerChatMessage("bot-1", CustomerChatRole.BOT, previewData.chatWelcome),
				CustomerChatMessage("user-2", CustomerChatRole.USER, "Quero ver meu contrato."),
			),
			chatInput = "Preciso de ajuda",
			onChatInputChange = {},
			onSendMessage = {},
			contactForm = CustomerContactFormState(),
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = previewData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSupportFaqPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSupportTab(
			view = CustomerSupportView.FAQ,
			onViewChange = {},
			openFaqIndex = 0,
			onFaqToggle = {},
			chatMessages = emptyList(),
			chatInput = "",
			onChatInputChange = {},
			onSendMessage = {},
			contactForm = CustomerContactFormState(),
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = previewData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSupportContactPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSupportTab(
			view = CustomerSupportView.CONTACT,
			onViewChange = {},
			openFaqIndex = null,
			onFaqToggle = {},
			chatMessages = emptyList(),
			chatInput = "",
			onChatInputChange = {},
			onSendMessage = {},
			contactForm = CustomerContactFormState(
				name = "Rafael Souza",
				email = "rafael@billione.com",
				message = "Quero tirar uma duvida sobre o contrato.",
			),
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = previewData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerPlanCardPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerPlanCard(contract = previewData.contracts.first())
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerMetricCardsPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerMetricCards(contractCount = 1, paidCount = 9, openCount = 3)
	}
}

@Preview(showBackground = true, widthDp = 840, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerMetricCardsWidePreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerMetricCards(contractCount = 1, paidCount = 9, openCount = 3)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerQuickActionsPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerQuickActions(
			actions = previewData.quickActions,
			onActionClick = {},
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSummaryChipsPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSummaryChips(chips = previewData.summaryChips)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerMotoCardPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerMotoCard(contract = previewData.contracts.first())
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerUpgradeBannerPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerUpgradeBanner(banner = previewData.upgradeBanner)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerSectionHeaderPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSectionHeader(
			eyebrow = "BILLI ONE",
			title = "Dashboard completa",
			description = "Blocos separados com preview individual.",
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
fun CustomerFaqAccordionPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerFaqAccordion(
			items = previewData.faqs,
			openIndex = 0,
			onToggle = {},
		)
	}
}
