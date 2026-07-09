package com.example.billionemotosappkt.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.screens.auth.FormularioLogin
import com.example.billionemotosappkt.ui.theme.AppDimens

@Composable
fun LoginFullScreenOverlay(
    isBusy: Boolean,
    errorMessage: String?,
    onClose: () -> Unit,
    onLogin: (cpf: String, password: String) -> Unit,
    onMotosClick: () -> Unit,
    onPlanosClick: () -> Unit,
    onOndeEstamosClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val heroHeight = maxHeight

        LaunchedEffect(heroHeight) {
            scrollState.scrollTo(with(density) { heroHeight.roundToPx() })
        }

        Image(
            painter = rememberAssetPainter("hero_bg_moto1.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.22f),
                            Color.Black.copy(alpha = 0.60f),
                            Color.Black.copy(alpha = 0.88f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = AppDimens.ScreenPadding,
                            vertical = AppDimens.ScreenPadding
                        ),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            TopAppAuth()
                        }
                        Text(
                            text = "FECHAR",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.clickable(onClick = onClose),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Viva mais *liberdade* de Billione Moto",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 34.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "Mude sua vida hoje, sem burocracia.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
                            ),
                        ),
                        RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    )
                    .padding(bottom = 24.dp),
            ) {
                FormularioLogin(
                    isBusy = isBusy,
                    errorMessage = errorMessage,
                    onLogin = onLogin,
                    onMotosClick = onMotosClick,
                    onPlanosClick = onPlanosClick,
                    onOndeEstamosClick = onOndeEstamosClick,
                )
            }
        }
    }
}
