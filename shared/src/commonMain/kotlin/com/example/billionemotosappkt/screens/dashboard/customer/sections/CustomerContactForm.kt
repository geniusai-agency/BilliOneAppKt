package com.example.billionemotosappkt.screens.dashboard.customer.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
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
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerContactFormState

import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerContactForm(
	formState: CustomerContactFormState,
	sent: Boolean,
	onFormChange: (CustomerContactFormState) -> Unit,
	onSubmit: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
) {
	if (sent) {
		Column(
			modifier = modifier
				.fillMaxWidth()
				.padding(vertical = 20.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
			Text(
				text = "Mensagem enviada!",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = "Responderemos em ate 24 horas.",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Spacer(modifier = Modifier.height(6.dp))
			Button(onClick = onDismiss) {
				Text(text = "Voltar ao suporte", fontWeight = FontWeight.Black)
			}
		}
		return
	}

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		InputField(
			label = "Nome",
			value = formState.name,
			onValueChange = { onFormChange(formState.copy(name = it)) },
			placeholder = "Seu nome completo",
		)
		InputField(
			label = "E-mail",
			value = formState.email,
			onValueChange = { onFormChange(formState.copy(email = it)) },
			placeholder = "seu@email.com",
		)
		InputField(
			label = "Mensagem",
			value = formState.message,
			onValueChange = { onFormChange(formState.copy(message = it)) },
			placeholder = "Descreva sua duvida ou problema",
			minLines = 4,
		)

		Button(
			onClick = onSubmit,
			enabled = formState.name.isNotBlank() && formState.email.isNotBlank() && formState.message.isNotBlank(),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
			modifier = Modifier
				.fillMaxWidth()
				.height(54.dp)
				.clip(RoundedCornerShape(16.dp))
				.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
		) {
			Icon(imageVector = AutoMirrored.Filled.Send, contentDescription = null)
			Spacer(modifier = Modifier.width(8.dp))
			Text(text = "Enviar mensagem", fontWeight = FontWeight.Black)
		}
	}
}

@Composable
private fun InputField(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	placeholder: String,
	minLines: Int = 1,
) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			text = label.uppercase(),
			style = MaterialTheme.typography.labelSmall,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			placeholder = { Text(text = placeholder) },
			minLines = minLines,
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(16.dp),
		)
	}
}




