package com.example.billionemotosappkt.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.R
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.components.CarrosselComSlide
import com.example.billionemotosappkt.components.auth.TopAppAuth
import com.example.billionemotosappkt.`data-class`.SlideLogin
import com.example.billionemotosappkt.data.auth.createBillioneMotosAuthStack
import com.example.billionemotosappkt.screens.cadastro.CadastroScreen
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme
import com.example.billionemotosappkt.ui.theme.DarkMutedForeground
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
	viewModel: AuthViewModel = viewModel(),
	onMotosClick: () -> Unit = {},
	onPlanosClick: () -> Unit = {},
	onOndeEstamosClick: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	AuthContent(
		viewModel = viewModel,
		onMotosClick = onMotosClick,
		onPlanosClick = onPlanosClick,
		onOndeEstamosClick = onOndeEstamosClick,
		modifier = modifier,
	)
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun AuthContent(
	viewModel: AuthViewModel,
	onMotosClick: () -> Unit,
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val state = viewModel.uiState.collectAsStateWithLifecycle().value
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()
	var loginOuCad by remember { mutableStateOf(0) }
	var showBottomSheet by remember { mutableStateOf(false) }
	var cadastroCpf by remember { mutableStateOf<String?>(null) }
	val heroHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.86f)

	val imagens = listOf(
		SlideLogin(
			title = "Viva mais *liberdade* de Billione Moto",
			img = R.drawable.hero_bg_moto1,
			desc = "Mude sua vida hoje, *sem burocracia*",
		),
		SlideLogin(
			title = "Aventura *sem limites* na cidade",
			img = R.drawable.moto_sport_updated,
			desc = "Avelloz 160 pronta para *qualquer terreno*",
		),
		SlideLogin(
			title = "Rode com *estilo* pela cidade",
			img = R.drawable.hero_bg_moto2,
			desc = "Avelloz 160 vermelha, *potência e elegância*",
		),
		SlideLogin(
			title = "Sua moto *ideal* te espera",
			img = R.drawable.moto_eletrica_new,
			desc = "Avelloz 160 azul, *liberdade no pôr do sol*",
		),
		SlideLogin(
			title = "Modelos *modernos* e econômicos",
			img = R.drawable.avelloz_160_black_new,
			desc = "Tecnologia e design *que impressionam*",
		),
		SlideLogin(
			title = "Conquiste sua *independência*",
			img = R.drawable.moto_detail_engine,
			desc = "No final do plano, *a moto é sua*",
		),
	)

	Surface(modifier = modifier.fillMaxSize()) {
		if (cadastroCpf != null) {
			CadastroScreen(
				cpf = cadastroCpf!!,
				viewModel = viewModel,
				onBack = { cadastroCpf = null },
			)
			return@Surface
		}

		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(
					Brush.verticalGradient(
						colors = listOf(
							MaterialTheme.colorScheme.surface,
							MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
						),
					),
				)
				.verticalScroll(rememberScrollState()),
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(heroHeight),
			) {
				CarrosselComSlide(
					imagens = imagens,
					topContent = { TopAppAuth() },
					content = {
						Column(
							verticalArrangement = Arrangement.spacedBy(AppDimens.ButtonGroupSpacing),
						) {
							Spacer(modifier = Modifier.height(2.dp))
							Text(
								text = "Escolha sua liberdade com mais leveza e menos burocracia.",
								color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.92f),
								fontSize = 15.sp,
								lineHeight = 21.sp,
							)
							ActionButton(
								text = "SOU NOVO AQUI",
								onClick = {
									showBottomSheet = true
									loginOuCad = 0
								},
								containerColor = MaterialTheme.colorScheme.primary,
								pressedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
								contentColor = MaterialTheme.colorScheme.onPrimary,
								borderColor = MaterialTheme.colorScheme.primary,
								pressedBorderColor = MaterialTheme.colorScheme.primary,
								glowPulse = false,
							)
							ActionButton(
								text = "JÁ SOU CLIENTE",
								onClick = {
									showBottomSheet = true
									loginOuCad = 1
								},
								containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.02f),
								pressedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
								contentColor = MaterialTheme.colorScheme.onSurface,
								borderColor = DarkMutedForeground.copy(alpha = 0.9f),
								pressedBorderColor = DarkMutedForeground,
								borderWidth = 1.dp,
								glowPulse = false,
							)
						}
					},
					autoAdvance = true,
				)
			}

			SectionSpacer()
			HomeSectionTitle(title = "Acesso rápido", subtitle = "Tudo que o usuário precisa em uma tela clara.")
			QuickMenu(
				onMotosClick = onMotosClick,
				onPlanosClick = onPlanosClick,
				onOndeEstamosClick = onOndeEstamosClick,
			)

			SectionSpacer()
			HomeSectionTitle(title = "Como funciona?", subtitle = "Quatro passos simples e sem ruído.")
			HowItWorksSection()

			SectionSpacer()
			HomeSectionTitle(title = "Planos em destaque", subtitle = "Card forte, preço claro e benefício direto.")
			LandingPlansSection(onPlanosClick = onPlanosClick)

			SectionSpacer()
			LandingFooter()

			Spacer(modifier = Modifier.height(20.dp))
		}

		if (showBottomSheet) {
			ModalBottomSheet(
				onDismissRequest = { showBottomSheet = false },
				sheetState = sheetState,
				dragHandle = null,
			) {
				if (loginOuCad == 0) {
					FormularioCadastro(
						submit = { cpf ->
							scope.launch {
								sheetState.hide()
							}.invokeOnCompletion {
								if (!sheetState.isVisible) {
									showBottomSheet = false
									cadastroCpf = cpf
								}
							}
						},
						onMotosClick = onMotosClick,
						onPlanosClick = onPlanosClick,
						onOndeEstamosClick = onOndeEstamosClick,
					)
				} else {
					FormularioLogin(
						isBusy = state.isSubmitting,
						errorMessage = state.errorMessage,
						onLogin = { cpf, password ->
							viewModel.onEmailChange(cpf)
							viewModel.onPasswordChange(password)
							viewModel.login()
						},
						onMotosClick = onMotosClick,
						onPlanosClick = onPlanosClick,
						onOndeEstamosClick = onOndeEstamosClick,
					)
				}
			}
		}
	}
}

@Composable
private fun HomeSectionTitle(
	title: String,
	subtitle: String,
) {
	Column(
		modifier = Modifier.padding(horizontal = AppDimens.ScreenPadding),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Text(
			text = title,
			color = MaterialTheme.colorScheme.onSurface,
			fontSize = 22.sp,
			fontWeight = FontWeight.Black,
		)
		Text(
			text = subtitle,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
			lineHeight = 18.sp,
		)
	}
}

@Composable
private fun SectionSpacer() {
	Spacer(modifier = Modifier.height(22.dp))
}

@Composable
private fun QuickMenu(
	onMotosClick: () -> Unit,
	onPlanosClick: () -> Unit,
	onOndeEstamosClick: () -> Unit,
) {
	val items = listOf(
		QuickMenuItem(Icons.Filled.BikeScooter, "Nossas motos", onMotosClick),
		QuickMenuItem(Icons.Filled.Description, "Nossos planos", onPlanosClick),
		QuickMenuItem(Icons.Filled.LocationOn, "Onde estamos", onOndeEstamosClick),
		QuickMenuItem(Icons.Filled.HelpOutline, "Perguntas frequentes", {}),
		QuickMenuItem(Icons.Filled.CreditCard, "Termos e contratos", {}),
	)

	Column(
		modifier = Modifier.padding(horizontal = AppDimens.ScreenPadding),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		items.forEach { item ->
			Card(
				onClick = item.onClick,
				shape = RoundedCornerShape(18.dp),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)),
				border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
				modifier = Modifier.fillMaxWidth(),
			) {
				Row(
					modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					Box(
						modifier = Modifier
							.size(36.dp)
							.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
						contentAlignment = Alignment.Center,
					) {
						Icon(
							imageVector = item.icon,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
						)
					}
					Spacer(modifier = Modifier.width(12.dp))
					Text(
						text = item.label,
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 14.sp,
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier.weight(1f),
					)
					Icon(
						imageVector = Icons.Filled.ArrowRight,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

@Composable
private fun HowItWorksSection() {
	val steps = listOf(
		StepCardData("1", "Escolha o plano", "Selecione o pacote ideal para você.", Icons.Filled.Science),
		StepCardData("2", "Escolha a moto", "Modelos modernos e econômicos.", Icons.Filled.BikeScooter),
		StepCardData("3", "Preencha cadastro", "Processo rápido e sem burocracia.", Icons.Filled.PersonAddAlt1),
		StepCardData("4", "Realize pagamento", "Aprovação em minutos.", Icons.Filled.CreditCard),
	)

	Column(
		modifier = Modifier.padding(horizontal = AppDimens.ScreenPadding),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		steps.chunked(2).forEach { rowItems ->
			Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
				rowItems.forEach { step ->
					Card(
						shape = RoundedCornerShape(18.dp),
						colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)),
						border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
						modifier = Modifier.weight(1f),
					) {
						Column(
							modifier = Modifier.padding(14.dp),
							verticalArrangement = Arrangement.spacedBy(10.dp),
						) {
							Box(
								modifier = Modifier
									.size(30.dp)
									.background(MaterialTheme.colorScheme.primary, CircleShape),
								contentAlignment = Alignment.Center,
							) {
								Text(
									text = step.number,
									color = MaterialTheme.colorScheme.onPrimary,
									fontSize = 12.sp,
									fontWeight = FontWeight.Black,
								)
							}
							Icon(imageVector = step.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
							Text(
								text = step.title,
								color = MaterialTheme.colorScheme.onSurface,
								fontWeight = FontWeight.Bold,
								fontSize = 13.sp,
							)
							Text(
								text = step.subtitle,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 12.sp,
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
private fun LandingPlansSection(onPlanosClick: () -> Unit) {
	val plans = listOf(
		LandingPlanData(
			tier = "OURO",
			name = "MINHA BILLIONE 0KM",
			price = "R$ 1.399",
			tagline = "A moto é sua no final",
			benefits = listOf("Moto 0km no final", "Seguro incluso", "Manutenções preventivas"),
			accent = Color(0xFF2EDB63),
		),
		LandingPlanData(
			tier = "PRATA",
			name = "MINHA BILLIONE SEMINOVA",
			price = "R$ 1.200",
			tagline = "A moto é sua no final",
			benefits = listOf("Moto seminova no final", "Sem limite de circulação", "Kit manutenção"),
			accent = Color(0xFF20D86B),
			highlight = true,
		),
		LandingPlanData(
			tier = "BRONZE",
			name = "MINHA BILLIONE SEMINOVA",
			price = "R$ 979",
			tagline = "A moto é sua no final",
			benefits = listOf("Sem limite de circulação", "Troca de óleo inclusa", "IPVA incluso"),
			accent = Color(0xFF46E07C),
		),
	)

	LazyRow(
		modifier = Modifier.fillMaxWidth(),
		contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AppDimens.ScreenPadding),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		items(plans.size) { index ->
			val plan = plans[index]
			Card(
				shape = RoundedCornerShape(24.dp),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)),
				border = BorderStroke(1.dp, plan.accent.copy(alpha = if (plan.highlight) 0.32f else 0.18f)),
				modifier = Modifier.width(280.dp),
			) {
				Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
					if (plan.highlight) {
						Text(
							text = "Mais escolhido",
							color = plan.accent,
							fontSize = 11.sp,
							fontWeight = FontWeight.Black,
						)
					}
					Text(
						text = plan.tier,
						color = plan.accent,
						fontSize = 12.sp,
						fontWeight = FontWeight.Black,
					)
					Text(
						text = plan.name,
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 18.sp,
						fontWeight = FontWeight.Black,
					)
					Text(
						text = plan.tagline,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 13.sp,
					)
					Text(
						text = plan.price,
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 28.sp,
						fontWeight = FontWeight.Black,
					)
					Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
						plan.benefits.forEach { benefit ->
							Row(verticalAlignment = Alignment.CenterVertically) {
								Box(
									modifier = Modifier
										.size(7.dp)
										.background(plan.accent, CircleShape),
								)
								Spacer(modifier = Modifier.width(8.dp))
								Text(
									text = benefit,
									color = MaterialTheme.colorScheme.onSurface,
									fontSize = 12.sp,
								)
							}
						}
					}
					ActionButton(
						text = "Ver plano",
						onClick = onPlanosClick,
						containerColor = plan.accent,
						pressedContainerColor = plan.accent.copy(alpha = 0.88f),
						contentColor = Color.Black,
						borderColor = plan.accent,
						pressedBorderColor = plan.accent,
						glowPulse = false,
					)
				}
			}
		}
	}
}

@Composable
private fun LandingFooter() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = AppDimens.ScreenPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Icon(
			painter = painterResource(R.drawable.logo_billione_footer),
			contentDescription = "Billione Moto",
			tint = Color.Unspecified,
			modifier = Modifier.size(width = 132.dp, height = 48.dp),
		)
		Text(
			text = "© 2026 Billione Moto. Todos os direitos reservados.",
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 11.sp,
			textAlign = TextAlign.Center,
		)
	}
}

private data class QuickMenuItem(
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
	val label: String,
	val onClick: () -> Unit,
)

private data class StepCardData(
	val number: String,
	val title: String,
	val subtitle: String,
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private data class LandingPlanData(
	val tier: String,
	val name: String,
	val price: String,
	val tagline: String,
	val benefits: List<String>,
	val accent: Color,
	val highlight: Boolean = false,
)
