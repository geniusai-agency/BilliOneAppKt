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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import com.example.billionemotosappkt.desktop.admin.`fun`.resolveImageSource
import com.example.billionemotosappkt.desktop.admin.screens.getMotoModelCardImage
import com.example.billionemotosappkt.shared.api.MotoModeloResponse

@Composable
fun MotoModeloCard(
	model: MotoModeloResponse,
	onView: () -> Unit,
	onEdit: () -> Unit,
	onDelete: () -> Unit,
	apiBaseUrl: String,
	apiAccessToken: String?,
	modifier: Modifier = Modifier,
) {
	var menuOpen by remember { mutableStateOf(false) }
	val imageResource = remember(model.imagemReferenciaUrl, model.marca, model.modelo, apiBaseUrl) {
		resolveImageSource(model.imagemReferenciaUrl, apiBaseUrl).ifBlank {
			getMotoModelCardImage(model.marca, model.modelo)
		}
	}
	Card(
		modifier = modifier.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
		border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(168.dp, 118.dp)
					.clip(RoundedCornerShape(22.dp))
					.background(
						Brush.linearGradient(
							listOf(
								Color(0xFF101915),
								Color(0xFF080D0A),
							),
						),
					)
					.border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(22.dp)),
				contentAlignment = Alignment.Center,
			) {
				Image(
					painter = desktopImagePainter(imageResource, apiBaseUrl, apiAccessToken),
					contentDescription = null,
					modifier = Modifier.fillMaxSize(),
				)
			}
			
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.Top,
				) {
					Column(
						modifier = Modifier.weight(1f),
						verticalArrangement = Arrangement.spacedBy(4.dp)
					) {
						Text(
							text = listOfNotNull(model.marca, model.nome).joinToString(" ")
								.ifBlank { "Modelo sem nome" },
							color = Color.White,
							fontWeight = FontWeight.Bold,
							fontSize = 19.sp,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
						)
						Text(
							text = model.descricao?.takeIf { it.isNotBlank() }
								?: "Ficha técnica e ações de catálogo.",
							color = Color.White.copy(alpha = 0.56f),
							maxLines = 2,
							overflow = TextOverflow.Ellipsis,
						)
					}
				}
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					MotoModeloSmallTag(
						text = model.categoria ?: "Categoria não definida",
						tint = Color(0xFF7DD3FC)
					)
					MotoModeloSmallTag(
						text = model.tipo ?: "Tipo não definido",
						tint = Color(0xFFFFB300)
					)
					MotoModeloSmallTag(
						text = model.combustivel ?: "Combustível não definido",
						tint = Color(0xFF20E65B)
					)
				}
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					MotoModeloSmallTag(text = model.cilindrada?.let { "${it}cc" } ?: "cc",
						tint = Color(0xFF20E65B))
					MotoModeloSmallTag(
						text = model.ano?.toString() ?: "ano",
						tint = Color(0xFF7DD3FC)
					)
					if (!model.id.isNullOrBlank()) {
						MotoModeloSmallTag(
							text = model.id.orEmpty().take(6),
							tint = Color.White.copy(alpha = 0.68f)
						)
					}
				}
			}
			
			Column(
				horizontalAlignment = Alignment.End,
				verticalArrangement = Arrangement.spacedBy(10.dp),
				modifier = Modifier
					.widthIn(min = 110.dp, max = 160.dp)
					.align(Alignment.Top),
			) {
				Box(contentAlignment = Alignment.TopEnd) {
					IconButton(onClick = { menuOpen = true }) {
						Icon(
							imageVector = Icons.Default.MoreVert,
							contentDescription = "Abrir ações",
							tint = Color.White.copy(alpha = 0.8f),
						)
					}
					DropdownMenu(
						expanded = menuOpen,
						onDismissRequest = { menuOpen = false },
						modifier = Modifier.background(Color(0xFF0E1411)),
					) {
						DropdownMenuItem(
							text = { Text("Ver detalhes") },
							onClick = {
								menuOpen = false
								onView()
							},
						)
						DropdownMenuItem(
							text = { Text("Editar") },
							onClick = {
								menuOpen = false
								onEdit()
							},
						)
						DropdownMenuItem(
							text = { Text("Desativar", color = Color(0xFFFF4A4A)) },
							onClick = {
								menuOpen = false
								onDelete()
							},
						)
					}
				}
				Text(
					text = model.marca?.uppercase()?.take(12) ?: "CATÁLOGO",
					color = Color(0xFF20E65B),
					fontSize = 11.sp,
					fontWeight = FontWeight.Bold,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = "R$ ${model.precoInicial ?: "n/a"}",
					color = Color.White,
					fontSize = 16.sp,
					fontWeight = FontWeight.Black,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = "Ações no menu",
					color = Color.White.copy(alpha = 0.42f),
					fontSize = 11.sp,
				)
			}
		}
	}
}


