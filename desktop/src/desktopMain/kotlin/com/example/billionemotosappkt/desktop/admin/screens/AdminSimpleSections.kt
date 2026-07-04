package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.components.ChipRow
import com.example.billionemotosappkt.desktop.admin.components.DetailGrid
import com.example.billionemotosappkt.desktop.admin.components.SectionShell

@Composable
fun ClientesSectionPlaceholder() = SectionShell(
    title = "Clientes",
    subtitle = "Cadastro, busca e ficha do cliente.",
    body = "Lista, filtros, perfil e aprovações.",
    chips = listOf("Busca", "CPF", "Status", "Cidade", "Aprovação"),
    stats = listOf("Aprovados", "Pendentes", "Reprovados"),
    content = {
        TwoUpCards(
            first = { modifier ->
                AdminSectionCard(
                    title = "Carteira ativa",
                    subtitle = "Clientes com vínculo de contrato",
                    icon = Icons.Default.Groups,
                    accent = Color(0xFF20E65B),
                    value = "0",
                    modifier = modifier,
                )
            },
            second = { modifier ->
                AdminSectionCard(
                    title = "Análises",
                    subtitle = "Processos em fila para avaliação",
                    icon = Icons.Default.Analytics,
                    accent = Color(0xFFFFB300),
                    value = "0",
                    modifier = modifier,
                )
            },
        )
        ChipRow(listOf("Todos", "Parceiros", "Clientes", "Pendentes"))
        DetailGrid(
            listOf(
                "Nenhum cliente carregado no momento.",
                "Os filtros desta tela seguem a lógica da API de clientes.",
                "Campos principais: nome, CPF, email, telefone, cidade e status.",
            ),
        )
    },
)

@Composable
fun RastreamentoSection() = SectionShell(
    title = "Rastreamento",
    subtitle = "Mapa, alertas e localização dos rastreadores.",
    body = "Mapa de rastreadores, alertas e localização em tempo real.",
    chips = listOf("Mapa", "Alertas", "Rotas", "Histórico"),
    stats = listOf("Ativos", "Offline", "Alertas"),
    content = {
        TwoUpCards(
            first = { modifier ->
                AdminSectionCard(
                    title = "Rastreadores ativos",
                    subtitle = "Dispositivos online",
                    icon = Icons.Default.LocationOn,
                    accent = Color(0xFF20E65B),
                    value = "0",
                    modifier = modifier,
                )
            },
            second = { modifier ->
                AdminSectionCard(
                    title = "Alertas",
                    subtitle = "Ocorrências em monitoramento",
                    icon = Icons.Default.Analytics,
                    accent = Color(0xFFFF4A4A),
                    value = "0",
                    modifier = modifier,
                )
            },
        )
        DetailGrid(
            listOf(
                "Mapa operacional e histórico de eventos devem entrar aqui.",
                "Esse bloco só mostra o esqueleto visual da tela.",
            ),
        )
    },
)

@Composable
private fun TwoUpCards(
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 16.dp
        val cardWidth = (maxWidth - gap) / 2
        if (cardWidth >= 280.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap), modifier = Modifier.fillMaxWidth()) {
                first(Modifier.width(cardWidth))
                second(Modifier.width(cardWidth))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                first(Modifier.fillMaxWidth())
                second(Modifier.fillMaxWidth())
            }
        }
    }
}
