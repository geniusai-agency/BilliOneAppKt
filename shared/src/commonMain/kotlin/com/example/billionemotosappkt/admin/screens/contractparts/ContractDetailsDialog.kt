package com.example.billionemotosappkt.desktop.admin.screens.contractparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.shared.api.ContratoResponse

/**
 * Diálogo de detalhes do contrato: mostra as informações das linhas do contrato
 * (cliente, moto, plano, financeiro, vigência) de forma estruturada e permite
 * abrir o PDF para leitura completa.
 */
@Composable
internal fun ContractDetailsDialog(
    contrato: ContratoResponse,
    onOpenPdf: () -> Unit,
    onDismiss: () -> Unit,
) {
    val effectivePdfUrl = contrato.pdfUrl?.ifBlank { null } ?: "/contratos/${contrato.id}/pdf"
    val hasPdf = true

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0B0F0C),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier.widthIn(min = 640.dp, max = 720.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Cabeçalho
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            contractClientName(contrato) ?: "Contrato",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "${contrato.planoSnapshotNome} • ${motoLabel(contrato)}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                        )
                    }
                    StatusPill(contrato)
                    Spacer(Modifier.width(10.dp))
                    Surface(onClick = onDismiss, shape = CircleShape, color = Color.White.copy(alpha = 0.06f)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(8.dp).size(16.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Corpo com rolagem
                Column(
                    modifier = Modifier
                        .heightIn(max = 520.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    InfoSection(
                        icon = Icons.Default.Person,
                        title = "Cliente",
                        rows = listOf(
                            "Nome" to contractClientName(contrato),
                            "CPF" to contractClientCpf(contrato),
                            "Email" to contractClientEmail(contrato),
                            "Telefone" to contractClientPhone(contrato),
                            "Cidade / UF" to listOfNotNull(
                                contrato.cliente?.cidade?.takeIf { it.isNotBlank() },
                                contrato.cliente?.estado?.takeIf { it.isNotBlank() },
                            ).joinToString(" / ").ifBlank { null },
                        ),
                    )
                    InfoSection(
                        icon = Icons.Default.DirectionsBike,
                        title = "Moto",
                        rows = listOf(
                            "Modelo" to (contrato.moto?.modelo),
                            "Marca" to contrato.moto?.marca,
                            "Placa" to contrato.moto?.placa,
                            "Ano" to contrato.moto?.ano?.toString(),
                            "Cor" to contrato.moto?.cor,
                        ),
                    )
                    InfoSection(
                        icon = Icons.Default.Layers,
                        title = "Plano",
                        rows = listOf(
                            "Nome" to contrato.planoSnapshotNome,
                            "Nível" to contrato.planoSnapshotNivel,
                            "Valor" to brl(contrato.planoSnapshotValor),
                            "Descrição" to contrato.planoSnapshotDescricao,
                        ),
                    )
                    InfoSection(
                        icon = Icons.Default.AttachMoney,
                        title = "Financeiro",
                        rows = listOf(
                            "Parcela" to "${brl(contrato.valorParcela)} × ${contrato.numParcelas}",
                            "Entrada" to brl(contrato.valorEntrada),
                            "Caução" to brl(contrato.valorCaucao),
                            "Dia de vencimento" to contrato.diaVencimento?.toString(),
                            "Periodicidade" to prettyEnum(contrato.periodicidade.name),
                            "Modalidade" to prettyEnum(contrato.modalidade.name),
                            "Tipo" to prettyEnum(contrato.tipo.name),
                        ),
                    )
                    InfoSection(
                        icon = Icons.Default.EventNote,
                        title = "Vigência",
                        rows = listOf(
                            "Início" to formatIsoDate(contrato.dataInicio),
                            "Fim" to (formatIsoDate(contrato.dataFim) ?: "Sem fim"),
                            "Carência" to "${contrato.carenciaDias} dias",
                            "Status" to statusLabel(contrato.status),
                            "Criado em" to formatIsoDate(contrato.createdAt),
                            "Contrato" to contrato.id,
                        ),
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Rodapé
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    ) {
                        Text("Fechar")
                    }
                    Button(
                        onClick = onOpenPdf,
                        enabled = hasPdf,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF20E65B),
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.08f),
                            disabledContentColor = Color.White.copy(alpha = 0.35f),
                        ),
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (hasPdf) "Ler contrato (PDF)" else "Sem PDF disponível",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(contrato: ContratoResponse) {
    val color = statusColor(contrato.status)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Box(modifier = Modifier.size(7.dp).background(color, RoundedCornerShape(999.dp)))
        Spacer(Modifier.width(6.dp))
        Text(statusLabel(contrato.status), color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoSection(
    icon: ImageVector,
    title: String,
    rows: List<Pair<String, String?>>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Color(0xFF20E65B), modifier = Modifier.size(16.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        // Grade de 2 colunas
        rows.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { (label, value) ->
                    InfoTile(label = label, value = value, modifier = Modifier.weight(1f))
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
        Text(
            value?.takeIf { it.isNotBlank() } ?: "—",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun brl(value: String?): String? {
    val v = value?.trim()?.takeIf { it.isNotBlank() } ?: return null
    return if (v.startsWith("R$", ignoreCase = true)) v else "R$ $v"
}

private fun prettyEnum(name: String): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun contractClientName(contrato: ContratoResponse): String? {
    val cliente = contrato.cliente
    return listOf(
        cliente?.usuario?.nome?.trim(),
        cliente?.nome?.trim(),
    ).firstOrNull { !it.isNullOrBlank() }
}

private fun contractClientCpf(contrato: ContratoResponse): String? {
    val cliente = contrato.cliente
    return listOf(
        cliente?.usuario?.cpf?.trim(),
        cliente?.cpf?.trim(),
    ).firstOrNull { !it.isNullOrBlank() }
}

private fun contractClientEmail(contrato: ContratoResponse): String? {
    val cliente = contrato.cliente
    return listOf(
        cliente?.usuario?.email?.trim(),
        cliente?.email?.trim(),
    ).firstOrNull { !it.isNullOrBlank() }
}

private fun contractClientPhone(contrato: ContratoResponse): String? {
    val cliente = contrato.cliente
    return listOf(
        cliente?.usuario?.telefone?.trim(),
        cliente?.telefone?.trim(),
    ).firstOrNull { !it.isNullOrBlank() }
}
