package com.example.billionemotosappkt.screens.planos

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun PlanosScreen(
	onBack: () -> Unit,
	onComprarPlano: (String) -> Unit = {},
	modifier: Modifier = Modifier,
) {
	val plans = remember {
		listOf(
			SellingPlan(
				id = "start",
				name = "Billione Start",
				price = "R$ 490/mês",
				tag = "Entrada reduzida",
				highlight = "Comece pagando menos e entre no ritmo.",
				description = "Para quem quer começar rápido com valor leve e um caminho simples até a moto.",
				benefits = listOf("Parcelas mais leves", "Suporte para liberação", "Ideal para começar"),
				accent = Color(0xFF2EDB63),
			),
			SellingPlan(
				id = "conquista",
				name = "Billione Conquista",
				price = "R$ 660/mês",
				tag = "Mais escolhido",
				highlight = "O plano mais equilibrado para uso diário e compra no final.",
				description = "Combina preço, prazo e segurança para você rodar tranquilo com chance real de virar dono.",
				benefits = listOf("Compra no final", "Equilíbrio entre custo e prazo", "Melhor custo-benefício"),
				accent = Color(0xFF20D86B),
			),
			SellingPlan(
				id = "premium",
				name = "Billione Premium",
				price = "R$ 740/mês",
				tag = "Mais completo",
				highlight = "Mais presença, mais conforto e a sensação de plano top.",
				description = "Pensado para quem quer uma experiência mais premium e um pacote mais forte de benefícios.",
				benefits = listOf("Acabamento superior", "Mais conforto", "Perfil premium"),
				accent = Color(0xFF46E07C),
			),
		)
	}
	var selectedIndex by remember { mutableIntStateOf(1) }
	val selected = plans[selectedIndex]

	Scaffold(
		modifier = modifier.fillMaxSize().navigationBarsPadding(),
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = AppDimens.ScreenPadding, vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(onClick = onBack) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Voltar",
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}
				Spacer(modifier = Modifier.size(4.dp))
				Text(
					text = "NOSSOS PLANOS",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
		) {
			Box(
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
					),
			)

			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(horizontal = AppDimens.ScreenPadding, vertical = 8.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				HeroPlanCard(selected = selected)
				PlanStatsRow()

				Text(
					text = "Escolha sua melhor opção",
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.Black,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)

				plans.forEachIndexed { index, plan ->
					PlanSellingCard(
						plan = plan,
						selected = index == selectedIndex,
						onClick = { selectedIndex = index },
					)
				}

				SelectedPlanDetailCard(plan = selected)
				HowItWorksCard()
				FearReversalCard()

				ActionButton(
					text = "QUERO ESTE PLANO",
					onClick = { onComprarPlano(selected.id) },
					containerColor = selected.accent,
					pressedContainerColor = selected.accent.copy(alpha = 0.88f),
					contentColor = Color.Black,
					borderColor = selected.accent,
					pressedBorderColor = selected.accent,
					glowPulse = false,
				)

				Button(
					onClick = onBack,
					colors = ButtonDefaults.buttonColors(
						containerColor = Color.Transparent,
						contentColor = MaterialTheme.colorScheme.onSurface,
					),
					modifier = Modifier.fillMaxWidth(),
				) {
					Text("Voltar")
				}

				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}
}

@Composable
private fun HeroPlanCard(selected: SellingPlan) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f)),
		shape = RoundedCornerShape(28.dp),
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.24f), RoundedCornerShape(28.dp)),
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Icon(
					imageVector = Icons.Filled.Star,
					contentDescription = null,
					tint = selected.accent,
					modifier = Modifier.size(18.dp),
				)
				Text(
					text = selected.tag.uppercase(),
					style = MaterialTheme.typography.labelSmall,
					fontWeight = FontWeight.Black,
					color = selected.accent,
				)
			}

			Text(
				text = "Leve a moto com um plano que faz sentido para o bolso e para o objetivo.",
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
				lineHeight = 30.sp,
			)

			Text(
				text = selected.highlight,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				lineHeight = 22.sp,
			)

			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Badge(text = "Sem enrolação", accent = selected.accent)
				Badge(text = "Foco em compra", accent = selected.accent)
			}
		}
	}
}

@Composable
private fun PlanStatsRow() {
	Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
		StatPill(
			icon = Icons.Filled.Wallet,
			title = "Parcela clara",
			subtitle = "Sem surpresa",
			modifier = Modifier.weight(1f),
		)
		StatPill(
			icon = Icons.Filled.Security,
			title = "Mais confiança",
			subtitle = "Processo guiado",
			modifier = Modifier.weight(1f),
		)
		StatPill(
			icon = Icons.Filled.LocalOffer,
			title = "Oferta real",
			subtitle = "Plano pra comprar",
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun PlanSellingCard(
	plan: SellingPlan,
	selected: Boolean,
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = if (selected) plan.accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
		),
		modifier = Modifier
			.fillMaxWidth()
			.border(
				1.dp,
				if (selected) plan.accent.copy(alpha = 0.30f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
				RoundedCornerShape(24.dp),
			),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = plan.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Black,
						color = MaterialTheme.colorScheme.onSurface,
					)
					Text(
						text = plan.description,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				Text(
					text = plan.price,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Black,
					color = plan.accent,
				)
			}

			Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
				plan.benefits.forEach { benefit ->
					BenefitChip(text = benefit, accent = plan.accent)
				}
			}
		}
	}
}

@Composable
private fun SelectedPlanDetailCard(plan: SellingPlan) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f)),
		shape = RoundedCornerShape(24.dp),
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, plan.accent.copy(alpha = 0.24f), RoundedCornerShape(24.dp)),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Text(
				text = "O que você leva com ${plan.name}",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
			plan.benefits.forEach { benefit ->
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						imageVector = Icons.Filled.CheckCircle,
						contentDescription = null,
						tint = plan.accent,
						modifier = Modifier.size(18.dp),
					)
					Spacer(modifier = Modifier.size(10.dp))
					Text(
						text = benefit,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface,
					)
				}
			}
		}
	}
}

@Composable
private fun HowItWorksCard() {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.10f)),
		shape = RoundedCornerShape(24.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Text(
				text = "Como funciona",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
			StepRow(step = "1", title = "Escolha seu plano", subtitle = "Veja o valor e o que inclui.")
			StepRow(step = "2", title = "Fale com o time", subtitle = "A gente valida o melhor caminho.")
			StepRow(step = "3", title = "Comece a rodar", subtitle = "Seu contrato entra no fluxo certo.")
		}
	}
}

@Composable
private fun FearReversalCard() {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
		shape = RoundedCornerShape(24.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Text(
				text = "Quer começar sem medo?",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
			Text(
				text = "Mostramos o valor, o benefício e o caminho. Sem telas confusas, sem promessa vaga.",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun StepRow(step: String, title: String, subtitle: String) {
	Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
		Box(
			modifier = Modifier
				.size(28.dp)
				.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape),
			contentAlignment = Alignment.Center,
		) {
			Text(step, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
		}
		Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
			Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
			Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
		}
	}
}

@Composable
private fun Badge(text: String, accent: Color) {
	Box(
		modifier = Modifier
			.background(accent.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
			.border(1.dp, accent.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
			.padding(horizontal = 12.dp, vertical = 6.dp),
	) {
		Text(text = text, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}

@Composable
private fun BenefitChip(text: String, accent: Color) {
	Box(
		modifier = Modifier
			.background(accent.copy(alpha = 0.10f), RoundedCornerShape(999.dp))
			.padding(horizontal = 10.dp, vertical = 6.dp),
	) {
		Text(text = text, color = accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
	}
}

@Composable
private fun StatPill(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	title: String,
	subtitle: String,
	modifier: Modifier = Modifier,
) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)),
		shape = RoundedCornerShape(20.dp),
		modifier = modifier.height(96.dp),
	) {
		Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
			Text(title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
			Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
		}
	}
}

private data class SellingPlan(
	val id: String,
	val name: String,
	val price: String,
	val tag: String,
	val highlight: String,
	val description: String,
	val benefits: List<String>,
	val accent: Color,
)

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF050806)
@Composable
private fun PlanosScreenPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		PlanosScreen(onBack = {})
	}
}
