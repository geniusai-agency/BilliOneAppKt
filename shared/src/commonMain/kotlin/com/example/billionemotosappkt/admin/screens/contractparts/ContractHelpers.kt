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
import com.example.billionemotosappkt.shared.utils.formatBrDate

@Composable
internal fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.18f)),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF4A4A))
            Spacer(Modifier.width(10.dp))
            Text(message, color = Color.White)
        }
    }
}

internal fun motoLabel(contrato: ContratoResponse): String {
    val model = contrato.moto?.modelo ?: contrato.planoSnapshotNome
    val plate = contrato.moto?.placa?.takeIf { it.isNotBlank() }
    return listOfNotNull(model.takeIf { it.isNotBlank() }, plate?.let { "• $it" }).joinToString(" ")
}

internal fun statusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.CANCELADO -> "Cancelado"
    ContratoStatus.PENDENTE_ASSINATURA -> "Pendente"
}

internal fun statusColor(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF20E65B)
    ContratoStatus.INADIMPLENTE -> Color(0xFFFF4A4A)
    ContratoStatus.ENCERRADO -> Color(0xFF8A8F95)
    ContratoStatus.CANCELADO -> Color(0xFFE05B64)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFFFB300)
}

internal fun formatIsoDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return formatBrDate(value) ?: value.take(10)
}
