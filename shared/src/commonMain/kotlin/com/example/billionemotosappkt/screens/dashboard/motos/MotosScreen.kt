package com.example.billionemotosappkt.screens.motos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.components.motos.MotoBottomRentBar
import com.example.billionemotosappkt.components.motos.MotoCategorySelector
import com.example.billionemotosappkt.components.motos.MotoDetailsCard
import com.example.billionemotosappkt.components.motos.MotoFeatureCard
import com.example.billionemotosappkt.components.motos.MotoHeroCard
import com.example.billionemotosappkt.components.motos.MotoVariant
import com.example.billionemotosappkt.ui.theme.AppDimens

@Composable
fun MotosScreen(
	onBack: () -> Unit,
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
	onRentClick: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	val variants = remember {
		listOf(
			MotoVariant(
				title = "Sport",
				subtitle = "A mais agressiva da linha",
				imageRes = "moto_sport_updated.png",
				monthlyPrice = "R$ 660",
				consumption = "45-65 km/L",
				details = listOf(
					"Freio" to "Tambor",
					"Partida" to "Pedal / Elétrica",
					"Tanque" to "10L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "160cc",
					"Cor" to "Preto",
					"Velocidade" to "Alta resposta",
				),
			),
			MotoVariant(
				title = "Sport ESD",
				subtitle = "Visual esportivo e urbano",
				imageRes = "avelloz_160_black_new.png",
				monthlyPrice = "R$ 620",
				consumption = "48-67 km/L",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Elétrica",
					"Tanque" to "10L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "160cc",
					"Cor" to "Vermelha",
					"Robustez" to "Uso diário",
				),
			),
			MotoVariant(
				title = "Elétrica",
				subtitle = "Silenciosa e moderna",
				imageRes = "moto_eletrica_new.png",
				monthlyPrice = "R$ 580",
				consumption = "Economia máxima",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Botão",
					"Tanque" to "Sem tanque",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "Elétrico",
					"Cor" to "Azul",
					"Autonomia" to "Longa duração",
				),
			),
			MotoVariant(
				title = "Premium",
				subtitle = "Conforto e presença",
				imageRes = "moto_premium.png",
				monthlyPrice = "R$ 740",
				consumption = "Conforto premium",
				details = listOf(
					"Freio" to "Disco",
					"Partida" to "Elétrica",
					"Tanque" to "12L",
					"Rodas" to "Liga leve",
				),
				specs = listOf(
					"Motor" to "Premium",
					"Cor" to "Branca",
					"Acabamento" to "Top",
				),
			),
		)
	}

	var selectedIndex by remember { mutableIntStateOf(0) }
	val selected = variants[selectedIndex]

	Scaffold(
		modifier = modifier.safeDrawingPadding(),
		containerColor = Color.Transparent,
		bottomBar = {
			MotoBottomRentBar(
				price = selected.monthlyPrice,
				benefitText = "Seminova • Aluguel • Km flexível • Mensal",
				onRentClick = onRentClick,
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(
					Brush.verticalGradient(
						colors = listOf(
							Color(0xFF030503),
							Color(0xFF090F0B),
							Color(0xFF050806),
						),
					),
				)
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(bottom = 18.dp),
		) {
			HeaderBar(onBack = onBack)
			MotoCategoryRow(
				variants = variants,
				selectedIndex = selectedIndex,
				onSelected = { selectedIndex = it },
			)

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = AppDimens.ScreenPadding),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text(
					text = "Escolha a sua moto",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 26.sp,
					fontWeight = FontWeight.Black,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)
				Text(
					text = selected.subtitle,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 13.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)

				MotoHeroCard(imageRes = selected.imageRes)

				Row(modifier = Modifier.fillMaxWidth()) {
					MotoFeatureCard(
						title = "A partir de",
						value = selected.monthlyPrice,
						modifier = Modifier.weight(1f),
					)
					Spacer(modifier = Modifier.width(12.dp))
					MotoFeatureCard(
						title = "Consumo",
						value = selected.consumption,
						modifier = Modifier.weight(1f),
					)
				}

				MotoDetailsCard(items = selected.details)

				QuickBenefitsRow()

				PrimaryActionCard(
					price = selected.monthlyPrice,
					onPlanosClick = onPlanosClick,
					onOndeEstamosClick = onOndeEstamosClick,
				)

				Spacer(modifier = Modifier.height(10.dp))
			}
		}
	}
}

@Composable
private fun HeaderBar(onBack: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = AppDimens.ScreenPadding, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		IconButton(onClick = onBack) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Voltar",
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}
		Text(
			text = "MOTOS",
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 14.sp,
			fontWeight = FontWeight.Black,
			textAlign = TextAlign.Center,
			modifier = Modifier.weight(1f),
		)
		Spacer(modifier = Modifier.width(48.dp))
	}
}

@Composable
private fun MotoCategoryRow(
	variants: List<MotoVariant>,
	selectedIndex: Int,
	onSelected: (Int) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
			.padding(horizontal = AppDimens.ScreenPadding, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		variants.forEachIndexed { index, variant ->
			MotoCategorySelector(
				variant = variant,
				isSelected = index == selectedIndex,
				onClick = { onSelected(index) },
			)
		}
	}
}

@Composable
private fun QuickBenefitsRow() {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		listOf("Seguro incluso", "Manutenção", "Sem limite").forEach { label ->
			Card(
				shape = RoundedCornerShape(999.dp),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
			) {
				Text(
					text = label,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 11.sp,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
				)
			}
		}
	}
}

@Composable
private fun PrimaryActionCard(
	price: String,
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Text(
				text = "Mensal a partir de $price",
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 18.sp,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = "Escolha um plano simples e veja o que faz sentido para o seu uso.",
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 13.sp,
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
			) {
				ActionButton(
					text = "Ver planos",
					onClick = onPlanosClick,
					modifier = Modifier.weight(1f),
					containerColor = MaterialTheme.colorScheme.primary,
					pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.86f),
					contentColor = MaterialTheme.colorScheme.onPrimary,
					borderColor = MaterialTheme.colorScheme.primary,
					pressedBorderColor = MaterialTheme.colorScheme.primary,
					glowPulse = false,
				)
			}
			HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
			) {
				SectionButton(text = "Onde estamos", onClick = onOndeEstamosClick, modifier = Modifier.weight(1f))
				SectionButton(text = "Voltar aos planos", onClick = onPlanosClick, modifier = Modifier.weight(1f))
			}
		}
	}
}

@Composable
private fun SectionButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		modifier = modifier,
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)),
	) {
		Text(
			text = text,
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 12.sp,
			fontWeight = FontWeight.SemiBold,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
		)
	}
}
