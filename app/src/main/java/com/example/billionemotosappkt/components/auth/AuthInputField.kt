package com.example.billionemotosappkt.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthInputField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	placeholder: String,
	modifier: Modifier = Modifier,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	trailingIcon: @Composable (() -> Unit)? = null,
	errorMessage: String? = null,
) {
	Column(modifier = modifier.fillMaxWidth()) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSurface,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(bottom = 10.dp),
		)

		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			shape = RoundedCornerShape(14.dp),
			placeholder = {
				Text(text = placeholder)
			},
			textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
			visualTransformation = visualTransformation,
			keyboardOptions = keyboardOptions,
			trailingIcon = trailingIcon,
			isError = errorMessage != null,
			colors = OutlinedTextFieldDefaults.colors(
				focusedTextColor = MaterialTheme.colorScheme.onSurface,
				unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
				disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				focusedBorderColor = MaterialTheme.colorScheme.primary,
				unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
				disabledBorderColor = MaterialTheme.colorScheme.outline,
				cursorColor = MaterialTheme.colorScheme.primary,
				focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
				unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
				disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
				focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
				unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
			),
		)
		if (errorMessage != null) {
			Text(
				text = errorMessage,
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.labelSmall,
				modifier = Modifier.padding(top = 6.dp),
			)
		}
	}
}
