package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanoFormData
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanoFormDialog
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanoPalette
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanosGrid
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanosOverview
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.PlanosOverviewRow
import com.example.billionemotosappkt.desktop.admin.screens.planosparts.buildPlanosOverview
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.CreatePlanoRequest
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListPlanosQuery
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.UpdatePlanoRequest
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * Seção de Planos do portal administrativo: catálogo comercial + gestão.
 *
 * Mostra indicadores da operação (assinantes, MRR, crescimento, projeção e
 * lucro estimado), calculados a partir de contratos reais, e permite criar e
 * editar planos. UI dividida no subpacote `planosparts`.
 */
@Composable
fun PlanosSection(
    api: BillioneMotosApi,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var planos by remember { mutableStateOf<List<PlanoResponse>>(emptyList()) }
    var contratos by remember { mutableStateOf<List<ContratoResponse>>(emptyList()) }
    var reloadTick by remember { mutableIntStateOf(0) }

    // Estado do formulário de criação/edição.
    var formOpen by remember { mutableStateOf(false) }
    var editingPlano by remember { mutableStateOf<PlanoResponse?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val yearMonth = remember { YearMonth.now() }
    val ymNow = remember(yearMonth) { yearMonth.toString() }
    val ymPrev = remember(yearMonth) { yearMonth.minusMonths(1).toString() }

    LaunchedEffect(api, reloadTick) {
        isLoading = true
        errorMessage = null
        val planosResult = runCatching { api.planos.list(ListPlanosQuery(includeInactive = true)) }
        val contratosResult = runCatching {
            api.contratos.list(ListContratosQuery(includeRelations = true, page = 1, limit = 500)).items
        }
        planosResult
            .onSuccess { planos = it }
            .onFailure { errorMessage = it.message ?: "Erro ao carregar planos" }
        // Contratos alimentam as métricas; se falhar, os planos ainda aparecem.
        contratosResult.onSuccess { contratos = it }
        isLoading = false
    }

    val overview: PlanosOverview = remember(planos, contratos, ymNow, ymPrev) {
        buildPlanosOverview(planos, contratos, ymNow, ymPrev)
    }

    val filteredStats = remember(overview, query) {
        val normalized = query.trim()
        if (normalized.isBlank()) {
            overview.statsPorPlano
        } else {
            overview.statsPorPlano.filter { s ->
                listOfNotNull(s.plano.nome, s.plano.nivel, s.plano.descricao)
                    .any { it.contains(normalized, ignoreCase = true) } ||
                    s.plano.tags.any { it.contains(normalized, ignoreCase = true) }
            }
        }
    }

    fun submitForm(data: PlanoFormData) {
        scope.launch {
            isSaving = true
            formError = null
            val target = editingPlano
            val result = runCatching {
                if (target == null) {
                    api.planos.create(
                        CreatePlanoRequest(
                            nome = data.nome,
                            nivel = data.nivel,
                            valor = data.valor,
                            tags = data.tags.ifEmpty { null },
                            descricao = data.descricao,
                        ),
                    )
                } else {
                    api.planos.update(
                        target.id,
                        UpdatePlanoRequest(
                            nome = data.nome,
                            nivel = data.nivel,
                            valor = data.valor,
                            tags = data.tags,
                            descricao = data.descricao,
                            ativo = data.ativo,
                        ),
                    )
                }
            }
            isSaving = false
            result
                .onSuccess {
                    formOpen = false
                    editingPlano = null
                    reloadTick += 1
                }
                .onFailure { formError = it.message ?: "Falha ao salvar o plano." }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Cabeçalho + ação principal
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Planos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Catálogo comercial, base de assinantes e desempenho.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                )
            }
            Button(
                onClick = {
                    editingPlano = null
                    formError = null
                    formOpen = true
                },
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlanoPalette.accent, contentColor = Color.Black),
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Novo plano", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        PlanosOverviewRow(overview = overview)

        // Busca
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Exibindo ${filteredStats.size} de ${planos.size} planos",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp,
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.width(300.dp).height(56.dp),
                placeholder = { Text("Buscar plano...", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlanoPalette.accent.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color.White.copy(alpha = 0.02f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
            )
        }

        when {
            isLoading -> Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PlanoPalette.accent)
            }

            errorMessage != null -> Card(
                colors = CardDefaults.cardColors(containerColor = PlanoPalette.danger.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, PlanoPalette.danger.copy(alpha = 0.2f)),
            ) {
                Text(errorMessage!!, color = PlanoPalette.danger, modifier = Modifier.padding(16.dp))
            }

            filteredStats.isEmpty() -> Box(
                Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (planos.isEmpty()) "Nenhum plano cadastrado. Crie o primeiro em \"Novo plano\"." else "Nenhum plano encontrado para a busca.",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp,
                )
            }

            else -> PlanosGrid(
                stats = filteredStats,
                onEdit = { plano ->
                    editingPlano = plano
                    formError = null
                    formOpen = true
                },
            )
        }
    }

    if (formOpen) {
        PlanoFormDialog(
            plano = editingPlano,
            isSaving = isSaving,
            errorMessage = formError,
            onDismiss = {
                if (!isSaving) {
                    formOpen = false
                    editingPlano = null
                }
            },
            onSubmit = { submitForm(it) },
        )
    }
}
