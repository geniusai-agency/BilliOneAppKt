package com.example.billionemotosappkt.screens.dashboard.customer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.dashboard.customer.components.CustomerDashboardContent
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerDashboardScreen(
	authState: AuthUiState,
	dashboardData: CustomerDashboardData,
	displayUserName: String? = null,
	displayUserEmail: String? = null,
	displayAvatarUrl: String? = null,
	showPlanAlert: Boolean = false,
	recommendedPlanName: String? = null,
	recommendedPlanValue: String? = null,
	onPlanosClick: () -> Unit = {},
	onEditProfile: () -> Unit = {},
	onRequestAnalysis: () -> Unit = {},
	onLogout: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var activeTab by rememberSaveable { androidx.compose.runtime.mutableStateOf(CustomerDashboardTab.RESUMO) }
	var supportView by rememberSaveable { androidx.compose.runtime.mutableStateOf(CustomerSupportView.MENU) }
	var selectedContractId by rememberSaveable { androidx.compose.runtime.mutableStateOf(dashboardData.contracts.firstOrNull()?.id.orEmpty()) }
	var openFaqIndex by rememberSaveable { androidx.compose.runtime.mutableStateOf<Int?>(null) }
	var chatInput by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var contactName by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var contactEmail by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var contactMessage by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
	var contactSent by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
	val chatMessages = remember {
		androidx.compose.runtime.mutableStateOf(
			listOf(
				CustomerChatMessage(
					id = "bot-1",
					role = CustomerChatRole.BOT,
					text = dashboardData.chatWelcome,
				),
			),
		)
	}
	
	Box(modifier = modifier.fillMaxSize()) {
		CustomerDashboardContent(
			authState = authState.copy(
				userName = displayUserName ?: authState.userName,
				userEmail = displayUserEmail ?: authState.userEmail,
				avatarUrl = displayAvatarUrl ?: authState.avatarUrl,
			),
			dashboardData = dashboardData,
			showPlanAlert = showPlanAlert,
			recommendedPlanName = recommendedPlanName,
			recommendedPlanValue = recommendedPlanValue,
			onPlanosClick = onPlanosClick,
			activeTab = activeTab,
			supportView = supportView,
			selectedContractId = selectedContractId,
			openFaqIndex = openFaqIndex,
			chatMessages = chatMessages.value,
			chatInput = chatInput,
			contactForm = CustomerContactFormState(
				name = contactName,
				email = contactEmail,
				message = contactMessage,
			),
			contactSent = contactSent,
			onTabChange = { tab ->
				activeTab = tab
				if (tab != CustomerDashboardTab.SUPORTE) {
					supportView = CustomerSupportView.MENU
				}
			},
			onSupportViewChange = { view -> supportView = view },
			onSelectedContractChange = { contractId -> selectedContractId = contractId },
			onFaqToggle = { faqIndex -> openFaqIndex = faqIndex },
			onChatInputChange = { chatInput = it },
			onSendMessage = {
				val message = chatInput.trim()
				if (message.isNotBlank()) {
					chatMessages.value = chatMessages.value + listOf(
						CustomerChatMessage(
							id = "user-${System.currentTimeMillis()}",
							role = CustomerChatRole.USER,
							text = message,
						),
						CustomerChatMessage(
							id = "bot-${System.currentTimeMillis() + 1}",
							role = CustomerChatRole.BOT,
							text = replyFor(message),
						),
					)
					chatInput = ""
				}
			},
			onContactFormChange = {
				contactName = it.name
				contactEmail = it.email
				contactMessage = it.message
			},
			onContactSubmit = {
				if (contactName.isNotBlank() && contactEmail.isNotBlank() && contactMessage.isNotBlank()) {
					contactSent = true
					contactName = ""
					contactEmail = ""
					contactMessage = ""
				}
			},
			onContactDismiss = {
				contactSent = false
				supportView = CustomerSupportView.MENU
			},
			onEditProfile = onEditProfile,
			onRequestAnalysis = onRequestAnalysis,
			onLogout = onLogout,
		)
	}
}

private fun replyFor(message: String): String {
	val lower = message.lowercase()
	return when {
		lower.contains("contrato") -> "Na aba Contrato voce encontra os dados principais e pode alternar entre vinculos."
		lower.contains("pag") || lower.contains("boleto") -> "Na aba Pagamentos voce ve as parcelas, status e vencimentos."
		lower.contains("moto") -> "O contrato mostra a moto vinculada e os dados principais do veiculo."
		else -> "Entendi. Vou encaminhar sua duvida para a equipe."
	}
}

@Preview(showBackground = true, widthDp = 420, heightDp = 920)
@Composable
fun CustomerDashboardScreenPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerDashboardScreen(
			authState = AuthUiState(
				isAuthenticated = true,
				userName = "Rafael Souza",
				userEmail = "rafael@billione.com",
			),
			dashboardData = CustomerDashboardPreviewData.dashboardData,
			displayUserName = "Rafael Souza",
			displayUserEmail = "rafael@billione.com",
			showPlanAlert = true,
			recommendedPlanName = "Billione Conquista",
			recommendedPlanValue = "R$ 660,00",
			onEditProfile = {},
			onRequestAnalysis = {},
			onLogout = {},
		)
	}
}
