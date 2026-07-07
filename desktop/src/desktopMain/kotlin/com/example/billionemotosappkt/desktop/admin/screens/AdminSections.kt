package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoModelosGrid
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoModelosMetaPill
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.MotoStatus
import kotlinx.coroutines.launch

@Composable
fun MotoModelosSection(api: BillioneMotosApi, apiBaseUrl: String, apiAccessToken: String?) {
	val scope = rememberCoroutineScope()
	var isLoading by remember { mutableStateOf(true) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	var query by remember { mutableStateOf("") }
	var models by remember { mutableStateOf<List<MotoModeloResponse>>(emptyList()) }
	var selectedModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var creatingModel by remember { mutableStateOf(false) }
	var editingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var deletingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
	var actionMessage by remember { mutableStateOf<String?>(null) }
	
	LaunchedEffect(api) {
		isLoading = true
		runCatching { api.motos.modelos() }
			.onSuccess {
				models = it
				errorMessage = null
			}
			.onFailure {
				errorMessage = it.message ?: "Erro ao carregar modelos"
			}
		isLoading = false
	}
	
	val filteredModels = remember(models, query) {
		val normalized = query.trim()
		if (normalized.isBlank()) {
			models
		} else {
			models.filter { model ->
				listOfNotNull(
					model.marca,
					model.nome,
					model.modelo,
					model.categoria,
					model.tipo,
					model.combustivel,
					model.codigoFipe,
					model.descricao,
				).any { it.contains(normalized, ignoreCase = true) }
			}
		}
	}
	
	val totalBrands = remember(models) {
		models.mapNotNull { it.marca?.takeIf(String::isNotBlank) }.distinct().size
	}
	
	val trackedModels = remember(models) {
		models.count { it.possuiRastreador }
	}
	
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(24.dp),
	) {
        // Título e Descrição
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Modelos",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Leitura, edição e exclusão de modelos cadastrados.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
            )
        }

        // Stats Cards (Mais retangulares/horizontais)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
            StatsBox(
                title = "Modelos carregados",
                subtitle = "Total disponível no catálogo",
                value = models.size.toString(),
                accent = Color(0xFF20E65B),
                modifier = Modifier.weight(1f)
            )
            StatsBox(
                title = "Com rastreador",
                subtitle = "Modelos com suporte ativo",
                value = trackedModels.toString(),
                accent = Color(0xFF7DD3FC),
                modifier = Modifier.weight(1f)
            )
		}
		
        // Barra de Ferramentas: Meta Pills + Search + Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MotoModelosMetaPill("Marcas", totalBrands.toString(), Color(0xFF20E65B))
                MotoModelosMetaPill("Exibidos", filteredModels.size.toString(), Color(0xFF7DD3FC))
                MotoModelosMetaPill("Catálogo", models.size.toString(), Color(0xFFFFB300))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.width(300.dp).height(56.dp),
                    placeholder = { Text("Buscar modelo...", color = Color.White.copy(0.4f), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(0.4f), modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF20E65B).copy(0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(0.02f),
                        unfocusedContainerColor = Color.White.copy(0.02f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Button(
                    onClick = { creatingModel = true },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Novo modelo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
		
		if (isLoading) {
			Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
				CircularProgressIndicator(color = Color(0xFF20E65B))
			}
		} else if (errorMessage != null) {
			Card(
				colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.1f)),
				border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f))
            ) {
				Text(text = errorMessage!!, color = Color(0xFFFF4A4A), modifier = Modifier.padding(16.dp))
			}
		} else {
			MotoModelosGrid(
				models = filteredModels,
				onView = { selectedModel = it },
				onEdit = { editingModel = it },
				onDelete = { deletingModel = it },
				apiBaseUrl = apiBaseUrl,
				apiAccessToken = apiAccessToken
			)
		}
	}
}

@Composable
private fun StatsBox(
    title: String,
    subtitle: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(0.05f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.White.copy(0.4f), fontSize = 12.sp)
            }
            Text(value, color = accent, fontSize = 32.sp, fontWeight = FontWeight.Black)
        }
    }
}
