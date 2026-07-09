package com.example.billionemotosappkt.desktop.admin.clientes.screens

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
import com.example.billionemotosappkt.desktop.admin.clientes.screens.detalheparts.*

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
            .background(
                androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(Color(0xFF122015), Color(0xFF071009), Color(0xFF040604)),
                    radius = 1400f,
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent,
                            Color(0xFF20E65B).copy(alpha = 0.03f),
                        ),
                    ),
                ),
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF20E65B))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    ClientHero(
                        cliente = baseCliente,
                        planoNome = planoNome,
                        motoNome = motoNome,
                        motoPlaca = motoPlaca,
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
                    SectionCard(title = "Resumo do cliente") {
                        KeyValueGrid(
                            items = listOf(
                                "CPF" to baseCliente.cpf,
                                "Telefone" to baseCliente.telefone,
                                "Email" to baseCliente.email,
                                "Cidade" to baseCliente.cityLabel,
                                "Plano" to planoNome,
                                "Status" to baseCliente.status.label(),
                            ),
                        )
                    }
                }

                item {
                    SectionCard(title = "Contrato atual") {
                        if (contratoAtual == null) {
                            EmptyState("Nenhum contrato localizado para este cliente.")
                        } else {
                            ContractCard(api = api, contrato = contratoAtual)
                        }
                    }
                }

                item {
                    SectionCard(title = "Endereco e observacoes") {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val compact = maxWidth < 920.dp
                            if (compact) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    InfoBlock(
                                        title = "Endereco",
                                        rows = listOf(
                                            "Endereco" to baseCliente.endereco.ifBlank { "-" },
                                            "Endereco parente" to baseCliente.enderecoParente.ifBlank { "-" },
                                            "CEP" to baseCliente.cep.ifBlank { "-" },
                                        ),
                                    )
                                    InfoBlock(
                                        title = "Observacoes",
                                        rows = emptyList(),
                                        body = {
                                            Text(
                                                text = baseCliente.observacoes.ifBlank { "Sem observacoes registradas." },
                                                color = Color.White.copy(alpha = 0.76f),
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                            )
                                        },
                                    )
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    InfoBlock(
                                        title = "Endereco",
                                        rows = listOf(
                                            "Endereco" to baseCliente.endereco.ifBlank { "-" },
                                            "Endereco parente" to baseCliente.enderecoParente.ifBlank { "-" },
                                            "CEP" to baseCliente.cep.ifBlank { "-" },
                                        ),
                                        modifier = Modifier.weight(1f),
                                    )
                                    InfoBlock(
                                        title = "Observacoes",
                                        rows = emptyList(),
                                        body = {
                                            Text(
                                                text = baseCliente.observacoes.ifBlank { "Sem observacoes registradas." },
                                                color = Color.White.copy(alpha = 0.76f),
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
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
                        ContractCard(api = api, contrato = contrato)
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}
