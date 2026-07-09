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
import kotlinx.coroutines.launch

@Composable
internal fun ContractCard(api: BillioneMotosApi, contrato: ContratoResponse) {
    var previewOpen by remember { mutableStateOf(false) }
    var previewResult by remember { mutableStateOf<DocumentPreviewSummary?>(null) }
    var previewLoading by remember { mutableStateOf(false) }
    var previewError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(contrato.pdfUrl) {
        previewResult = null
        previewError = null
        previewLoading = false
        val resolvedUrl = contrato.pdfUrl?.trim().orEmpty()
        if (resolvedUrl.isBlank()) {
            previewError = "Arquivo indisponivel."
            return@LaunchedEffect
        }

        previewLoading = true
        try {
            previewResult = api.loadDocumentPreviewSummary(resolvedUrl)
            if (previewResult == null) {
                previewError = "Nao foi possivel carregar o arquivo."
            }
        } finally {
            previewLoading = false
        }
    }

    if (previewOpen) {
        DocumentPreviewViewerDialog(
            api = api,
            title = contractLabel(contrato),
            subtitle = contrato.planoSnapshotNome,
            url = contrato.pdfUrl,
            summary = previewResult,
            loading = false,
            error = previewError,
            onDismiss = { previewOpen = false },
            onOpenExternally = { openDocumentExternally(api, contrato.pdfUrl) },
        )
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, accentForContract(contrato.status).copy(alpha = 0.14f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(contractLabel(contrato), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${contrato.planoSnapshotNome} • ${contrato.moto?.modelo ?: contrato.motoId.take(8)}",
                        color = Color.White.copy(alpha = 0.44f),
                        fontSize = 12.sp,
                    )
                }
                StatusBadge(contractStatusLabel(contrato.status), accentForContract(contrato.status))
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val compact = maxWidth < 900.dp
                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MiniInfo("Inicio", formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio)
                        MiniInfo("Fim", contractEndLabel(contrato))
                        MiniInfo("Parcela", "R$ ${contrato.valorParcela}")
                        MiniInfo("Plano", contrato.planoSnapshotNivel)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        MiniInfo("Inicio", formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio, modifier = Modifier.weight(1f))
                        MiniInfo("Fim", contractEndLabel(contrato), modifier = Modifier.weight(1f))
                        MiniInfo("Parcela", "R$ ${contrato.valorParcela}", modifier = Modifier.weight(1f))
                        MiniInfo("Plano", contrato.planoSnapshotNivel, modifier = Modifier.weight(1f))
                    }
                }
            }

            TextButton(
                onClick = {
                    previewOpen = true
                },
            ) {
                Text(
                    text = "Ver contrato",
                    color = Color(0xFF20E65B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun MiniInfo(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.025f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.40f), fontSize = 11.sp)
            Text(value.ifBlank { "-" }, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
