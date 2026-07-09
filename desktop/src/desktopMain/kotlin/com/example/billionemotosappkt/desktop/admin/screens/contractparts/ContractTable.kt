package com.example.billionemotosappkt.desktop.admin.screens.contractparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewViewerDialog
import com.example.billionemotosappkt.desktop.admin.clientes.components.loadDocumentPreviewSummary
import com.example.billionemotosappkt.desktop.admin.clientes.components.openDocumentExternally
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.UpdateContratoRequest

@Composable
internal fun ContractsTable(
    contratos: List<ContratoResponse>,
    totalItems: Int,
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onView: (ContratoResponse) -> Unit,
    onEdit: (ContratoResponse) -> Unit,
    onInactivate: (ContratoResponse) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B100D)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Lista de contratos",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$totalItems contratos carregados",
                        color = Color.White.copy(alpha = 0.42f),
                        fontSize = 12.sp,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFF20E65B).copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, Color(0xFF20E65B).copy(alpha = 0.18f)),
                ) {
                    Text(
                        text = "${contratos.size} em exibição",
                        color = Color(0xFF20E65B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

            if (contratos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.24f),
                            modifier = Modifier.size(28.dp),
                        )
                        Text(
                            text = "Nenhum contrato encontrado.",
                            color = Color.White.copy(alpha = 0.44f),
                            fontSize = 13.sp,
                        )
                    }
                }
            } else {
                contratos.forEachIndexed { index, contrato ->
                    ContractRowCard(
                        contrato = contrato,
                        onView = onView,
                        onEdit = onEdit,
                        onInactivate = onInactivate,
                    )
                    if (index < contratos.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            ContractsPagerBar(
                currentPage = currentPage,
                totalPages = totalPages,
                totalItems = totalItems,
                onPrev = onPrev,
                onNext = onNext,
            )
        }
    }
}

@Composable
internal fun ContractsPagerBar(
    currentPage: Int,
    totalPages: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Mostrando página $currentPage de $totalPages · $totalItems contrato(s)",
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 12.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onPrev,
                enabled = currentPage > 1,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Anterior")
            }
            Button(
                onClick = onNext,
                enabled = currentPage < totalPages,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF20E65B),
                    contentColor = Color.Black,
                ),
            ) {
                Text("Próxima")
            }
        }
    }
}

@Composable
internal fun ContractRowCard(
    contrato: ContratoResponse,
    onView: (ContratoResponse) -> Unit,
    onEdit: (ContratoResponse) -> Unit,
    onInactivate: (ContratoResponse) -> Unit,
) {
    val accent = statusColor(contrato.status)
    val closed = contrato.status == ContratoStatus.CANCELADO || contrato.status == ContratoStatus.ENCERRADO

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1511)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val compact = maxWidth < 920.dp

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = contrato.cliente?.nome ?: "Cliente #${contrato.clienteId.take(8)}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${contrato.planoSnapshotNome} • ${motoLabel(contrato)}",
                            color = Color.White.copy(alpha = 0.46f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    StatusBadge(
                        label = statusLabel(contrato.status),
                        accent = accent,
                    )
                }

                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ContractMetaRow(
                            items = listOf(
                                "Início" to (formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio.take(10)),
                                "Fim" to (contrato.dataFim?.let { formatIsoDate(it) ?: it.take(10) } ?: "Sem fim"),
                            ),
                        )
                        ContractMetaRow(
                            items = listOf(
                                "Plano" to contrato.planoSnapshotNivel,
                                "Parcela" to "R$ ${contrato.valorParcela} x ${contrato.numParcelas}",
                            ),
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ContractMetaChip(
                            label = "Início",
                            value = formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio.take(10),
                            modifier = Modifier.weight(1f),
                        )
                        ContractMetaChip(
                            label = "Fim",
                            value = contrato.dataFim?.let { formatIsoDate(it) ?: it.take(10) } ?: "Sem fim",
                            modifier = Modifier.weight(1f),
                        )
                        ContractMetaChip(
                            label = "Plano",
                            value = contrato.planoSnapshotNivel,
                            modifier = Modifier.weight(1f),
                        )
                        ContractMetaChip(
                            label = "Parcela",
                            value = "R$ ${contrato.valorParcela} x ${contrato.numParcelas}",
                            modifier = Modifier.weight(1.15f),
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ContractActionButton(
                            label = "Visualizar",
                            icon = Icons.Default.Visibility,
                            accent = Color(0xFF7DD3FC),
                            onClick = { onView(contrato) },
                        )
                        ContractActionButton(
                            label = "Editar",
                            icon = Icons.Default.Edit,
                            accent = Color(0xFF20E65B),
                            onClick = { onEdit(contrato) },
                        )
                        ContractActionButton(
                            label = if (closed) "Inativado" else "Inativar",
                            icon = Icons.Default.Close,
                            accent = Color(0xFFFF6B6B),
                            onClick = { if (!closed) onInactivate(contrato) },
                            enabled = !closed,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ContractMetaRow(
    items: List<Pair<String, String>>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { (label, value) ->
            ContractMetaChip(
                label = label,
                value = value,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun ContractMetaChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.025f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.40f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value.ifBlank { "-" },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ContractActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (enabled) accent else Color.White.copy(alpha = 0.25f),
        ),
        border = BorderStroke(1.dp, if (enabled) accent.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun StatusBadge(
    label: String,
    accent: Color,
) {
    Surface(
        color = accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
    ) {
        Text(
            text = label,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
internal fun ContractEditDialog(
    contrato: ContratoResponse,
    onDismiss: () -> Unit,
    onConfirm: (UpdateContratoRequest) -> Unit,
    isLoading: Boolean = false,
) {
    var dataInicio by remember(contrato.id) { mutableStateOf(contrato.dataInicio.take(10)) }
    var dataFim by remember(contrato.id) { mutableStateOf(contrato.dataFim?.take(10).orEmpty()) }
    var valorParcela by remember(contrato.id) { mutableStateOf(contrato.valorParcela) }
    var numParcelas by remember(contrato.id) { mutableStateOf(contrato.numParcelas.toString()) }
    var observacoes by remember(contrato.id) { mutableStateOf(contrato.observacoes.orEmpty()) }
    var motivoBloqueio by remember(contrato.id) { mutableStateOf(contrato.motivoBloqueio.orEmpty()) }
    var motivoRescisao by remember(contrato.id) { mutableStateOf(contrato.motivoRescisao.orEmpty()) }

    val canSave = dataInicio.isNotBlank() &&
        valorParcela.toDoubleOrNull() != null &&
        numParcelas.toIntOrNull() != null &&
        !isLoading

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0C120E),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Editar contrato", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = contrato.cliente?.nome ?: contrato.id,
                    color = Color.White.copy(alpha = 0.46f),
                    fontSize = 12.sp,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Ajuste os dados principais. A inativacao continua separada.",
                    color = Color.White.copy(alpha = 0.44f),
                    fontSize = 11.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ContractTextField(
                        label = "Data início",
                        value = dataInicio,
                        onValueChange = { dataInicio = it },
                        modifier = Modifier.weight(1f),
                    )
                    ContractTextField(
                        label = "Data fim",
                        value = dataFim,
                        onValueChange = { dataFim = it },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ContractTextField(
                        label = "Valor da parcela",
                        value = valorParcela,
                        onValueChange = { valorParcela = it },
                        modifier = Modifier.weight(1f),
                    )
                    ContractTextField(
                        label = "Número de parcelas",
                        value = numParcelas,
                        onValueChange = { numParcelas = it },
                        modifier = Modifier.weight(1f),
                    )
                }
                ContractTextField(
                    label = "Observações",
                    value = observacoes,
                    onValueChange = { observacoes = it },
                    minLines = 3,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ContractTextField(
                        label = "Motivo bloqueio",
                        value = motivoBloqueio,
                        onValueChange = { motivoBloqueio = it },
                        modifier = Modifier.weight(1f),
                    )
                    ContractTextField(
                        label = "Motivo rescisão",
                        value = motivoRescisao,
                        onValueChange = { motivoRescisao = it },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val payload = UpdateContratoRequest(
                        dataInicio = normalizeDateInput(dataInicio),
                        dataFim = normalizeDateInput(dataFim),
                        valorParcela = valorParcela.toDoubleOrNull(),
                        numParcelas = numParcelas.toIntOrNull(),
                        observacoes = observacoes.trim().ifBlank { null },
                        motivoBloqueio = motivoBloqueio.trim().ifBlank { null },
                        motivoRescisao = motivoRescisao.trim().ifBlank { null },
                    )
                    onConfirm(payload)
                },
                enabled = canSave,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
            ) {
                Text(if (isLoading) "Salvando..." else "Salvar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Text("Cancelar")
            }
        },
    )
}

@Composable
internal fun ContractInactivateDialog(
    contrato: ContratoResponse,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0C120E),
        title = {
            Text("Inativar contrato", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                text = "O contrato de ${contrato.cliente?.nome ?: contrato.id} será cancelado e a moto será liberada conforme a regra atual do backend.",
                color = Color.White.copy(alpha = 0.64f),
                fontSize = 12.sp,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B), contentColor = Color.White),
            ) {
                Text(if (isLoading) "Inativando..." else "Inativar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Text("Cancelar")
            }
        },
    )
}

@Composable
internal fun ContractPreviewDialog(
    api: BillioneMotosApi,
    contrato: ContratoResponse,
    onDismiss: () -> Unit,
) {
    var loading by remember(contrato.id) { mutableStateOf(false) }
    var error by remember(contrato.id) { mutableStateOf<String?>(null) }
    var summary by remember(contrato.id) { mutableStateOf<com.example.billionemotosappkt.desktop.admin.clientes.components.DocumentPreviewSummary?>(null) }

    LaunchedEffect(contrato.pdfUrl) {
        loading = true
        error = null
        summary = null
        summary = runCatching {
            api.loadDocumentPreviewSummary(contrato.pdfUrl)
        }.getOrNull()
        if (contrato.pdfUrl.isNullOrBlank()) {
            error = "Contrato sem PDF para visualizacao."
        }
        loading = false
    }

    DocumentPreviewViewerDialog(
        api = api,
        title = contrato.cliente?.nome ?: "Contrato",
        subtitle = "${contrato.planoSnapshotNome} • ${motoLabel(contrato)}",
        url = contrato.pdfUrl,
        summary = summary,
        loading = loading,
        error = error,
        onDismiss = onDismiss,
        onOpenExternally = { openDocumentExternally(api, contrato.pdfUrl) },
    )
}

@Composable
private fun ContractTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF20E65B),
            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
            focusedLabelColor = Color(0xFF20E65B),
            unfocusedLabelColor = Color.White.copy(alpha = 0.46f),
            cursorColor = Color(0xFF20E65B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
        ),
    )
}

private fun normalizeDateInput(value: String): String? {
    val text = value.trim()
    if (text.isBlank()) return null
    if (text.contains('T')) return text
    return "${text}T00:00:00Z"
}
