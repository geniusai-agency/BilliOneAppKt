package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.data.MotoModeloFormState
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter

@Composable
fun MotoModeloPreviewPane(
	title: String,
	heroResource: String,
	cardResource: String,
	form: MotoModeloFormState,
	onDismiss: () -> Unit,
	apiBaseUrl: String,
	apiAccessToken: String?,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxHeight()
			.background(
				Brush.verticalGradient(
					listOf(Color(0xFF0D140F), Color(0xFF060906)),
				),
			)
			.padding(16.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(14.dp),
			modifier = Modifier.fillMaxSize()
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = title,
						color = Color.White,
						fontSize = 24.sp,
						fontWeight = FontWeight.Black
					)
					Text(
						text = listOf(form.marca, form.nome).joinToString(" ")
							.ifBlank { "Pré-visualização do modelo" },
						color = Color.White.copy(alpha = 0.62f),
						fontSize = 13.sp,
					)
				}
				TextButton(onClick = onDismiss) { Text("Fechar") }
			}
			
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
					.clip(RoundedCornerShape(24.dp))
					.background(Color.Black.copy(alpha = 0.18f))
					.border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp)),
			) {
				Image(
					painter = desktopImagePainter(heroResource, apiBaseUrl, apiAccessToken),
					contentDescription = null,
					modifier = Modifier.fillMaxSize(),
				)
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(
							Brush.verticalGradient(
								listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f)),
							),
						),
				)
				Column(
					modifier = Modifier
						.align(Alignment.BottomStart)
						.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp),
				) {
					Text(
						text = listOf(form.marca, form.nome).joinToString(" ")
							.ifBlank { "Novo modelo" },
						color = Color.White,
						fontSize = 22.sp,
						fontWeight = FontWeight.Black,
					)
					Text(
						text = form.descricao.ifBlank { "Preencha os campos ao lado para ver a ficha do modelo." },
						color = Color.White.copy(alpha = 0.72f),
						fontSize = 12.sp,
						maxLines = 3,
						overflow = TextOverflow.Ellipsis,
					)
					Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						MotoModeloSmallTag(
							text = form.categoria.ifBlank { "Categoria" },
							tint = Color(0xFF7DD3FC)
						)
						MotoModeloSmallTag(
							text = form.tipo.ifBlank { "Tipo" },
							tint = Color(0xFFFFB300)
						)
						MotoModeloSmallTag(
							text = form.combustivel.ifBlank { "Combustível" },
							tint = Color(0xFF20E65B)
						)
					}
				}
			}
			
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				ModelHeroStat(
					title = "Imagem",
					value = "catálogo",
					modifier = Modifier.weight(1f),
					imageResource = cardResource,
					apiBaseUrl = apiBaseUrl,
					apiAccessToken = apiAccessToken,
				)
				ModelHeroStat(
					title = "Técnico",
					value = listOfNotNull(
						form.cilindrada.takeIf { it.isNotBlank() }?.plus("cc"),
						form.ano.takeIf { it.isNotBlank() }).joinToString(" • ")
						.ifBlank { "sem dados" },
					modifier = Modifier.weight(1f),
					imageResource = "moto_detail_engine.png",
					apiBaseUrl = apiBaseUrl,
					apiAccessToken = apiAccessToken,
				)
			}
		}
	}
}