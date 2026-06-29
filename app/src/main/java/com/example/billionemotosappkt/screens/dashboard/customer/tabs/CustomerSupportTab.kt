package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerChatMessage
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContactFormState
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardData
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerSupportView
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerSupportTab(
	view: CustomerSupportView,
	onViewChange: (CustomerSupportView) -> Unit,
	openFaqIndex: Int?,
	onFaqToggle: (Int?) -> Unit,
	chatMessages: List<CustomerChatMessage>,
	chatInput: String,
	onChatInputChange: (String) -> Unit,
	onSendMessage: () -> Unit,
	contactForm: CustomerContactFormState,
	onContactFormChange: (CustomerContactFormState) -> Unit,
	contactSent: Boolean,
	onContactSubmit: () -> Unit,
	onContactDismiss: () -> Unit,
	dashboardData: CustomerDashboardData,
	modifier: Modifier = Modifier,
) {
	when (view) {
		CustomerSupportView.MENU -> SupportMenu(
			dashboardData = dashboardData,
			onItemClick = onViewChange,
			modifier = modifier,
		)

		CustomerSupportView.CHAT -> SupportChat(
			dashboardData = dashboardData,
			chatMessages = chatMessages,
			chatInput = chatInput,
			onChatInputChange = onChatInputChange,
			onSendMessage = onSendMessage,
			onBack = { onViewChange(CustomerSupportView.MENU) },
			modifier = modifier,
		)

		CustomerSupportView.FAQ -> SupportFaq(
			dashboardData = dashboardData,
			openFaqIndex = openFaqIndex,
			onFaqToggle = onFaqToggle,
			onBack = { onViewChange(CustomerSupportView.MENU) },
			modifier = modifier,
		)

		CustomerSupportView.CONTACT -> SupportContact(
			dashboardData = dashboardData,
			formState = contactForm,
			sent = contactSent,
			onFormChange = onContactFormChange,
			onSubmit = onContactSubmit,
			onDismiss = onContactDismiss,
			modifier = modifier,
		)
	}
}

@Composable
private fun SupportMenu(
	dashboardData: CustomerDashboardData,
	onItemClick: (CustomerSupportView) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		SupportMenuHeader()

		Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
			dashboardData.supportMenu.forEach { item ->
				SupportMenuRow(
					label = item.label,
					subtitle = supportMenuSubtitle(item.view),
					icon = supportMenuIcon(item.icon),
					accent = item.accent,
					onClick = { onItemClick(item.view) },
				)
			}
		}

		CustomerAppCard(
			title = "Contato direto",
			description = null,
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					dashboardData.supportContacts.forEach { line ->
						ContactLine(line = line)
					}
				}
			},
		)
	}
}

@Composable
private fun SupportChat(
	dashboardData: CustomerDashboardData,
	chatMessages: List<CustomerChatMessage>,
	chatInput: String,
	onChatInputChange: (String) -> Unit,
	onSendMessage: () -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Chat rapido",
			description = "Use palavras simples e receba uma resposta imediata.",
			content = {
				Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.clip(RoundedCornerShape(16.dp))
							.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
							.padding(14.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
						Spacer(modifier = Modifier.size(10.dp))
						Text(
							text = dashboardData.chatWelcome,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.weight(1f),
						)
					}

					chatMessages.forEach { message ->
						ChatBubble(message = message)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
						OutlinedTextField(
							value = chatInput,
							onValueChange = onChatInputChange,
							placeholder = { Text(text = "Digite sua mensagem...") },
							modifier = Modifier.weight(1f),
							shape = RoundedCornerShape(16.dp),
						)
						Button(
							onClick = onSendMessage,
							colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
							modifier = Modifier.size(56.dp),
						) {
							Icon(imageVector = AutoMirrored.Filled.Send, contentDescription = null)
						}
					}
				}
			},
		)

		SupportBackButton(onBack = onBack)
	}
}

@Composable
private fun SupportFaq(
	dashboardData: CustomerDashboardData,
	openFaqIndex: Int?,
	onFaqToggle: (Int?) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Perguntas frequentes",
			description = "Respostas rapidas para as duvidas mais comuns.",
			content = {
				CustomerFaqAccordion(
					items = dashboardData.faqs,
					openIndex = openFaqIndex,
					onToggle = onFaqToggle,
				)
			},
		)
		SupportBackButton(onBack = onBack)
	}
}

@Composable
private fun SupportContact(
	dashboardData: CustomerDashboardData,
	formState: CustomerContactFormState,
	sent: Boolean,
	onFormChange: (CustomerContactFormState) -> Unit,
	onSubmit: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
		CustomerAppCard(
			title = "Formulario de contato",
			description = if (sent) "Mensagem enviada com sucesso." else "Envie sua mensagem para a equipe.",
			content = {
				CustomerContactForm(
					formState = formState,
					sent = sent,
					onFormChange = onFormChange,
					onSubmit = onSubmit,
					onDismiss = onDismiss,
				)
			},
		)
		SupportBackButton(onBack = onDismiss)
	}
}

@Composable
private fun SupportMenuHeader() {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Box(
			modifier = Modifier
				.size(48.dp)
				.clip(RoundedCornerShape(18.dp))
				.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
			contentAlignment = Alignment.Center,
		) {
			Icon(
				imageVector = Icons.Default.HeadsetMic,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		}
		Text(
			text = "Central de Suporte",
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurface,
		)
		Text(
			text = "Como podemos ajudar?",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun SupportMenuRow(
	label: String,
	subtitle: String,
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	accent: Boolean,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
			.border(
				1.dp,
				if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
				RoundedCornerShape(16.dp),
			)
			.padding(14.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Row(
			modifier = Modifier.weight(1f),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(
						if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.24f),
					),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
			Spacer(modifier = Modifier.size(12.dp))
			Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(
					text = label,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurface,
				)
				Text(
					text = subtitle,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
		Text(text = "›", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
	}
}

@Composable
private fun ContactLine(line: com.example.billionemotosappkt.screens.dashboard.customer.CustomerContactLine) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f))
			.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
			.padding(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			imageVector = contactIcon(line.icon),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
		)
		Spacer(modifier = Modifier.size(10.dp))
		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(text = line.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
			Text(text = line.value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
		}
	}
}

@Composable
private fun ChatBubble(message: CustomerChatMessage) {
	val user = message.role == com.example.billionemotosappkt.screens.dashboard.customer.CustomerChatRole.USER
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = if (user) Arrangement.End else Arrangement.Start,
	) {
		Column(
			modifier = Modifier
				.clip(RoundedCornerShape(16.dp))
				.background(if (user) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
				.border(1.dp, if (user) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
				.padding(12.dp),
		) {
			Text(
				text = message.text,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}

@Composable
private fun SupportBackButton(onBack: () -> Unit) {
	Button(
		onClick = onBack,
		colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Icon(imageVector = AutoMirrored.Filled.ArrowBack, contentDescription = null)
		Spacer(modifier = Modifier.size(8.dp))
		Text(text = "Voltar", fontWeight = FontWeight.Black)
	}
}

private fun supportMenuIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
	return when (name) {
		"chat", "message" -> Icons.Default.ChatBubbleOutline
		"help" -> AutoMirrored.Filled.HelpOutline
		"mail" -> Icons.Default.Email
		else -> Icons.Default.HeadsetMic
	}
}

private fun contactIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
	return when (name) {
		"phone" -> Icons.Default.Phone
		"mail" -> Icons.Default.Email
		"map" -> Icons.Default.LocationOn
		else -> Icons.Default.SupportAgent
	}
}

private fun supportMenuSubtitle(view: CustomerSupportView): String {
	return when (view) {
		CustomerSupportView.CHAT -> "Respostas rápidas 24/7"
		CustomerSupportView.FAQ -> "Dúvidas mais comuns"
		CustomerSupportView.CONTACT -> "Envie sua mensagem"
		CustomerSupportView.MENU -> "Central de suporte"
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerSupportMenuPreview() {
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
			contactForm = CustomerDashboardPreviewData.contactForm,
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = CustomerDashboardPreviewData.dashboardData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerSupportChatPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerSupportTab(
			view = CustomerSupportView.CHAT,
			onViewChange = {},
			openFaqIndex = null,
			onFaqToggle = {},
			chatMessages = CustomerDashboardPreviewData.chatMessages,
			chatInput = "Quero ver meu contrato.",
			onChatInputChange = {},
			onSendMessage = {},
			contactForm = CustomerDashboardPreviewData.contactForm,
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = CustomerDashboardPreviewData.dashboardData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerSupportFaqPreview() {
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
			contactForm = CustomerDashboardPreviewData.contactForm,
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = CustomerDashboardPreviewData.dashboardData,
		)
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerSupportContactPreview() {
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
			contactForm = CustomerDashboardPreviewData.contactForm,
			onContactFormChange = {},
			contactSent = false,
			onContactSubmit = {},
			onContactDismiss = {},
			dashboardData = CustomerDashboardPreviewData.dashboardData,
		)
	}
}
