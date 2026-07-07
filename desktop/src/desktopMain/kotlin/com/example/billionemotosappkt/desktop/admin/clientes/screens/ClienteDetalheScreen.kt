package com.example.billionemotosappkt.desktop.admin.clientes.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ClienteResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.ContratoStatus
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClienteDetalheScreen(
    cliente: ClienteListItem,
    api: BillioneMotosApi,
    onBack: () -> Unit,
) {
    var clienteDetalhado by remember { mutableStateOf<ClienteResponse?>(null) }
    var contratos by remember { mutableStateOf<List<ContratoResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cliente.id) {
        isLoading = true
        loadError = null

        val response = runCatching { api.clientes.get(cliente.id) }.getOrNull()
        val contratoList = runCatching {
            api.contratos.list(
                ListContratosQuery(
                    clienteId = cliente.id,
                    includeRelations = true,
                    page = 1,
                    limit = 50,
                ),
            ).items
        }.getOrDefault(emptyList())

        clienteDetalhado = response
        contratos = buildList {
            addAll(response?.contratos.orEmpty())
            response?.contrato?.let { add(it) }
            addAll(contratoList)
        }
            .distinctBy { it.id }
            .sortedWith(compareByDescending<ContratoResponse> { it.status == ContratoStatus.ATIVO }.thenByDescending { it.dataInicio })

        loadError = when {
            response == null && contratoList.isEmpty() -> "Nao foi possivel carregar o detalhe do cliente."
            else -> null
        }
        isLoading = false
    }

    val baseCliente = clienteDetalhado?.toListItem()?.copy(
        planoNome = clienteDetalhado?.plano?.nome ?: cliente.planoNome,
        contratoId = contratos.firstOrNull()?.id ?: cliente.contratoId,
        contratoStatus = contratos.firstOrNull()?.status?.name ?: cliente.contratoStatus,
        motoNome = contratoAtualNome(contratos.firstOrNull(), cliente),
        motoPlaca = contratos.firstOrNull()?.moto?.placa ?: cliente.motoPlaca,
    ) ?: cliente

    val contratoAtual = contratos.firstOrNull { it.status == ContratoStatus.ATIVO }
        ?: contratos.firstOrNull()

    val planoNome = when {
        !clienteDetalhado?.plano?.nome.isNullOrBlank() -> clienteDetalhado?.plano?.nome.orEmpty()
        !contratoAtual?.plano?.nome.isNullOrBlank() -> contratoAtual?.plano?.nome.orEmpty()
        !contratoAtual?.planoSnapshotNome.isNullOrBlank() -> contratoAtual?.planoSnapshotNome.orEmpty()
        baseCliente.planoNome.isNotBlank() -> baseCliente.planoNome
        baseCliente.planoId.isNotBlank() -> baseCliente.planoId
        else -> "-"
    }

    val motoNome = contratoAtualNome(contratoAtual, baseCliente)
    val motoPlaca = contratoAtual?.moto?.placa?.takeIf { it.isNotBlank() } ?: baseCliente.motoPlaca.ifBlank { "-" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060907)),
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF20E65B))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    HeaderBar(
                        cliente = baseCliente,
                        planoNome = planoNome,
                        motoNome = motoNome,
                        onBack = onBack,
                    )
                }

                loadError?.let { message ->
                    item { ErrorBanner(message) }
                }

                item {
                    OverviewGrid(
                        planoNome = planoNome,
                        contratoAtual = contratoAtual,
                        motoNome = motoNome,
                        motoPlaca = motoPlaca,
                    )
                }

                item {
                    DetailSection(
                        title = "Dados do cliente",
                        rows = listOf(
                            "CPF" to baseCliente.cpf,
                            "Telefone" to baseCliente.telefone,
                            "Email" to baseCliente.email,
                            "Cidade" to baseCliente.cityLabel,
                            "Plano" to planoNome,
                        ),
                    )
                }

                item {
                    DetailSection(
                        title = "Contrato atual",
                        rows = if (contratoAtual != null) {
                            listOf(
                                "Contrato" to contractLabel(contratoAtual),
                                "Status" to contractStatusLabel(contratoAtual.status),
                                "Moto" to motoNome,
                                "Placa" to motoPlaca,
                                "Inicio" to (formatIsoDate(contratoAtual.dataInicio) ?: "-"),
                                "Fim" to (formatIsoDate(contratoAtual.dataFim) ?: "Sem fim definido"),
                                "Parcela" to "R$ ${contratoAtual.valorParcela}",
                                "Periodo" to contratoAtual.periodicidade.name,
                            )
                        } else {
                            listOf("Contrato" to "Nenhum contrato localizado.")
                        },
                    )
                }

                item {
                    DetailSection(
                        title = "Documentos e endereco",
                        rows = listOf(
                            "Endereco" to baseCliente.endereco.ifBlank { "-" },
                            "Endereco parente" to baseCliente.enderecoParente.ifBlank { "-" },
                            "CEP" to baseCliente.cep.ifBlank { "-" },
                            "CNH" to listOf(baseCliente.cnh, baseCliente.cnhCategoria)
                                .filter { it.isNotBlank() }
                                .joinToString(" / ")
                                .ifBlank { "-" },
                        ),
                    )
                }

                item {
                    DetailSection(
                        title = "Observacoes",
                        rows = emptyList(),
                        body = {
                            Text(
                                text = baseCliente.observacoes.ifBlank { "Sem observacoes registradas." },
                                color = Color.White.copy(alpha = 0.74f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                            )
                        },
                    )
                }

                item {
                    SectionTitle(
                        title = "Contratos vinculados",
                        subtitle = "${contratos.size} contrato(s) carregado(s)",
                    )
                }

                if (contratos.isEmpty()) {
                    item { EmptyState("Nenhum contrato encontrado para este cliente.") }
                } else {
                    items(contratos, key = { it.id }) { contrato ->
                        ContractCard(contrato = contrato)
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun HeaderBar(
    cliente: ClienteListItem,
    planoNome: String,
    motoNome: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(
            onClick = onBack,
            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.62f)),
        ) {
            Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Voltar", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(cliente.status.accent().copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Person, null, tint = cliente.status.accent(), modifier = Modifier.size(24.dp))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(cliente.nome, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Plano: $planoNome  |  Moto: $motoNome",
                color = Color.White.copy(alpha = 0.44f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        StatusBadge(cliente.status.label(), cliente.status.accent())
    }
}

@Composable
private fun OverviewGrid(
    planoNome: String,
    contratoAtual: ContratoResponse?,
    motoNome: String,
    motoPlaca: String,
) {
    val cards = listOf(
        "Plano" to planoNome,
        "Contrato" to (contratoAtual?.let { contractStatusLabel(it.status) } ?: "Sem contrato"),
        "Moto" to motoNome,
        "Placa" to motoPlaca,
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 1040.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cards.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { (title, value) ->
                            MiniMetricCard(title = title, value = value, modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                cards.forEach { (title, value) ->
                    MiniMetricCard(title = title, value = value, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MiniMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(value.ifBlank { "-" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    rows: List<Pair<String, String>>,
    body: (@Composable () -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp, modifier = Modifier.weight(0.34f))
                    Text(
                        value.ifBlank { "-" },
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(0.66f),
                        textAlign = TextAlign.End,
                    )
                }
            }
            body?.invoke()
        }
    }
}

@Composable
private fun ContractCard(contrato: ContratoResponse) {
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MiniInfo("Inicio", formatIsoDate(contrato.dataInicio) ?: contrato.dataInicio, modifier = Modifier.weight(1f))
                MiniInfo("Fim", contractEndLabel(contrato), modifier = Modifier.weight(1f))
                MiniInfo("Parcela", "R$ ${contrato.valorParcela}", modifier = Modifier.weight(1f))
                MiniInfo("Plano", contrato.planoSnapshotNivel, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniInfo(label: String, value: String, modifier: Modifier = Modifier) {
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

@Composable
private fun StatusBadge(label: String, accent: Color) {
    Surface(
        color = accent.copy(alpha = 0.15f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(accent, CircleShape),
            )
            Spacer(Modifier.width(6.dp))
            Text(label, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color.White.copy(alpha = 0.40f), fontSize = 12.sp)
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF4A4A))
            Spacer(Modifier.width(10.dp))
            Text(message, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
            Text(message, color = Color.White.copy(alpha = 0.42f), fontSize = 13.sp)
        }
    }
}

private fun contratoAtualNome(contrato: ContratoResponse?, fallback: ClienteListItem): String {
    if (contrato == null) return fallback.motoNome.ifBlank { "-" }
    return when {
        !contrato.moto?.modelo.isNullOrBlank() -> contrato.moto?.modelo.orEmpty()
        !contrato.moto?.modeloMoto?.nome.isNullOrBlank() -> contrato.moto?.modeloMoto?.nome.orEmpty()
        else -> fallback.motoNome.ifBlank { "-" }
    }
}

private fun contractLabel(contrato: ContratoResponse): String {
    return contrato.cliente?.nome?.takeIf { it.isNotBlank() }
        ?: "Contrato ${contrato.id.takeLast(6).uppercase(Locale.getDefault())}"
}

private fun contractStatusLabel(status: ContratoStatus): String = when (status) {
    ContratoStatus.ATIVO -> "Ativo"
    ContratoStatus.INADIMPLENTE -> "Inadimplente"
    ContratoStatus.ENCERRADO -> "Encerrado"
    ContratoStatus.CANCELADO -> "Cancelado"
    ContratoStatus.PENDENTE_ASSINATURA -> "Pendente"
}

private fun accentForContract(status: ContratoStatus): Color = when (status) {
    ContratoStatus.ATIVO -> Color(0xFF45D483)
    ContratoStatus.PENDENTE_ASSINATURA -> Color(0xFFE0B64C)
    ContratoStatus.INADIMPLENTE -> Color(0xFFFF8A65)
    ContratoStatus.ENCERRADO -> Color(0xFF7C8A8F)
    ContratoStatus.CANCELADO -> Color(0xFFE05B64)
}

private fun contractEndLabel(contrato: ContratoResponse): String {
    return formatIsoDate(contrato.dataFim) ?: "Sem data"
}

private fun formatIsoDate(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        val parsed = ZonedDateTime.parse(value)
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(parsed)
    }.getOrNull() ?: value.take(10)
}
