package com.example.billionemotosappkt.desktop.admin.screens.motosparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosList
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoImagePickerField
import com.example.billionemotosappkt.desktop.admin.data.MotosFilters
import com.example.billionemotosappkt.desktop.admin.data.MotosUiState
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListMotosQuery
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListManutencoesQuery
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import com.example.billionemotosappkt.shared.api.ManutencaoResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.UpdateMotoRequest
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info

@Composable
internal fun MotoStatCard(
	title: String,
	value: String,
	icon: ImageVector,
	accent: Color,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
				Text(title, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
			}
			Text(value, color = accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
		}
	}
}

@Composable
internal fun MetricCard(
	title: String,
	value: String,
	icon: ImageVector,
	accent: Color,
	subtitle: String,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp))
				Text(title, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
			}
			Text(value, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
			if (subtitle.isNotBlank()) {
				Text(subtitle, color = Color.White.copy(alpha = 0.38f), fontSize = 11.sp)
			}
		}
	}
}

@Composable
internal fun InfoPanel(
	title: String,
	icon: ImageVector,
	subtitle: String,
	rows: List<Pair<String, String>>,
	modifier: Modifier = Modifier,
	highlight: String? = null,
) {
	Card(
		modifier = modifier,
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = Color(0xFF20E65B), modifier = Modifier.size(16.dp))
				Column {
					Text(title, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
					Text(subtitle, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
				}
			}
			rows.forEach { (label, value) ->
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
					Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.42f))
					Text(value, color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp, modifier = Modifier.weight(0.58f), textAlign = TextAlign.End)
				}
			}
			if (!highlight.isNullOrBlank()) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.border(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
						.background(Color(0xFFFFB300).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
						.padding(horizontal = 12.dp, vertical = 10.dp),
				) {
					Text(highlight, color = Color.White.copy(alpha = 0.82f), fontSize = 12.sp)
				}
			}
		}
	}
}

@Composable
internal fun SectionCard(
	title: String,
	icon: ImageVector,
	heading: String,
	rows: List<Pair<String, String>> = emptyList(),
	body: (@Composable () -> Unit)? = null,
) {
	Card(
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Icon(icon, null, tint = Color(0xFF20E65B), modifier = Modifier.size(16.dp))
				Text(title, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
			}
			Text(heading, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
			rows.forEach { (label, value) ->
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
					Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.42f))
					Text(value, color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp, modifier = Modifier.weight(0.58f), textAlign = TextAlign.End)
				}
			}
			body?.invoke()
		}
	}
}

@Composable
internal fun HistoryCard(
	title: String,
	subtitle: String,
	right: String,
	description: String,
	accent: Color,
) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
		border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
	) {
		Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Column(modifier = Modifier.weight(1f)) {
					Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
					Text(subtitle, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
				}
				Text(right, color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
			}
			Text(description.ifBlank { "-" }, color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp, lineHeight = 16.sp)
		}
	}
}
