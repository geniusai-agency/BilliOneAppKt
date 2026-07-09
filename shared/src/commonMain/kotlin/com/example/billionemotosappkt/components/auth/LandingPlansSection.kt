package com.example.billionemotosappkt.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.components.ActionButton
import com.example.billionemotosappkt.ui.theme.AppDimens

private data class LandingPlanData(
    val tier: String,
    val name: String,
    val price: String,
    val tagline: String,
    val benefits: List<String>,
    val accent: Color,
    val highlight: Boolean = false,
)

@Composable
fun LandingPlansSection(onPlanosClick: () -> Unit) {
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
            benefits = listOf(
                "Moto seminova no final",
                "Sem limite de circulação",
                "Kit manutenção"
            ),
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
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.14f
                    )
                ),
                border = BorderStroke(
                    1.dp,
                    plan.accent.copy(alpha = if (plan.highlight) 0.32f else 0.18f)
                ),
                modifier = Modifier.width(280.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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