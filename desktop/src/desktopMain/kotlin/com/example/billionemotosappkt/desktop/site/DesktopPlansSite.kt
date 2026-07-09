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

@Composable
internal fun DesktopPlanCustomizationSection(
	selectedCondition: Int,
	onConditionSelected: (Int) -> Unit,
	selectedContract: Int,
	onContractSelected: (Int) -> Unit,
	selectedMileage: Int,
	onMileageSelected: (Int) -> Unit,
	selectedPayment: Int,
	onPaymentSelected: (Int) -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Text(
			text = "PERSONALIZE SEU PLANO",
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 22.sp,
			fontWeight = FontWeight.Black,
		)

		DesktopPlanOptionGroup(
			title = "CONDIÇÃO",
			options = listOf(
				DesktopPlanOption("0 km", "+R$ 200/mês"),
				DesktopPlanOption("Seminova", null),
			),
			selectedIndex = selectedCondition,
			onSelected = onConditionSelected,
		)

		DesktopPlanOptionGroup(
			title = "CONTRATO",
			options = listOf(
				DesktopPlanOption("Opção compra no final", "+R$ 150/mês"),
				DesktopPlanOption("Somente aluguel", null),
			),
			selectedIndex = selectedContract,
			onSelected = onContractSelected,
		)

		DesktopPlanOptionGroup(
			title = "QUILOMETRAGEM",
			options = listOf(
				DesktopPlanOption("Km ilimitado", "+R$ 120/mês"),
				DesktopPlanOption("Km flexível", null),
			),
			selectedIndex = selectedMileage,
			onSelected = onMileageSelected,
		)

		DesktopPlanOptionGroup(
			title = "PAGAMENTO",
			options = listOf(
				DesktopPlanOption("Pagamento semanal", null),
				DesktopPlanOption("Pagamento mensal", null),
			),
			selectedIndex = selectedPayment,
			onSelected = onPaymentSelected,
		)
	}
}

@Composable
internal fun DesktopPlanSummaryCard(
	condition: String,
	contract: String,
	mileage: String,
	payment: String,
) {
	Card(
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Text(
				text = "INFORMAÇÕES DO PLANO",
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 14.sp,
				fontWeight = FontWeight.Black,
			)
			DesktopPlanSummaryRow(label = "Condição", value = condition)
			DesktopPlanSummaryRow(label = "Contrato", value = contract)
			DesktopPlanSummaryRow(label = "Quilometragem", value = mileage)
			DesktopPlanSummaryRow(label = "Pagamento", value = payment)
		}
	}
}

@Composable
internal fun DesktopPlanSummaryRow(
	label: String,
	value: String,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
		)
		Text(
			text = value,
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 13.sp,
			fontWeight = FontWeight.Bold,
		)
	}
}

internal fun planConditionLabel(index: Int) = if (index == 0) "0 km" else "Seminova"

internal fun planContractLabel(index: Int) = if (index == 0) "Opção compra no final" else "Somente aluguel"

internal fun planMileageLabel(index: Int) = if (index == 0) "Km ilimitado" else "Km flexível"

internal fun planPaymentLabel(index: Int) = if (index == 0) "Pagamento semanal" else "Pagamento mensal"

internal data class DesktopPlanOption(
	val title: String,
	val subtitle: String?,
)

@Composable
internal fun DesktopPlanOptionGroup(
	title: String,
	options: List<DesktopPlanOption>,
	selectedIndex: Int,
	onSelected: (Int) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = title,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
			fontWeight = FontWeight.Black,
		)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			options.forEachIndexed { index, option ->
				DesktopPlanOptionCard(
					option = option,
					isSelected = index == selectedIndex,
					modifier = Modifier.weight(1f),
					onClick = { onSelected(index) },
				)
			}
		}
	}
}

@Composable
internal fun DesktopPlanOptionCard(
	option: DesktopPlanOption,
	isSelected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = Color(0xFF20E65B)

	Button(
		onClick = onClick,
		modifier = modifier.height(72.dp),
		shape = RoundedCornerShape(14.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSelected) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
			contentColor = if (isSelected) accent else MaterialTheme.colorScheme.onSurface,
		),
		border = BorderStroke(1.dp, if (isSelected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = option.title,
				fontWeight = FontWeight.Bold,
				fontSize = 14.sp,
				textAlign = TextAlign.Center,
			)
			option.subtitle?.let {
				Text(
					text = it,
					color = if (isSelected) accent.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
					fontSize = 12.sp,
					textAlign = TextAlign.Center,
				)
			}
		}
	}
}
