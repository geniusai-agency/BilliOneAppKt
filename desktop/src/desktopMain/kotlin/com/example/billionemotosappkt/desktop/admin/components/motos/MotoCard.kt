package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import com.example.billionemotosappkt.desktop.admin.`fun`.getMotoImage
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus

@Composable
fun MotoCard(moto: MotoResponse) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
		border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
	) {
		Row(
			modifier = Modifier.padding(16.dp).fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(20.dp)
		) {
			val imageName = remember(moto.modelo) { getMotoImage(moto.modelo) }
			Box(
				modifier = Modifier.size(120.dp, 90.dp).clip(RoundedCornerShape(12.dp))
					.background(Color.Black.copy(alpha = 0.25f)),
				contentAlignment = Alignment.Center
			) {
				Image(
					painter = desktopImagePainter(imageName),
					contentDescription = null,
					modifier = Modifier.fillMaxSize().padding(8.dp)
				)
			}
			
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(6.dp)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = "${moto.marca ?: ""} ${moto.modelo ?: ""}".trim()
							.ifBlank { "Moto #${moto.id.take(5)}" },
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = Color.White
					)
					Box(
						modifier = Modifier.background(
							Color(0xFF20E65B).copy(alpha = 0.1f),
							RoundedCornerShape(6.dp)
						).padding(horizontal = 6.dp, vertical = 2.dp)
					) {
						Text(
							text = moto.placa,
							color = Color(0xFF20E65B),
							fontSize = 11.sp,
							fontWeight = FontWeight.Bold
						)
					}
				}
				Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
					InfoItem(icon = Icons.Default.Speed, text = "${moto.kmAtual} km")
					InfoItem(
						icon = Icons.Default.CalendarToday,
						text = moto.ano?.toString() ?: "N/A"
					)
					InfoItem(
						icon = if (moto.rastreador?.ativo == true) Icons.Default.LocationOn else Icons.Default.LocationOff,
						text = if (moto.rastreador?.ativo == true) "Rastreador ativo" else "Sem rastreamento"
					)
				}
			}
			
			MotoStatusBadge(status = moto.status)
			Icon(
				Icons.AutoMirrored.Filled.ArrowForwardIos,
				contentDescription = null,
				tint = Color.White.copy(alpha = 0.3f),
				modifier = Modifier.size(16.dp)
			)
		}
	}
}


@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp)
	) {
		Icon(
			icon,
			contentDescription = null,
			modifier = Modifier.size(14.dp),
			tint = Color.White.copy(alpha = 0.4f)
		)
		Text(text = text, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
	}
}

@Composable
private fun MotoStatusBadge(status: MotoStatus) {
	val (color, label) = when (status) {
		MotoStatus.DISPONIVEL -> Color(0xFF20E65B) to "Disponível"
		MotoStatus.ALUGADA -> Color(0xFF7DD3FC) to "Alugada"
		MotoStatus.MANUTENCAO -> Color(0xFFFFB300) to "Manutenção"
		MotoStatus.BLOQUEADA -> Color(0xFFFF4A4A) to "Bloqueada"
		MotoStatus.CONTRATADA -> Color(0xFF8B5CF6) to "Contratada"
		MotoStatus.PENDENTE_CONTRATO -> Color(0xFFFFB300).copy(alpha = 0.8f) to "Pendente"
		else -> Color.White.copy(alpha = 0.4f) to status.name
	}
	Box(
		modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.12f))
			.border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
			.padding(horizontal = 14.dp, vertical = 8.dp)
	) {
		Text(text = label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}