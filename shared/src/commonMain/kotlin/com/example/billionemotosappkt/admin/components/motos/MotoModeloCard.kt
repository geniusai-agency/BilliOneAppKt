package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import com.example.billionemotosappkt.desktop.admin.`fun`.getMotoModelCardImage
import com.example.billionemotosappkt.desktop.admin.`fun`.resolveImageSource
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
	val imageResource = remember(model.imagemReferenciaUrl, model.fotoUrls, model.marca, model.modelo, apiBaseUrl) {
		val primaryUrl = model.imagemReferenciaUrl ?: model.fotoUrls.firstOrNull() ?: ""
		resolveImageSource(primaryUrl, apiBaseUrl).ifBlank {
			getMotoModelCardImage(model.marca, model.modelo)
		}
	}

	Card(
		modifier = modifier.fillMaxWidth(),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0B100D)),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 168.dp),
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(18.dp),
				horizontalArrangement = Arrangement.spacedBy(18.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier
						.size(178.dp, 124.dp)
						.clip(RoundedCornerShape(14.dp))
						.background(
							Brush.radialGradient(
								listOf(
									Color(0xFF203028),
									Color(0xFF101813),
									Color(0xFF080D0A),
								),
							),
						)
						.border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(14.dp)),
					contentAlignment = Alignment.Center,
				) {
					Image(
						painter = desktopImagePainter(imageResource, apiBaseUrl, apiAccessToken),
						contentDescription = null,
						contentScale = ContentScale.Fit,
						modifier = Modifier.fillMaxSize().padding(8.dp),
					)
				}

				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(12.dp),
				) {
					Column(modifier = Modifier.fillMaxWidth().padding(end = 8.dp)) {
						Text(
							text = listOfNotNull(
								model.marca?.takeIf { it.isNotBlank() },
								model.nome?.takeIf { it.isNotBlank() },
								model.modelo?.takeIf { it.isNotBlank() },
							).joinToString(" ").ifBlank { "Modelo sem nome" },
							color = Color.White,
							fontWeight = FontWeight.Bold,
							fontSize = 20.sp,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
						)
						Spacer(Modifier.height(4.dp))
						Text(
							text = model.descricao?.takeIf { it.isNotBlank() }
								?: "Ficha tecnica e acoes de catalogo.",
							color = Color.White.copy(alpha = 0.5f),
							fontSize = 13.sp,
							maxLines = 2,
							lineHeight = 18.sp,
							overflow = TextOverflow.Ellipsis,
						)
					}

					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						MotoModeloSmallTag(text = model.categoria?.takeIf { it.isNotBlank() } ?: "Categoria", tint = Color(0xFF7DD3FC))
						MotoModeloSmallTag(text = model.tipo?.takeIf { it.isNotBlank() } ?: "Uso urbano", tint = Color(0xFFFFB300))
						MotoModeloSmallTag(text = model.combustivel?.takeIf { it.isNotBlank() } ?: "Combustivel", tint = Color(0xFF20E65B))
					}

					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						TechnicalTag(text = model.cilindrada?.let { "${it}cc" } ?: "160cc")
						TechnicalTag(text = model.ano?.toString() ?: "2026")
						TechnicalTag(text = model.id?.take(6) ?: "BLN-E")
					}
				}

				Column(
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.Center,
					modifier = Modifier.widthIn(min = 132.dp, max = 180.dp),
				) {
					Text(
						text = model.marca?.uppercase() ?: "AVELOZ",
						color = Color(0xFF20E65B),
						fontSize = 11.sp,
						fontWeight = FontWeight.Bold,
						letterSpacing = 1.sp,
					)
					Spacer(Modifier.height(4.dp))
					Text(
						text = model.precoInicial.toPriceLabel(),
						color = Color.White,
						fontSize = 18.sp,
						fontWeight = FontWeight.Black,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
					Text(
						text = "valor inicial",
						color = Color.White.copy(alpha = 0.3f),
						fontSize = 10.sp,
					)
				}
			}

			Box(
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(top = 12.dp, end = 12.dp),
			) {
				IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(32.dp)) {
					Icon(Icons.Default.MoreVert, null, tint = Color.White.copy(0.42f))
				}
				DropdownMenu(
					expanded = menuOpen,
					onDismissRequest = { menuOpen = false },
					modifier = Modifier.background(Color(0xFF0E1411)),
				) {
					DropdownMenuItem(text = { Text("Ver detalhes") }, onClick = { menuOpen = false; onView() })
					DropdownMenuItem(text = { Text("Editar") }, onClick = { menuOpen = false; onEdit() })
					DropdownMenuItem(text = { Text("Desativar", color = Color(0xFFFF4A4A)) }, onClick = { menuOpen = false; onDelete() })
				}
			}
		}
	}
}

private fun String?.toPriceLabel(): String {
	val value = this?.trim().orEmpty()
	if (value.isBlank()) return "R$ 0,00"
	return if (value.startsWith("R$", ignoreCase = true)) value else "R$ $value"
}

@Composable
private fun TechnicalTag(text: String) {
	Surface(
		color = Color(0xFF20E65B).copy(alpha = 0.08f),
		shape = RoundedCornerShape(6.dp),
		border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.15f)),
	) {
		Text(
			text = text,
			color = Color(0xFF20E65B).copy(alpha = 0.8f),
			fontSize = 11.sp,
			fontWeight = FontWeight.Medium,
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
		)
	}
}
