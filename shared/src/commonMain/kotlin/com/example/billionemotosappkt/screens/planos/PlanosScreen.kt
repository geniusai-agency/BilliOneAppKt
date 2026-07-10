package com.example.billionemotosappkt.screens.planos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class SellingPlan(
	val id: String,
	val name: String,
	val price: String,
	val tag: String,
	val isPopular: Boolean = false,
	val highlight: String,
	val description: String,
	val benefits: List<String>,
	val inclusions: List<String>,
	val accent: Color,
)

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
				tag = "ENTRADA REDUZIDA",
				isPopular = false,
				highlight = "Comece pagando menos e entre no ritmo sem pesadelo no orçamento.",
				description = "Ideal para quem quer valor de parcela mais leve e aprovação facilitada para rodar rápido.",
				benefits = listOf("Parcelas mais leves", "Sem burocracia", "Ideal para iniciar"),
				inclusions = listOf(
					"Manutenção preventiva de rotina inclusa",
					"Rastreamento IoT 24h via GPS",
					"Suporte prioritário via WhatsApp e App",
					"Opção de compra programada no final",
				),
				accent = Color(0xFF2EDB63),
			),
			SellingPlan(
				id = "conquista",
				name = "Billione Conquista",
				price = "R$ 660/mês",
				tag = "MAIS ESCOLHIDO",
				isPopular = true,
				highlight = "O plano mais equilibrado para uso diário com chance real de virar dono.",
				description = "Combina preço justo, prazo e máxima segurança para você rodar tranquilo e conquistar sua moto.",
				benefits = listOf("Compra no final", "Melhor Custo-Benefício", "Seguro & IoT Inclusos"),
				inclusions = listOf(
					"Plano com transferência de propriedade em 36M",
					"Revisões de quilometragem e troca de óleo",
					"Rastreador com bloqueio remoto de ignição",
					"Moto substituta em caso de manutenção prolongada",
				),
				accent = Color(0xFF20D86B),
			),
			SellingPlan(
				id = "premium",
				name = "Billione Premium",
				price = "R$ 740/mês",
				tag = "COMPLETO VIP",
				isPopular = false,
				highlight = "Máximo conforto, prioridade absoluta no atendimento e frota topo de linha.",
				description = "Para quem busca a melhor experiência, sem preocupação com manutenção ou burocracia.",
				benefits = listOf("Atendimento VIP 24h", "Peças Top de Linha", "Prioridade na Frota"),
				inclusions = listOf(
					"Pacote completo de revisões e desgaste natural",
					"Rastreamento com suporte presencial em sinistros",
					"Atendimento VIP sem fila de espera",
					"Transferência garantida ao fim do contrato",
				),
				accent = Color(0xFF46E07C),
			),
		)
	}
	var selectedIndex by remember { mutableIntStateOf(1) }
	val selected = plans[selectedIndex]

	Scaffold(
		modifier = modifier.fillMaxSize().safeDrawingPadding(),
		containerColor = Color(0xFF060907),
		topBar = {
			HeaderBar(onBack = onBack)
		},
		bottomBar = {
			StickyBottomBar(
				selectedPlan = selected,
				onConfirm = { onComprarPlano(selected.id) },
			)
		},
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
		) {
			// Fundo com gradiente sutil dark neon
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(
						Brush.verticalGradient(
							colors = listOf(
								Color(0xFF090E0B),
								Color(0xFF060907),
								Color(0xFF040605),
							),
						),
					),
			)

			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(horizontal = 16.dp, vertical = 10.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				// Hero Header Banner
				HeroBannerCard(selectedPlan = selected)

				// Três pilares de confiança
				TrustPillarsRow()

				// Título da seção de seleção
				Text(
					text = "SELECIONE SEU PLANO",
					style = MaterialTheme.typography.labelMedium.copy(
						fontSize = 12.sp,
						letterSpacing = 1.5.sp,
					),
					fontWeight = FontWeight.Black,
					color = Color(0xFF71F58D),
					modifier = Modifier.padding(top = 4.dp, start = 4.dp),
				)

				// Cards dos Planos
				plans.forEachIndexed { index, plan ->
					ModernPlanCard(
						plan = plan,
						selected = index == selectedIndex,
						onClick = { selectedIndex = index },
					)
				}

				// O que está incluso no plano selecionado
				InclusionsCard(plan = selected)

				// Como Funciona em 3 Passos
				HowItWorksCard()

				// Seção FAQ / Reversão de Dúvidas
				FaqAccordionCard()

				Spacer(modifier = Modifier.height(16.dp))
			}
		}
	}
}

@Composable
private fun HeaderBar(onBack: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color(0xFF060907).copy(alpha = 0.95f))
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.clip(CircleShape)
					.background(Color(0x1AFFFFFF))
					.border(1.dp, Color(0x33FFFFFF), CircleShape)
					.clickable(onClick = onBack),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Voltar",
					tint = Color.White,
					modifier = Modifier.size(20.dp),
				)
			}

			Column {
				Text(
					text = "NOSSOS PLANOS",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Black,
					color = Color.White,
					letterSpacing = 0.5.sp,
				)
				Text(
					text = "Escolha e rode sem burocracia",
					style = MaterialTheme.typography.bodySmall,
					color = Color(0xFFA7B0AA),
					fontSize = 11.sp,
				)
			}
		}

		Box(
			modifier = Modifier
				.clip(RoundedCornerShape(999.dp))
				.background(Color(0x1F2EDB63))
				.border(1.dp, Color(0x402EDB63), RoundedCornerShape(999.dp))
				.padding(horizontal = 10.dp, vertical = 5.dp),
		) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
				Icon(
					imageVector = Icons.Filled.Verified,
					contentDescription = null,
					tint = Color(0xFF2EDB63),
					modifier = Modifier.size(14.dp),
				)
				Text(
					text = "36M COMPRA",
					fontSize = 10.sp,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF2EDB63),
				)
			}
		}
	}
}

@Composable
private fun HeroBannerCard(selectedPlan: SellingPlan) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1712)),
		modifier = Modifier
			.fillMaxWidth()
			.border(
				width = 1.dp,
				brush = Brush.horizontalGradient(
					colors = listOf(
						Color(0xFF2EDB63).copy(alpha = 0.5f),
						Color(0x1F2EDB63),
						Color(0x05FFFFFF),
					),
				),
				shape = RoundedCornerShape(24.dp),
			),
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Box(
					modifier = Modifier
						.clip(RoundedCornerShape(999.dp))
						.background(Color(0xFF2EDB63).copy(alpha = 0.15f))
						.border(1.dp, Color(0xFF2EDB63).copy(alpha = 0.35f), RoundedCornerShape(999.dp))
						.padding(horizontal = 10.dp, vertical = 4.dp),
				) {
					Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
						Icon(
							imageVector = Icons.Filled.Star,
							contentDescription = null,
							tint = Color(0xFF2EDB63),
							modifier = Modifier.size(12.dp),
						)
						Text(
							text = selectedPlan.tag,
							style = MaterialTheme.typography.labelSmall,
							fontWeight = FontWeight.Black,
							color = Color(0xFF2EDB63),
							fontSize = 10.sp,
						)
					}
				}
			}

			Text(
				text = "Sua moto 0km com parcela transparente.",
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Black,
				color = Color.White,
				lineHeight = 28.sp,
			)

			Text(
				text = selectedPlan.highlight,
				style = MaterialTheme.typography.bodyMedium,
				color = Color(0xFFB0BCB5),
				lineHeight = 20.sp,
			)
		}
	}
}

@Composable
private fun TrustPillarsRow() {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		TrustPill(
			icon = Icons.Filled.Wallet,
			title = "Parcela Clara",
			subtitle = "Sem surpresa",
			modifier = Modifier.weight(1f),
		)
		TrustPill(
			icon = Icons.Filled.Shield,
			title = "Rastreio IoT",
			subtitle = "Proteção 24h",
			modifier = Modifier.weight(1f),
		)
		TrustPill(
			icon = Icons.Filled.Key,
			title = "Vire Dono",
			subtitle = "Plano 36M",
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun TrustPill(
	icon: ImageVector,
	title: String,
	subtitle: String,
	modifier: Modifier = Modifier,
) {
	Card(
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1511)),
		modifier = modifier
			.border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp)),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = Color(0xFF2EDB63),
				modifier = Modifier.size(20.dp),
			)
			Text(
				text = title,
				fontWeight = FontWeight.Bold,
				color = Color.White,
				fontSize = 12.sp,
			)
			Text(
				text = subtitle,
				color = Color(0xFF8A9690),
				fontSize = 10.sp,
			)
		}
	}
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ModernPlanCard(
	plan: SellingPlan,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val borderColor by animateColorAsState(
		targetValue = if (selected) plan.accent else Color(0x22FFFFFF),
		animationSpec = tween(300),
	)

	val containerColor by animateColorAsState(
		targetValue = if (selected) Color(0xFF132017) else Color(0xFF0C120E),
		animationSpec = tween(300),
	)

	val priceValue = plan.price.substringBefore("/").trim()
	val pricePeriod = plan.price.substringAfter("/", "").trim()

	Card(
		onClick = onClick,
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = containerColor),
		modifier = Modifier
			.fillMaxWidth()
			.border(
				width = if (selected) 2.dp else 1.dp,
				color = borderColor,
				shape = RoundedCornerShape(22.dp),
			),
	) {
		Column {
			// Faixa de destaque para o plano popular
			if (plan.isPopular) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							Brush.horizontalGradient(
								colors = listOf(
									Color(0xFF2EDB63),
									Color(0xFF1B9E44),
								),
							),
						)
						.padding(vertical = 4.dp),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = "🔥 MAIS VENDIDO PELOS PILOTOS",
						color = Color.Black,
						fontSize = 10.sp,
						fontWeight = FontWeight.Black,
						letterSpacing = 1.sp,
					)
				}
			}

			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.Top,
				) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(10.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.weight(1f),
					) {
						// Custom Radio Selector
						Box(
							modifier = Modifier
								.size(24.dp)
								.clip(CircleShape)
								.background(if (selected) plan.accent.copy(alpha = 0.2f) else Color.Transparent)
								.border(
									2.dp,
									if (selected) plan.accent else Color(0x66FFFFFF),
									CircleShape,
								),
							contentAlignment = Alignment.Center,
						) {
							if (selected) {
								Box(
									modifier = Modifier
										.size(10.dp)
										.clip(CircleShape)
										.background(plan.accent),
								)
							}
						}

						Column {
							Text(
								text = plan.name,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Black,
								color = Color.White,
							)
							Text(
								text = plan.tag,
								fontSize = 10.sp,
								fontWeight = FontWeight.Bold,
								color = plan.accent,
							)
						}
					}

					// Preço
					Column(horizontalAlignment = Alignment.End) {
						Text(
							text = priceValue,
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Black,
							color = plan.accent,
						)
						if (pricePeriod.isNotBlank()) {
							Text(
								text = "/$pricePeriod",
								fontSize = 11.sp,
								fontWeight = FontWeight.Medium,
								color = Color(0xFF8A9690),
							)
						}
					}
				}

				Text(
					text = plan.description,
					style = MaterialTheme.typography.bodySmall,
					color = Color(0xFFA7B0AA),
					lineHeight = 18.sp,
				)

				// Benefits Chips
				FlowRow(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(6.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp),
				) {
					plan.benefits.forEach { benefit ->
						Box(
							modifier = Modifier
								.clip(RoundedCornerShape(999.dp))
								.background(plan.accent.copy(alpha = 0.12f))
								.border(1.dp, plan.accent.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
								.padding(horizontal = 10.dp, vertical = 4.dp),
						) {
							Text(
								text = benefit,
								color = plan.accent,
								fontSize = 11.sp,
								fontWeight = FontWeight.SemiBold,
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun InclusionsCard(plan: SellingPlan) {
	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1410)),
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, plan.accent.copy(alpha = 0.3f), RoundedCornerShape(22.dp)),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Icon(
					imageVector = Icons.Filled.Verified,
					contentDescription = null,
					tint = plan.accent,
					modifier = Modifier.size(20.dp),
				)
				Text(
					text = "INCLUSO NO SEU ${plan.name.uppercase()}",
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.Black,
					color = Color.White,
					letterSpacing = 0.5.sp,
				)
			}

			plan.inclusions.forEach { item ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(10.dp),
				) {
					Icon(
						imageVector = Icons.Filled.CheckCircle,
						contentDescription = null,
						tint = plan.accent,
						modifier = Modifier.size(18.dp),
					)
					Text(
						text = item,
						style = MaterialTheme.typography.bodyMedium,
						color = Color(0xFFD0D7D3),
						fontSize = 13.sp,
					)
				}
			}
		}
	}
}

@Composable
private fun HowItWorksCard() {
	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F0C)),
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp)),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Text(
				text = "COMO FUNCIONA A CONTRATAÇÃO",
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Black,
				color = Color.White,
				letterSpacing = 0.5.sp,
			)

			StepItem(
				number = "1",
				title = "Escolha seu plano",
				description = "Selecione o plano que cabe no seu bolso nesta tela.",
			)

			StepItem(
				number = "2",
				title = "Cadastre seus dados",
				description = "Envie foto da CNH/RG para análise simplificada.",
			)

			StepItem(
				number = "3",
				title = "Assine e retire sua moto",
				description = "Assinatura digital direta e agendamento da retirada.",
			)
		}
	}
}

@Composable
private fun StepItem(number: String, title: String, description: String) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.Top,
	) {
		Box(
			modifier = Modifier
				.size(28.dp)
				.clip(CircleShape)
				.background(Color(0xFF2EDB63).copy(alpha = 0.2f))
				.border(1.dp, Color(0xFF2EDB63), CircleShape),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = number,
				fontWeight = FontWeight.Black,
				color = Color(0xFF2EDB63),
				fontSize = 13.sp,
			)
		}

		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(
				text = title,
				fontWeight = FontWeight.Bold,
				color = Color.White,
				fontSize = 13.sp,
			)
			Text(
				text = description,
				color = Color(0xFFA7B0AA),
				fontSize = 12.sp,
				lineHeight = 16.sp,
			)
		}
	}
}

@Composable
private fun FaqAccordionCard() {
	var expandedIndex by remember { mutableIntStateOf(-1) }

	val faqs = remember {
		listOf(
			"Preciso ter nome limpo no SPC/Serasa?" to "Não exigimos aprovação bancária tradicional. Realizamos uma análise própria e flexível focada no seu perfil de uso.",
			"A moto realmente vira minha no final?" to "Sim! Ao concluir as 36 parcelas do contrato, iniciamos o processo de transferência definitiva de propriedade para seu nome.",
			"Como funciona a manutenção inclusa?" to "Revisões preventivas e peças de desgaste natural são cobertas pela nossa rede credenciada de oficinas parceiras.",
		)
	}

	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F0C)),
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp)),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = "DÚVIDAS FREQUENTES",
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Black,
				color = Color.White,
				letterSpacing = 0.5.sp,
			)

			faqs.forEachIndexed { index, (question, answer) ->
				val expanded = expandedIndex == index
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(12.dp))
						.background(Color(0x10FFFFFF))
						.clickable {
							expandedIndex = if (expanded) -1 else index
						}
						.padding(12.dp),
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Text(
							text = question,
							fontWeight = FontWeight.Bold,
							color = Color.White,
							fontSize = 13.sp,
							modifier = Modifier.weight(1f),
						)
						Icon(
							imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
							contentDescription = null,
							tint = Color(0xFF2EDB63),
						)
					}

					AnimatedVisibility(
						visible = expanded,
						enter = fadeIn() + expandVertically(),
						exit = fadeOut() + shrinkVertically(),
					) {
						Text(
							text = answer,
							color = Color(0xFFA7B0AA),
							fontSize = 12.sp,
							lineHeight = 17.sp,
							modifier = Modifier.padding(top = 8.dp),
						)
					}
				}
			}
		}
	}
}

@Composable
private fun StickyBottomBar(
	selectedPlan: SellingPlan,
	onConfirm: () -> Unit,
) {
	Surface(
		color = Color(0xFF090E0B),
		tonalElevation = 8.dp,
		shadowElevation = 12.dp,
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
		shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 18.dp, vertical = 14.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = selectedPlan.name,
					fontWeight = FontWeight.Black,
					color = Color.White,
					fontSize = 14.sp,
				)
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = selectedPlan.price,
						fontWeight = FontWeight.Black,
						color = selectedPlan.accent,
						fontSize = 16.sp,
					)
				}
			}

			Spacer(modifier = Modifier.width(12.dp))

			Button(
				onClick = onConfirm,
				colors = ButtonDefaults.buttonColors(
					containerColor = selectedPlan.accent,
					contentColor = Color.Black,
				),
				shape = RoundedCornerShape(16.dp),
				modifier = Modifier.height(48.dp),
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(6.dp),
				) {
					Text(
						text = "QUERO ESTE PLANO",
						fontWeight = FontWeight.Black,
						fontSize = 13.sp,
						letterSpacing = 0.5.sp,
					)
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowForward,
						contentDescription = null,
						modifier = Modifier.size(16.dp),
					)
				}
			}
		}
	}
}
