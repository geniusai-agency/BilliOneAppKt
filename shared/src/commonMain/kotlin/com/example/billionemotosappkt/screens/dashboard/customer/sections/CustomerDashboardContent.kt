package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerChatMessage
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContactFormState
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardTab
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerSupportView
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerDashboardContent(
	authState: AuthUiState,
	dashboardData: CustomerDashboardData,
	showPlanAlert: Boolean,
	recommendedPlanName: String? = null,
	recommendedPlanValue: String? = null,
	onPlanosClick: () -> Unit,
	activeTab: CustomerDashboardTab,
	supportView: CustomerSupportView,
	selectedContractId: String,
	openFaqIndex: Int?,
	chatMessages: List<CustomerChatMessage>,
	chatInput: String,
	contactForm: CustomerContactFormState,
	contactSent: Boolean,
	onTabChange: (CustomerDashboardTab) -> Unit,
	onBack: () -> Unit,
	onSupportViewChange: (CustomerSupportView) -> Unit,
	onSelectedContractChange: (String) -> Unit,
	onFaqToggle: (Int?) -> Unit,
	onChatInputChange: (String) -> Unit,
	onSendMessage: () -> Unit,
	onContactFormChange: (CustomerContactFormState) -> Unit,
	onContactSubmit: () -> Unit,
	onContactDismiss: () -> Unit,
	onEditProfile: () -> Unit,
	onRequestAnalysis: () -> Unit,
	onLogout: () -> Unit,
	api: BillioneMotosApi? = null,
	modifier: Modifier = Modifier,
) {
	val selectedContract = dashboardData.contracts.firstOrNull { it.id == selectedContractId }
		?: dashboardData.contracts.firstOrNull()
	val scrollState = rememberScrollState()
	
	Scaffold(
		modifier = modifier
			.fillMaxSize()
			.safeDrawingPadding(),
		topBar = {
			CustomerDashboardHeader(
				userName = authState.userName ?: "Cliente Billione",
				userEmail = authState.userEmail,
				showBackButton = supportView != CustomerSupportView.MENU,
				onBack = onBack,
				onEditProfile = onEditProfile,
				onLogout = onLogout,
			)
		},
		bottomBar = {
			CustomerDashboardBottomNav(
				activeTab = activeTab,
				onTabChange = onTabChange,
			)
		},
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.background),
			) {
				BackgroundGlow()
			}
			
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(scrollState)
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				if (dashboardData.contracts.isNotEmpty()) {
					CustomerJourneyBanner(
						authState = authState,
						onPrimaryAction = onRequestAnalysis,
					)
				}
				
				when (activeTab) {
					CustomerDashboardTab.RESUMO -> {
						if (dashboardData.contracts.isEmpty()) {
							CustomerNoContractScreen(
								authState = authState,
								dashboardData = dashboardData,
								onPrimaryAction = onRequestAnalysis,
								onPlanosClick = onPlanosClick,
								onSupportClick = { onTabChange(CustomerDashboardTab.SUPORTE) }
							)
						} else {
							CustomerDashboardHomeTab(
								contract = selectedContract,
								data = dashboardData,
								activeTab = activeTab,
								onTabChange = onTabChange,
								openCount = dashboardData.payments.count { it.status != com.example.billionemotosappkt.screens.dashboard.customer.CustomerPaymentStatus.PAID },
								paidCount = dashboardData.payments.count { it.status == com.example.billionemotosappkt.screens.dashboard.customer.CustomerPaymentStatus.PAID },
								onActionClick = { tab ->
									if (tab != null) onTabChange(tab)
								},
							)
						}
					}
					
					CustomerDashboardTab.PAGAMENTOS -> CustomerPaymentsTab(
						payments = dashboardData.payments,
						contractCount = dashboardData.contracts.size,
						onBack = { onTabChange(CustomerDashboardTab.RESUMO) }
					)
					CustomerDashboardTab.CONTRATO -> CustomerContractTab(
						selectedContract = selectedContract,
						contracts = dashboardData.contracts,
						documents = dashboardData.documents,
						onSelectContract = onSelectedContractChange,
						onBack = { onTabChange(CustomerDashboardTab.RESUMO) }
					)
					
					CustomerDashboardTab.SUPORTE -> CustomerSupportTab(
						view = supportView,
						onViewChange = onSupportViewChange,
						openFaqIndex = openFaqIndex,
						onFaqToggle = onFaqToggle,
						chatMessages = chatMessages,
						chatInput = chatInput,
						onChatInputChange = onChatInputChange,
						onSendMessage = onSendMessage,
						contactForm = contactForm,
						onContactFormChange = onContactFormChange,
						contactSent = contactSent,
						onContactSubmit = onContactSubmit,
						onContactDismiss = onContactDismiss,
						onBack = { onTabChange(CustomerDashboardTab.RESUMO) },
						dashboardData = dashboardData,
						api = api,
					)
				}
				
				Spacer(modifier = Modifier.height(24.dp))
			}
		}
	}
}

@Composable
private fun CustomerJourneyBanner(
	authState: AuthUiState,
	onPrimaryAction: () -> Unit,
) {
	// Contrato aprovado e aguardando assinatura tem prioridade e destaque próprio.
	val pendingSignature = authState.contractStatus == ContratoStatus.PENDENTE_ASSINATURA ||
			authState.journeyScreen == "contract-signature"

	if (pendingSignature) {
		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
			),
		) {
			Column(
				modifier = Modifier.padding(16.dp),
			) {
				Text(
					text = authState.journeyTitle ?: "Aguardando assinatura",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onTertiaryContainer,
				)
				Spacer(modifier = Modifier.height(6.dp))
				Text(
					text = authState.journeyMessage
						?: "Seu contrato foi aprovado e está pronto para ser assinado no app.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onTertiaryContainer,
				)
				Spacer(modifier = Modifier.height(12.dp))
				Button(onClick = {}, enabled = false) {
					Text(text = "Assinar contrato (em breve)")
				}
			}
		}
		return
	}

	val pendingAnalysis =
		authState.journeyScreen == "analysis-pending" || authState.journeyState == "WAITING_ANALYSIS"
	val shouldShowBanner = pendingAnalysis ||
			authState.journeyScreen == "analysis-rejected" ||
			authState.journeyScreen == "contract-required"
	val title = when {
		pendingAnalysis -> authState.journeyTitle ?: "Sua solicitação está em análise"
		authState.journeyScreen == "contract-required" -> authState.journeyTitle
			?: "Contrato pendente"
		
		authState.journeyScreen == "contract-signature" -> authState.journeyTitle
			?: "Contrato aguardando assinatura"
		
		authState.journeyScreen == "analysis-rejected" -> authState.journeyTitle
			?: "Análise reprovada"
		
		else -> authState.journeyTitle ?: "Acompanhamento da conta"
	}
	val message = when {
		pendingAnalysis -> authState.journeyMessage
			?: "Seu cadastro está em análise. Assim que o time concluir, a dashboard será atualizada."
		
		authState.journeyScreen == "analysis-rejected" -> authState.journeyMessage
			?: "Você pode ajustar os dados e reenviar a solicitação quando estiver liberado."
		
		authState.journeyScreen == "contract-required" -> authState.journeyMessage
			?: "Complete a etapa de contrato para liberar o uso da conta."
		
		authState.journeyScreen == "contract-signature" -> authState.journeyMessage
			?: "Seu contrato está pronto para assinatura."
		
		else -> authState.journeyMessage
	}
	
	if (!shouldShowBanner) {
		return
	}
	
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
		),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSurface,
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = message.orEmpty(),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Spacer(modifier = Modifier.height(12.dp))
			Button(onClick = onPrimaryAction) {
				Text(
					text = if (pendingAnalysis) "Pedir nova análise" else "Pedir análise",
				)
			}
		}
	}
}

@Composable
private fun BackgroundGlow() {
	Box(modifier = Modifier.fillMaxSize()) {
		Box(
			modifier = Modifier
				.padding(top = 80.dp)
				.fillMaxWidth(0.7f)
				.background(Color(0xFF20D86B).copy(alpha = 0.05f)),
		)
	}
}


