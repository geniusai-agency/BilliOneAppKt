package com.example.billionemotosappkt.desktop.admin.screens.contractparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.components.DetailGrid
import com.example.billionemotosappkt.desktop.admin.components.SectionHeader
import com.example.billionemotosappkt.desktop.admin.components.SectionShell
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery

@Composable
internal fun HeaderRow(
    snapshot: AdminDashboardSnapshot,
    total: Int,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionHeader(
                title = "Contratos",
                subtitle = "$total contratos cadastrados",
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onRefresh,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Rodar automacoes", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF20E65B),
                    contentColor = Color.Black,
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            ) {
                Text("Novo contrato", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardWidth = ((maxWidth - 12.dp) / 2).coerceAtLeast(280.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            AdminSectionCard(
                title = "Contratos ativos",
                subtitle = "Em andamento",
                icon = Icons.Default.Description,
                accent = Color(0xFF20E65B),
                value = snapshot.activeContracts.toString(),
                modifier = Modifier.width(cardWidth),
            )
            AdminSectionCard(
                title = "Vencendo 60d",
                subtitle = "Proximas renovacoes",
                icon = Icons.Default.EventNote,
                accent = Color(0xFFFFB300),
                value = snapshot.dueIn60Days.toString(),
                modifier = Modifier.width(cardWidth),
            )
        }
    }
}

@Composable
internal fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.34f)) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.38f)) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.02f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedBorderColor = Color.White.copy(alpha = 0.12f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            cursorColor = Color(0xFF20E65B),
        ),
    )
}

@Composable
internal fun FilterSurface(
    planChips: List<PlanChip>,
    selectedPlan: String?,
    onPlanSelected: (String?) -> Unit,
    selectedStatus: ContratoStatus?,
    onStatusSelected: (ContratoStatus?) -> Unit,
    onClear: () -> Unit,
) {
    val activeCount = listOf(
        selectedPlan != null,
        selectedStatus != null,
    ).count { it }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Filtros de contratos",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$activeCount filtro(s) ativo(s)",
                        color = Color.White.copy(alpha = 0.42f),
                        fontSize = 12.sp,
                    )
                }

                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                ) {
                    Text("Limpar filtros")
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Por plano", color = Color.White.copy(alpha = 0.58f), fontSize = 12.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    planChips.forEach { chip ->
                        PlanChipView(
                            label = chip.label,
                            count = chip.count,
                            selected = selectedPlan == null && chip.label == "Todos" || selectedPlan?.equals(chip.label, ignoreCase = true) == true,
                            onClick = {
                                onPlanSelected(if (chip.label == "Todos") null else chip.label)
                            },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Status", color = Color.White.copy(alpha = 0.58f), fontSize = 12.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    StatusChip("Todos", selectedStatus == null) { onStatusSelected(null) }
                    ContratoStatus.entries.forEach { status ->
                        StatusChip(statusLabel(status), selectedStatus == status) { onStatusSelected(status) }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PlanChipView(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = when {
            selected -> Color(0xFF20E65B).copy(alpha = 0.18f)
            label.equals("Ouro", ignoreCase = true) -> Color(0xFFF59E0B).copy(alpha = 0.12f)
            label.equals("Prata", ignoreCase = true) -> Color(0xFF9CA3AF).copy(alpha = 0.12f)
            label.equals("Bronze", ignoreCase = true) -> Color(0xFFB45309).copy(alpha = 0.12f)
            else -> Color.White.copy(alpha = 0.03f)
        },
        border = BorderStroke(
            1.dp,
            when {
                selected -> Color(0xFF20E65B).copy(alpha = 0.35f)
                else -> Color.White.copy(alpha = 0.06f)
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                color = when {
                    selected -> Color(0xFF20E65B)
                    else -> Color.White.copy(alpha = 0.72f)
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                count.toString(),
                color = Color.White.copy(alpha = 0.42f),
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
internal fun StatusChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Color(0xFF20E65B).copy(alpha = 0.18f) else Color.White.copy(alpha = 0.03f),
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
        ),
    ) {
        Text(
            label,
            color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

internal data class PlanChip(
    val label: String,
    val count: Int,
)
