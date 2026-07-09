package com.example.billionemotosappkt.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.components.CarrosselComSlide
import com.example.billionemotosappkt.components.auth.HomeSectionTitle
import com.example.billionemotosappkt.components.auth.LandingFooter
import com.example.billionemotosappkt.components.auth.LandingPlansSection
import com.example.billionemotosappkt.components.auth.LoginFullScreenOverlay
import com.example.billionemotosappkt.components.auth.QuickMenu
import com.example.billionemotosappkt.components.auth.TopAppAuth
import com.example.billionemotosappkt.`data-class`.SlideLogin
import com.example.billionemotosappkt.screens.cadastro.CadastroScreen
import com.example.billionemotosappkt.ui.theme.AppDimens
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme
import com.example.billionemotosappkt.ui.theme.DarkMutedForeground
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onMotosClick: () -> Unit = {},
    onPlanosClick: () -> Unit = {},
    onOndeEstamosClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var loginOuCad by remember { mutableStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var cadastroCpf by remember { mutableStateOf<String?>(null) }

    val imagens = listOf(
        SlideLogin(
            title = "Viva mais *liberdade* de Billione Moto",
            img = "hero_bg_moto1.png",
            desc = "Mude sua vida hoje, *sem burocracia*",
        ),
        SlideLogin(
            title = "Aventura *sem limites* na cidade",
            img = "moto_sport_updated.png",
            desc = "Avelloz 160 pronta para *qualquer terreno*",
        ),
        SlideLogin(
            title = "Rode com *estilo* pela cidade",
            img = "hero_bg_moto2.png",
            desc = "Avelloz 160 vermelha, *potência e elegância*",
        ),
        SlideLogin(
            title = "Sua moto *ideal* te espera",
            img = "moto_eletrica_new.png",
            desc = "Avelloz 160 azul, *liberdade no pôr do sol*",
        ),
        SlideLogin(
            title = "Modelos *modernos* e econômicos",
            img = "avelloz_160_black_new.png",
            desc = "Tecnologia e design *que impressionam*",
        ),
        SlideLogin(
            title = "Conquiste sua *independência*",
            img = "moto_detail_engine.png",
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

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val heroHeight = maxHeight * 0.86f

            Column(
            modifier = Modifier
                .fillMaxSize(1f)
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
                            Text(
                                text = "Acesso administrador",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showBottomSheet = true
                                        loginOuCad = 1
                                    }
                                    .padding(top = 2.dp),
                            )
                        }
                    },
                    autoAdvance = true,
                )
            }

            SectionSpacer()
            HomeSectionTitle(
                title = "Acesso rápido",
                subtitle = "Tudo que o usuário precisa em uma tela clara."
            )
            QuickMenu(
                onMotosClick = onMotosClick,
                onPlanosClick = onPlanosClick,
                onOndeEstamosClick = onOndeEstamosClick,
            )

            SectionSpacer()
            HomeSectionTitle(
                title = "Como funciona?",
                subtitle = "Quatro passos simples e sem ruído."
            )
            HowItWorksSection()

            SectionSpacer()
            HomeSectionTitle(
                title = "Planos em destaque",
                subtitle = "Card forte, preço claro e benefício direto."
            )
            LandingPlansSection(onPlanosClick = onPlanosClick)

            SectionSpacer()
            LandingFooter()

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showBottomSheet && loginOuCad == 1) {
            LoginFullScreenOverlay(
                isBusy = state.isSubmitting,
                errorMessage = state.errorMessage,
                onClose = { showBottomSheet = false },
                onLogin = { cpf, password ->
                    viewModel.onCpfChange(cpf)
                    viewModel.onPasswordChange(password)
                    viewModel.login()
                },
                onMotosClick = onMotosClick,
                onPlanosClick = onPlanosClick,
                onOndeEstamosClick = onOndeEstamosClick,
            )
        } else if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                dragHandle = null,
            ) {
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
            }
        }
        }
    }
}

@Composable
private fun SectionSpacer() {
    Spacer(modifier = Modifier.height(22.dp))
}

@Composable
private fun HowItWorksSection() {
    val steps = listOf(
        StepCardData(
            "1",
            "Escolha o plano",
            "Selecione o pacote ideal para você.",
            Icons.Filled.Science
        ),
        StepCardData(
            "2",
            "Escolha a moto",
            "Modelos modernos e econômicos.",
            Icons.Filled.BikeScooter
        ),
        StepCardData(
            "3",
            "Preencha cadastro",
            "Processo rápido e sem burocracia.",
            Icons.Filled.PersonAddAlt1
        ),
        StepCardData("4", "Realize pagamento", "Aprovação em minutos.", Icons.Filled.CreditCard),
    )

    Column(
        modifier = Modifier.padding(horizontal = AppDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        steps.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { step ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.14f
                            )
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                        ),
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
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
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

private data class StepCardData(
    val number: String,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

