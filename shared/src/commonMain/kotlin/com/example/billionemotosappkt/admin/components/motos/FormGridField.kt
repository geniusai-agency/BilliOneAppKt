package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FormGridField(
	label: String,
	value: String,
	singleLine: Boolean = true,
	minLines: Int = 1,
	onValueChange: (String) -> Unit,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label, color = Color.White.copy(alpha = 0.58f)) },
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		singleLine = singleLine,
		minLines = minLines,
		colors = OutlinedTextFieldDefaults.colors(
			focusedBorderColor = Color(0xFF20E65B),
			unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
			focusedContainerColor = Color(0xFF0F1411),
			unfocusedContainerColor = Color(0xFF0B100D),
		),
	)
}
