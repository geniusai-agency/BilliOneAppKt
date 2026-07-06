package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MotosSearchField(value: String, onValueChange: (String) -> Unit) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = Modifier.width(320.dp),
		placeholder = { Text("Buscar placa, modelo...") },
		leadingIcon = {
			Icon(
				Icons.Default.Search,
				contentDescription = null,
				modifier = Modifier.size(20.dp)
			)
		},
		shape = RoundedCornerShape(16.dp),
		singleLine = true,
		colors = OutlinedTextFieldDefaults.colors(
			focusedBorderColor = Color(0xFF20E65B),
			unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
			focusedContainerColor = Color(0xFF111614),
			unfocusedContainerColor = Color(0xFF0D1210)
		)
	)
}