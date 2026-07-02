package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.components.AdminSectionCard
import com.example.billionemotosappkt.desktop.admin.components.ChipRow
import com.example.billionemotosappkt.desktop.admin.components.DetailGrid
import com.example.billionemotosappkt.desktop.admin.components.SectionShell
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot

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
fun MotosSection() = SectionShell(
    title = "Motos",
    subtitle = "Frota, disponibilidade e manutenção.",
    body = "Frota, disponibilidade, placa e status operacional.",
    chips = listOf("Disponível", "Alugada", "Manutenção", "Bloqueada"),
    stats = listOf("Total", "Disponíveis", "Em manutenção"),
    content = {
        TwoUpCards(
            first = { modifier ->
                AdminSectionCard(
                    title = "Frota",
                    subtitle = "Unidades operacionais cadastradas",
                    icon = Icons.Default.DirectionsBike,
                    accent = Color(0xFF20E65B),
                    value = "0",
                    modifier = modifier,
                )
            },
            second = { modifier ->
                AdminSectionCard(
                    title = "Manutenção",
                    subtitle = "Motos em fila ou com alerta",
                    icon = Icons.Default.Tune,
                    accent = Color(0xFFFFB300),
                    value = "0",
                    modifier = modifier,
                )
            },
        )
        DetailGrid(
            listOf(
                "Mostre placa, modelo, status e localização operacional.",
                "A página pode ser ligada aos endpoints de motos e rastreamento.",
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
fun ContratosSection(snapshot: AdminDashboardSnapshot) = SectionShell(
    title = "Contratos",
    subtitle = "Assinatura, vencimentos e pagamentos.",
    body = "Vencimentos, pagamentos e contratos ativos.",
    chips = listOf("Ativos", "Vencendo", "Pagamentos", "PDF"),
    stats = listOf("${snapshot.activeContracts} ativos", "${snapshot.dueIn60Days} vencendo", snapshot.monthlyReceived),
    content = {
        TwoUpCards(
            first = { modifier ->
                AdminSectionCard(
                    title = "Contratos ativos",
                    subtitle = "Em andamento",
                    icon = Icons.Default.WorkOutline,
                    accent = Color(0xFF20E65B),
                    value = snapshot.activeContracts.toString(),
                    modifier = modifier,
                )
            },
            second = { modifier ->
                AdminSectionCard(
                    title = "Recebimento",
                    subtitle = "Valor confirmado no mês",
                    icon = Icons.Default.AttachMoney,
                    accent = Color(0xFF20E65B),
                    value = snapshot.monthlyReceived,
                    modifier = modifier,
                )
            },
        )
        DetailGrid(
            listOf(
                "Tela alinhada com o web: vencimentos, status e pagamentos no centro.",
                "Aqui você pode plugar a listagem real de contratos da API.",
            ),
        )
    },
)

@Composable
fun ContratosJuridicosSection() = SectionShell(
    title = "Contratos Jurídicos",
    subtitle = "Termos, aditivos e documentos legais.",
    body = "Editor de termos, aditivos e documentos legais.",
    chips = listOf("Editor", "Aditivo", "Assinatura", "Versionamento"),
    stats = listOf("Rascunhos", "Publicados", "Assinados"),
    content = {
        DetailGrid(
            listOf(
                "Estrutura visual pensada para documentos jurídicos e versionamento.",
                "Pode receber editor, upload e histórico de termos.",
            ),
        )
    },
)

@Composable
fun FinanceiroSection(snapshot: AdminDashboardSnapshot) = SectionShell(
    title = "Financeiro",
    subtitle = "Receitas, despesas e fluxo de caixa.",
    body = "Receitas, despesas, cobranças e fluxo de caixa.",
    chips = listOf("Receita", "Despesa", "Caixa", "Fluxo"),
    stats = listOf(snapshot.monthlyBilling, snapshot.monthlyReceived, snapshot.openItems),
    content = {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            AdminSectionCard(
                title = "Faturamento",
                subtitle = "Total do período",
                icon = Icons.Default.AttachMoney,
                accent = Color(0xFF20E65B),
                value = snapshot.monthlyBilling,
            )
            AdminSectionCard(
                title = "Em aberto",
                subtitle = "Pendências financeiras",
                icon = Icons.Default.Wallet,
                accent = Color(0xFFFF4A4A),
                value = snapshot.openItems,
            )
        }
        DetailGrid(
            listOf(
                "Esse bloco segue o estilo do dashboard web e pode ser ligado ao gráfico real.",
                "Use esta área para relatórios e resumo mensal.",
            ),
        )
    },
)

@Composable
fun SantanderSection() = SectionShell(
    title = "Santander",
    subtitle = "Boletos, remessas e retorno CNAB.",
    body = "Boletos, remessas, retorno CNAB e conciliação bancária.",
    chips = listOf("CNAB", "Boletos", "Retorno", "Baixa"),
    stats = listOf("Arquivos", "Pendentes", "Processados"),
    content = {
        DetailGrid(
            listOf(
                "Área reservada para integração bancária e conciliação.",
                "Aqui entra a operação de boletos e retorno CNAB.",
            ),
        )
    },
)

@Composable
fun ManutencaoSection() = SectionShell(
    title = "Manutenção",
    subtitle = "OS, agenda e ordens abertas.",
    body = "Ordens abertas, agenda e histórico de manutenção.",
    chips = listOf("Agenda", "Abertas", "Pecas", "Executadas"),
    stats = listOf("OS abertas", "Em execução", "Finalizadas"),
    content = {
        DetailGrid(
            listOf(
                "Fluxo de manutenção visualmente consistente com o restante do admin.",
                "Integre aqui a lista de serviços e ordens abertas.",
            ),
        )
    },
)

@Composable
fun OrdemServicoSection() = SectionShell(
    title = "Ordem de Serviço",
    subtitle = "Detalhes, peças e etapas da OS.",
    body = "Detalhes da OS, peças e etapas operacionais.",
    chips = listOf("Detalhes", "Peças", "Etapas", "Notas"),
    stats = listOf("Aberta", "Em fila", "Concluída"),
    content = {
        DetailGrid(
            listOf(
                "Tela preparada para o detalhe operacional da OS.",
                "Pode receber timeline, checklist e anexos.",
            ),
        )
    },
)

@Composable
fun RelatoriosSection() = SectionShell(
    title = "Relatórios",
    subtitle = "Indicadores e exportações da operação.",
    body = "Indicadores, exportações e visão gerencial.",
    chips = listOf("PDF", "CSV", "Período", "KPIs"),
    stats = listOf("Mensal", "Trimestral", "Anual"),
    content = {
        DetailGrid(
            listOf(
                "Espaço para gráficos, exportação e indicadores chave.",
                "Visual alinhado ao dashboard principal.",
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
