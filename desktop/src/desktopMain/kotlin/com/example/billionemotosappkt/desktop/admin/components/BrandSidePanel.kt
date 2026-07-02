package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable

fun BrandSidePanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Image(
                painter = painterResource("logo_billione.png"),
                contentDescription = "Billione Moto",
                modifier = Modifier.size(width = 190.dp, height = 86.dp),
                contentScale = ContentScale.Fit,
            )

            BadgePill(text = "Painel administrativo", accent = Color(0xFFD4AF37))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.widthIn(max = 520.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Comande sua frota com ")
                        withStyle(SpanStyle(color = Color(0xFF20E65B), fontWeight = FontWeight.Black)) {
                            append("precisão")
                        }
                    },
                    color = Color.White,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 54.sp,
                    letterSpacing = (-0.04).sp,
                )
                Text(
                    text = "Operação completa em uma só plataforma - contratos, manutenção, financeiro e rastreamento em tempo real.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.widthIn(max = 460.dp),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    BrandStatCard(value = "100%", label = "Operação digital")
                    BrandStatCard(value = "24/7", label = "Monitoramento")
                    BrandStatCard(value = "ISO", label = "Padrão de segurança")
                }
            }
        }

        Text(
            text = "© 2026 Billione Motos · Todos os direitos reservados",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 12.sp,
        )
    }
}
