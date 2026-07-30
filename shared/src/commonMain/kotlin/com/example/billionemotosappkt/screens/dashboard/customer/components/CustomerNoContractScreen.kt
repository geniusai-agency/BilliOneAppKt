package com.example.billionemotosappkt.screens.dashboard.customer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardData
import com.example.billionemotosappkt.shared.api.ContratoStatus

private val EmeraldNeon = Color(0xFF20D86B)
private val EmeraldGlow = Color(0xFF059669)
private val AmberGold = Color(0xFFF59E0B)
private val CrimsonRed = Color(0xFFEF4444)
private val BlueAccent = Color(0xFF3B82F6)
private val DarkBg = Color(0xFF090F0C)
private val GlassCard = Color(0xFF101914)
private val SubGlass = Color(0xFF16231C)

@Composable
fun CustomerNoContractScreen(
	authState: AuthUiState,
	dashboardData: CustomerDashboardData,
	onPrimaryAction: () -> Unit,
	onPlanosClick: () -> Unit,
	onSupportClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val currentStep = remember(authState) {
		when {
			authState.contractStatus == ContratoStatus.PENDENTE_ASSINATURA || authState.journeyScreen == "contract-signature" -> 4
			authState.journeyState == "WAITING_ANALYSIS" || authState.journeyScreen == "analysis-pending" -> 3
			authState.journeyScreen == "analysis-rejected" -> 3
			authState.clienteId != null -> 2
			else -> 1
		}
	}

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		// 1. Card de Solicitacao / Pedido Side-by-Side com Feedbacks
		OrderFeedbackCard(
			authState = authState,
			currentStep = currentStep,
			onPrimaryAction = onPrimaryAction,
			onPlanosClick = onPlanosClick,
		)

		// 3. Linha do Tempo Visual do Pedido
		OrderStepperTrack(currentStep = currentStep)

		// 4. Vitrine de Planos em Destaque
		OnboardingPlansSection(onSelectPlan = onPlanosClick)

		// 5. Grid de Beneficios Exclusivos
		OnboardingBenefitsSection()

		// 6. FAQ Sanfonado
		OnboardingFaqSection()

		// 7. Callout de Atendimento / Suporte
		OnboardingSupportFooter(onSupportClick = onSupportClick)
	}
}

@Composable
private fun OnboardingHeaderBar(
	userName: String,
	userEmail: String?,
	currentStep: Int,
) {
	Card(
		shape = RoundedCornerShape(26.dp),
		colors = CardDefaults.cardColors(containerColor = GlassCard),
		border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.22f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					Brush.horizontalGradient(
						colors = listOf(
							EmeraldNeon.copy(alpha = 0.12f),
							Color.Transparent,
						)
					)
				)
				.padding(18.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					modifier = Modifier.weight(1f),
				) {
					Box(
						modifier = Modifier
							.size(46.dp)
							.background(EmeraldNeon.copy(alpha = 0.15f), CircleShape)
							.border(1.5.dp, EmeraldNeon, CircleShape),
						contentAlignment = Alignment.Center,
					) {
						Icon(
							imageVector = Icons.Default.Person,
							contentDescription = null,
							tint = EmeraldNeon,
							modifier = Modifier.size(22.dp),
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
						Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
							Text(
								text = "Olá, $userName",
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold,
								color = Color.White,
							)
							Text(text = "✨", fontSize = 14.sp)
						}
						Text(
							text = userEmail ?: "BilliOne Motos · Minha Conta",
							style = MaterialTheme.typography.bodySmall,
							color = Color.White.copy(alpha = 0.60f),
						)
					}
				}

				Surface(
					color = EmeraldNeon.copy(alpha = 0.14f),
					shape = RoundedCornerShape(999.dp),
					border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.30f)),
				) {
					Row(
						modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(5.dp),
					) {
						Box(
							modifier = Modifier
								.size(6.dp)
								.background(EmeraldNeon, CircleShape)
						)
						Text(
							text = "Sem Contrato Ativo",
							fontSize = 10.sp,
							fontWeight = FontWeight.Bold,
							color = EmeraldNeon,
						)
					}
				}
			}
		}
	}
}

@Composable
private fun OrderFeedbackCard(
	authState: AuthUiState,
	currentStep: Int,
	onPrimaryAction: () -> Unit,
	onPlanosClick: () -> Unit,
) {
	val pendingSignature = authState.contractStatus == ContratoStatus.PENDENTE_ASSINATURA ||
			authState.journeyScreen == "contract-signature"
	val pendingAnalysis = authState.journeyScreen == "analysis-pending" || authState.journeyState == "WAITING_ANALYSIS"
	val isRejected = authState.journeyScreen == "analysis-rejected"

	val themeColor = when {
		pendingSignature -> EmeraldNeon
		pendingAnalysis -> AmberGold
		isRejected -> CrimsonRed
		else -> EmeraldNeon
	}

	val statusLabel = when {
		pendingSignature -> "APROVADO · CONTRATO DISPONÍVEL"
		pendingAnalysis -> "ANÁLISE EM ANDAMENTO"
		isRejected -> "AJUSTE NECESSÁRIO"
		else -> "PRONTO PARA SOLICITAR"
	}

	val titleText = when {
		pendingSignature -> "Seu contrato está pronto para assinatura!"
		pendingAnalysis -> "Sua solicitação de moto está em análise"
		isRejected -> "Sua solicitação requer ajuste de documentos"
		else -> "Solicite sua moto 0km em poucos cliques"
	}

	val feedbackMessage = when {
		pendingSignature -> "Parabéns! Sua análise de crédito foi concluída com sucesso. Assine o contrato digitalmente agora para agendar a retirada da moto."
		pendingAnalysis -> "Recebemos seus dados! Nossa equipe técnica está analisando seus documentos. O tempo médio de resposta é de até 24 horas úteis."
		isRejected -> "Identificamos uma inconsistência na imagem da CNH ou comprovante. Por favor, reenvie os documentos legíveis para aprovarmos."
		else -> "Escolha seu plano preferido, envie seus documentos e tenha sua moto pronta para uso com seguro total e manutenção inclusa."
	}

	val buttonLabel = when {
		pendingSignature -> "ASSINAR CONTRATO DIGITAL"
		pendingAnalysis -> "VER STATUS DA ANÁLISE"
		isRejected -> "REENVIAR DOCUMENTOS"
		else -> "ESCOLHER PLANO E PEDIR MOTO"
	}

	Card(
		shape = RoundedCornerShape(26.dp),
		colors = CardDefaults.cardColors(containerColor = GlassCard),
		border = BorderStroke(1.dp, themeColor.copy(alpha = 0.35f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			// Status Tag & Feedback Badge Header
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Surface(
					color = themeColor.copy(alpha = 0.16f),
					shape = RoundedCornerShape(999.dp),
					border = BorderStroke(1.dp, themeColor.copy(alpha = 0.40f)),
				) {
					Row(
						modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(6.dp),
					) {
						Box(modifier = Modifier.size(7.dp).background(themeColor, CircleShape))
						Text(
							text = statusLabel,
							fontSize = 11.sp,
							fontWeight = FontWeight.Bold,
							color = themeColor,
						)
					}
				}

				if (pendingAnalysis) {
					Surface(
						color = AmberGold.copy(alpha = 0.12f),
						shape = RoundedCornerShape(12.dp),
					) {
						Row(
							modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(4.dp),
						) {
							Icon(
								imageVector = Icons.Default.HourglassTop,
								contentDescription = null,
								tint = AmberGold,
								modifier = Modifier.size(13.dp),
							)
							Text(
								text = "Previsão ~24h",
								fontSize = 11.sp,
								color = AmberGold,
								fontWeight = FontWeight.SemiBold,
							)
						}
					}
				}
			}

			// Bloco Principal de Feedback do Pedido
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(14.dp),
				verticalAlignment = Alignment.Top,
			) {
				Box(
					modifier = Modifier
						.size(44.dp)
						.background(themeColor.copy(alpha = 0.12f), CircleShape)
						.border(1.dp, themeColor.copy(alpha = 0.30f), CircleShape),
					contentAlignment = Alignment.Center,
				) {
					Icon(
						imageVector = when {
							pendingSignature -> Icons.Default.AssignmentTurnedIn
							pendingAnalysis -> Icons.Default.HourglassTop
							isRejected -> Icons.Default.Info
							else -> Icons.Default.DirectionsBike
						},
						contentDescription = null,
						tint = themeColor,
						modifier = Modifier.size(22.dp),
					)
				}

				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(4.dp),
				) {
					Text(
						text = titleText,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						color = Color.White,
					)
					Text(
						text = feedbackMessage,
						style = MaterialTheme.typography.bodySmall,
						color = Color.White.copy(alpha = 0.72f),
						lineHeight = 18.sp,
					)
				}
			}

			// Resumo de Pedido (Card Interno Side-by-Side)
			Surface(
				shape = RoundedCornerShape(18.dp),
				color = SubGlass,
				border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
				modifier = Modifier.fillMaxWidth(),
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(14.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
						Text(
							text = "STATUS DO PEDIDO",
							fontSize = 10.sp,
							fontWeight = FontWeight.Bold,
							color = Color.White.copy(alpha = 0.45f),
						)
						Text(
							text = if (currentStep >= 2) "Solicitação Cadastrada" else "Nenhum pedido ativo",
							fontSize = 13.sp,
							fontWeight = FontWeight.SemiBold,
							color = Color.White,
						)
					}

					Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						StatusCheckChip(
							label = "CNH",
							checked = currentStep >= 2,
						)
						StatusCheckChip(
							label = "Doc",
							checked = currentStep >= 2,
						)
						StatusCheckChip(
							label = "Crédito",
							checked = currentStep >= 3,
						)
					}
				}
			}

			// Botao de Acao com Destaque
			Button(
				onClick = {
					if (pendingSignature || isRejected || pendingAnalysis) {
						onPrimaryAction()
					} else {
						onPlanosClick()
					}
				},
				colors = ButtonDefaults.buttonColors(
					containerColor = themeColor,
					contentColor = Color.Black,
				),
				shape = RoundedCornerShape(16.dp),
				modifier = Modifier
					.fillMaxWidth()
					.height(52.dp),
			) {
				Text(
					text = buttonLabel,
					fontWeight = FontWeight.Black,
					fontSize = 13.sp,
				)
				Spacer(modifier = Modifier.width(8.dp))
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowForward,
					contentDescription = null,
					modifier = Modifier.size(16.dp),
				)
			}
		}
	}
}

@Composable
private fun StatusCheckChip(label: String, checked: Boolean) {
	Surface(
		color = if (checked) EmeraldNeon.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
		shape = RoundedCornerShape(8.dp),
		border = BorderStroke(1.dp, if (checked) EmeraldNeon.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.10f)),
	) {
		Row(
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Icon(
				imageVector = Icons.Default.CheckCircle,
				contentDescription = null,
				tint = if (checked) EmeraldNeon else Color.White.copy(alpha = 0.30f),
				modifier = Modifier.size(12.dp),
			)
			Text(
				text = label,
				fontSize = 10.sp,
				fontWeight = FontWeight.Bold,
				color = if (checked) EmeraldNeon else Color.White.copy(alpha = 0.40f),
			)
		}
	}
}

@Composable
private fun OrderStepperTrack(currentStep: Int) {
	val steps = listOf(
		"Cadastro",
		"Plano & Moto",
		"Análise",
		"Assinatura",
		"Retirada",
	)

	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = GlassCard),
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Text(
				text = "Linha do Tempo do Pedido",
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold,
				color = Color.White.copy(alpha = 0.90f),
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				steps.forEachIndexed { index, stepName ->
					val stepNum = index + 1
					val isCompleted = stepNum < currentStep
					val isCurrent = stepNum == currentStep

					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(6.dp),
						modifier = Modifier.weight(1f),
					) {
						Box(
							modifier = Modifier
								.size(34.dp)
								.background(
									color = when {
										isCompleted -> EmeraldNeon
										isCurrent -> EmeraldNeon.copy(alpha = 0.22f)
										else -> Color.White.copy(alpha = 0.05f)
									},
									shape = CircleShape,
								)
								.border(
									width = if (isCurrent) 2.dp else 1.dp,
									color = when {
										isCompleted || isCurrent -> EmeraldNeon
										else -> Color.White.copy(alpha = 0.12f)
									},
									shape = CircleShape,
								),
							contentAlignment = Alignment.Center,
						) {
							if (isCompleted) {
								Icon(
									imageVector = Icons.Default.CheckCircle,
									contentDescription = null,
									tint = Color.Black,
									modifier = Modifier.size(18.dp),
								)
							} else {
								Text(
									text = stepNum.toString(),
									fontSize = 12.sp,
									fontWeight = FontWeight.Bold,
									color = if (isCurrent) EmeraldNeon else Color.White.copy(alpha = 0.35f),
								)
							}
						}

						Text(
							text = stepName,
							fontSize = 10.sp,
							fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
							color = if (isCurrent) EmeraldNeon else if (isCompleted) Color.White else Color.White.copy(alpha = 0.40f),
							textAlign = TextAlign.Center,
							maxLines = 1,
						)
					}
				}
			}
		}
	}
}

@Composable
private fun OnboardingPlansSection(
	onSelectPlan: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "Escolha seu Plano Ideal",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				color = Color.White,
			)
			Text(
				text = "Ver todos ➔",
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				color = EmeraldNeon,
				modifier = Modifier.clickable { onSelectPlan() },
			)
		}

		PlanCardItem(
			title = "Delivery Pro",
			badge = "MAIS POPULAR",
			price = "R$ 580,00 /mês",
			description = "Alta rodagem e máxima produtividade para entregadores de aplicativo.",
			features = listOf("Manutenção preventiva 100% inclusa", "Seguro completo contra roubo e furto", "Moto reserva imediata em caso de oficina"),
			accentColor = EmeraldNeon,
			onSelect = onSelectPlan,
		)

		PlanCardItem(
			title = "Urbano Econômico",
			badge = "ECONÔMICO",
			price = "R$ 450,00 /mês",
			description = "Economia máxima para deslocamentos diários entre casa e trabalho.",
			features = listOf("Consumo estimado em até 50 km/L", "Trocas de óleo periódicas inclusas", "Suporte rápido via WhatsApp"),
			accentColor = BlueAccent,
			onSelect = onSelectPlan,
		)
	}
}

@Composable
private fun PlanCardItem(
	title: String,
	badge: String,
	price: String,
	description: String,
	features: List<String>,
	accentColor: Color,
	onSelect: () -> Unit,
) {
	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = GlassCard),
		border = BorderStroke(1.dp, accentColor.copy(alpha = 0.28f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = title,
					fontSize = 17.sp,
					fontWeight = FontWeight.Bold,
					color = Color.White,
				)
				Surface(
					color = accentColor.copy(alpha = 0.16f),
					shape = RoundedCornerShape(999.dp),
					border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
				) {
					Text(
						text = badge,
						fontSize = 9.sp,
						fontWeight = FontWeight.Black,
						color = accentColor,
						modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
					)
				}
			}

			Text(
				text = price,
				fontSize = 20.sp,
				fontWeight = FontWeight.Black,
				color = accentColor,
			)

			Text(
				text = description,
				fontSize = 12.sp,
				color = Color.White.copy(alpha = 0.65f),
				lineHeight = 16.sp,
			)

			Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
				features.forEach { feature ->
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						Icon(
							imageVector = Icons.Default.CheckCircle,
							contentDescription = null,
							tint = accentColor,
							modifier = Modifier.size(15.dp),
						)
						Text(
							text = feature,
							fontSize = 11.sp,
							color = Color.White.copy(alpha = 0.85f),
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(4.dp))

			OutlinedButton(
				onClick = onSelect,
				border = BorderStroke(1.dp, accentColor.copy(alpha = 0.50f)),
				shape = RoundedCornerShape(14.dp),
				colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
				modifier = Modifier
					.fillMaxWidth()
					.height(46.dp),
			) {
				Text("QUERO ESTE PLANO ➔", fontWeight = FontWeight.Bold, fontSize = 12.sp)
			}
		}
	}
}

@Composable
private fun OnboardingBenefitsSection() {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = "Vantagens Exclusivas",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold,
			color = Color.White,
		)

		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp),
			modifier = Modifier.fillMaxWidth(),
		) {
			BenefitTile(
				icon = Icons.Default.Build,
				title = "Manutenção Inclusa",
				subtitle = "Revisões e peças por nossa conta.",
				modifier = Modifier.weight(1f),
			)
			BenefitTile(
				icon = Icons.Default.Shield,
				title = "Seguro Total",
				subtitle = "Proteção total roubo/furto.",
				modifier = Modifier.weight(1f),
			)
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp),
			modifier = Modifier.fillMaxWidth(),
		) {
			BenefitTile(
				icon = Icons.Default.VerifiedUser,
				title = "Sem Burocracia",
				subtitle = "Modelo pensado no autônomo.",
				modifier = Modifier.weight(1f),
			)
			BenefitTile(
				icon = Icons.Default.Speed,
				title = "Aprovação Rápida",
				subtitle = "Análise ágil em poucas horas.",
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun BenefitTile(
	icon: ImageVector,
	title: String,
	subtitle: String,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier,
		shape = RoundedCornerShape(18.dp),
		color = GlassCard,
		border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
	) {
		Column(
			modifier = Modifier.padding(14.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = EmeraldNeon,
				modifier = Modifier.size(20.dp),
			)
			Text(
				text = title,
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold,
				color = Color.White,
			)
			Text(
				text = subtitle,
				fontSize = 10.sp,
				color = Color.White.copy(alpha = 0.55f),
				lineHeight = 14.sp,
			)
		}
	}
}

@Composable
private fun OnboardingFaqSection() {
	var expandedIndex by remember { mutableStateOf<Int?>(null) }

	val faqs = listOf(
		"Quanto tempo demora a análise de crédito?" to "Nossa equipe analisa os documentos enviados em um prazo médio de 2 a 24 horas úteis. Assim que aprovado, seu contrato fica pronto para assinatura digital.",
		"Preciso ter nome sem restrição no SPC/Serasa?" to "Não! A BilliOne Motos possui modelo flexível de aprovação pensado em trabalhadores autônomos e entregadores de aplicativos.",
		"Como funciona a caução e a primeira parcela?" to "A caução e a taxa inicial são pagas apenas após a aprovação do seu contrato, antes de retirar a moto no ponto parceiro.",
		"Quando posso retirar a moto?" to "Assim que você assinar o contrato digital no aplicativo e o pagamento for confirmado, você agenda o dia e horário para retirar a moto zerada.",
	)

	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = "Dúvidas Frequentes",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold,
			color = Color.White,
		)

		faqs.forEachIndexed { index, (question, answer) ->
			val isExpanded = expandedIndex == index
			Card(
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(containerColor = GlassCard),
				border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						expandedIndex = if (isExpanded) null else index
					},
			) {
				Column(modifier = Modifier.padding(14.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Row(
							modifier = Modifier.weight(1f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(8.dp),
						) {
							Icon(
								imageVector = Icons.Default.HelpOutline,
								contentDescription = null,
								tint = EmeraldNeon,
								modifier = Modifier.size(16.dp),
							)
							Text(
								text = question,
								fontSize = 12.sp,
								fontWeight = FontWeight.SemiBold,
								color = Color.White,
							)
						}
						Icon(
							imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
							contentDescription = null,
							tint = Color.White.copy(alpha = 0.5f),
						)
					}

					AnimatedVisibility(
						visible = isExpanded,
						enter = fadeIn() + expandVertically(),
						exit = fadeOut() + shrinkVertically(),
					) {
						Column {
							Spacer(modifier = Modifier.height(10.dp))
							HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
							Spacer(modifier = Modifier.height(10.dp))
							Text(
								text = answer,
								fontSize = 11.sp,
								color = Color.White.copy(alpha = 0.70f),
								lineHeight = 16.sp,
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun OnboardingSupportFooter(onSupportClick: () -> Unit) {
	Card(
		shape = RoundedCornerShape(22.dp),
		colors = CardDefaults.cardColors(containerColor = DarkBg),
		border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.22f)),
		modifier = Modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.weight(1f),
			) {
				Icon(
					imageVector = Icons.Default.SupportAgent,
					contentDescription = null,
					tint = EmeraldNeon,
					modifier = Modifier.size(28.dp),
				)
				Column {
					Text(
						text = "Precisa de ajuda com o pedido?",
						fontSize = 13.sp,
						fontWeight = FontWeight.Bold,
						color = Color.White,
					)
					Text(
						text = "Fale com nossa equipe de suporte.",
						fontSize = 11.sp,
						color = Color.White.copy(alpha = 0.60f),
					)
				}
			}

			Button(
				onClick = onSupportClick,
				colors = ButtonDefaults.buttonColors(
					containerColor = EmeraldNeon.copy(alpha = 0.15f),
					contentColor = EmeraldNeon,
				),
				shape = RoundedCornerShape(12.dp),
			) {
				Text("SUPORTE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
			}
		}
	}
}
