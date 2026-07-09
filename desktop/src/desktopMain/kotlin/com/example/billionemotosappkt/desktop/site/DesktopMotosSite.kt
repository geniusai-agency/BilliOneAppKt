package com.example.billionemotosappkt.desktop.site

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.auth.DesktopAuthClient
import com.example.billionemotosappkt.desktop.auth.DesktopAuthDialog
import com.example.billionemotosappkt.desktop.auth.DesktopAuthMode
import com.example.billionemotosappkt.desktop.auth.UserKind
import com.example.billionemotosappkt.desktop.auth.toContext

internal data class DesktopMotoVariant(
	val title: String,
	val subtitle: String,
	val image: String,
	val monthlyPrice: String,
	val consumption: String,
	val details: List<Pair<String, String>>,
	val specs: List<Pair<String, String>>,
)

@Composable
internal fun DesktopSectionScreen(
	title: String,
	description: String,
	onBack: () -> Unit,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						Color(0xFF050806),
						Color(0xFF0B110D),
						Color(0xFF050806),
					),
				),
			)
			.padding(horizontal = 20.dp, vertical = 16.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Button(
				onClick = onBack,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f),
					contentColor = MaterialTheme.colorScheme.onSurface,
				),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = null,
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text("Voltar")
			}
			Image(
				painter = painterResource("logo_billione.png"),
				contentDescription = "Logo Billione",
				modifier = Modifier.size(48.dp),
			)
		}

		Spacer(modifier = Modifier.height(22.dp))
		Text(
			text = title,
			fontSize = 34.sp,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colorScheme.onSurface,
		)
		Spacer(modifier = Modifier.height(6.dp))
		Text(
			text = description,
			fontSize = 16.sp,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

	Spacer(modifier = Modifier.height(20.dp))
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f, fill = true),
		) {
			content()
		}
	}
}

@Composable
internal fun DesktopMotosSection() {
	DesktopMotosScreen(
		onPlanosClick = { },
		onOndeEstamosClick = { },
		onRentClick = { },
	)
}

@Composable
internal fun DesktopMotosScreen(
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
	onRentClick: () -> Unit,
) {
	val motoVariants = remember {
		listOf(
			DesktopMotoVariant(
				title = "Sport",
				subtitle = "A mais agressiva da linha",
				image = "moto_sport_updated.png",
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
			DesktopMotoVariant(
				title = "Sport ESD",
				subtitle = "Visual esportivo e urbano",
				image = "hero_bg_moto2.png",
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
			DesktopMotoVariant(
				title = "Elétrica",
				subtitle = "Silenciosa e moderna",
				image = "moto_eletrica_new.png",
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
			DesktopMotoVariant(
				title = "Premium",
				subtitle = "Conforto e presença",
				image = "moto_premium.png",
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
	var selectedCondition by remember { mutableStateOf(0) }
	var selectedContract by remember { mutableStateOf(1) }
	var selectedMileage by remember { mutableStateOf(1) }
	var selectedPayment by remember { mutableStateOf(1) }
	val selected = motoVariants[selectedIndex]

	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(bottom = 2.dp),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 20.dp, vertical = 8.dp)
				.padding(bottom = 160.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = "ESCOLHA SUA MOTO",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 18.sp,
					fontWeight = FontWeight.Black,
				)
				DesktopSectionLinks(
					onPlanosClick = onPlanosClick,
					onOndeEstamosClick = onOndeEstamosClick,
				)
			}

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState()),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				motoVariants.forEachIndexed { index, variant ->
					DesktopMotoCategorySelector(
						variant = variant,
						isSelected = index == selectedIndex,
						onClick = { selectedIndex = index },
					)
				}
			}

			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				Text(
					text = "Conheça a nova",
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 16.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)
				Text(
					text = "Billione ${selected.title} 160",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 30.sp,
					fontWeight = FontWeight.Black,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)

				DesktopMotoHeroCard(imageName = selected.image)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
				) {
					DesktopMotoFeatureCard(
						title = "Modelo",
						value = "Avelloz 160",
						modifier = Modifier.weight(1f),
					)
					DesktopMotoFeatureCard(
						title = "Consumo",
						value = selected.consumption,
						modifier = Modifier.weight(1f),
					)
				}

				DesktopMotoDetailsCard(items = selected.details)

				DesktopPlanSummaryCard(
					condition = planConditionLabel(selectedCondition),
					contract = planContractLabel(selectedContract),
					mileage = planMileageLabel(selectedMileage),
					payment = planPaymentLabel(selectedPayment),
				)
			}

			DesktopPlanCustomizationSection(
				selectedCondition = selectedCondition,
				onConditionSelected = { selectedCondition = it },
				selectedContract = selectedContract,
				onContractSelected = { selectedContract = it },
				selectedMileage = selectedMileage,
				onMileageSelected = { selectedMileage = it },
				selectedPayment = selectedPayment,
				onPaymentSelected = { selectedPayment = it },
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				DesktopSectionLinkButton(text = "Nossos planos", onClick = onPlanosClick)
				DesktopSectionLinkButton(text = "Onde estamos", onClick = onOndeEstamosClick)
			}
		}

		DesktopMotoBottomRentBar(
			price = "R$ 1.100",
			benefitText = "Billione Premium",
			onRentClick = onRentClick,
			modifier = Modifier.align(Alignment.BottomCenter),
		)
	}
}

@Composable
internal fun DesktopSectionLinks(
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
) {
	Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
		DesktopSectionLinkButton(text = "Nossos planos", onClick = onPlanosClick)
		DesktopSectionLinkButton(text = "Onde estamos", onClick = onOndeEstamosClick)
	}
}

@Composable
internal fun DesktopSectionLinkButton(
	text: String,
	onClick: () -> Unit,
) {
	OutlinedButton(
		onClick = onClick,
		shape = RoundedCornerShape(14.dp),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
	) {
		Text(text = text, color = MaterialTheme.colorScheme.onSurface)
	}
}
