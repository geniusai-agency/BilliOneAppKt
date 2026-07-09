package com.example.billionemotosappkt.desktop.admin.clientes.screens.detalheparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.accent
import com.example.billionemotosappkt.desktop.admin.clientes.model.label
import com.example.billionemotosappkt.desktop.admin.clientes.model.toListItem
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewSummary
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewViewerDialog
import com.example.billionemotosappkt.desktop.admin.clientes.components.openDocumentExternally
import com.example.billionemotosappkt.desktop.admin.clientes.components.loadDocumentPreviewSummary
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

internal fun contratoAtualNome(contrato: ContratoResponse?, fallback: ClienteListItem): String {
    if (contrato == null) return fallback.motoNome.ifBlank { "-" }
    return when {
        !contrato.moto?.modelo.isNullOrBlank() -> contrato.moto?.modelo.orEmpty()
        !contrato.moto?.modeloMoto?.nome.isNullOrBlank() -> contrato.moto?.modeloMoto?.nome.orEmpty()
        else -> fallback.motoNome.ifBlank { "-" }
    }
}

internal fun contractLabel(contrato: ContratoResponse): String {
    return contrato.cliente?.nome?.takeIf { it.isNotBlank() }
        ?: "Contrato ${contrato.id.takeLast(6).uppercase(Locale.getDefault())}"
}

internal fun contractStatusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.CANCELADO -> "Cancelado"
    ContratoStatus.PENDENTE_ASSINATURA -> "Pendente"
}

internal fun accentForContract(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF45D483)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFE0B64C)
    ContratoStatus.INADIMPLENTE -> Color(0xFFFF8A65)
    ContratoStatus.ENCERRADO -> Color(0xFF7C8A8F)
    ContratoStatus.CANCELADO -> Color(0xFFE05B64)
}

internal fun contractEndLabel(contrato: ContratoResponse): String {
    return formatIsoDate(contrato.dataFim) ?: "Sem data"
}

internal fun formatIsoDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        val parsed = ZonedDateTime.parse(value)
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(parsed)
    }.getOrNull() ?: value.take(10)
}
