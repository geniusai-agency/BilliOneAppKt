package com.example.billionemotosappkt.desktop.admin.clientes.components.editparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.billionemotosappkt.desktop.admin.clientes.repository.ClientBody
import kotlinx.coroutines.launch
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.UpdateClienteRequest

// FormSection e EditField mantidos idênticos aqui abaixo...
@Composable
internal fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Text(
			title.uppercase(),
			fontSize = 11.sp,
			fontWeight = FontWeight.Bold,
			color = Color(0xFF20E65B)
		)
		content()
	}
}

@Composable
internal fun EditField(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	singleLine: Boolean = true,
	minLines: Int = 1,
	enabled: Boolean = true,
	visualTransformation: VisualTransformation = VisualTransformation.None
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(label) },
		modifier = modifier.fillMaxWidth(),
		singleLine = singleLine,
		minLines = minLines,
		enabled = enabled,
		visualTransformation = visualTransformation,
		shape = RoundedCornerShape(12.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedTextColor = Color.White,
			unfocusedTextColor = Color.White,
			disabledTextColor = Color.White.copy(0.4f),
			unfocusedBorderColor = Color.White.copy(0.1f),
			focusedBorderColor = Color(0xFF20E65B),
			disabledBorderColor = Color.White.copy(0.05f),
			unfocusedLabelColor = Color.White.copy(0.4f),
			focusedLabelColor = Color(0xFF20E65B),
			disabledLabelColor = Color.White.copy(0.2f)
		)
	)
}
