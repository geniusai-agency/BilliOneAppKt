package com.example.billionemotosappkt.desktop.admin.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AdminDashboardSnapshot(
    val activeContracts: Int,
    val dueIn60Days: Int,
    val monthlyBilling: String,
    val monthlyReceived: String,
    val openItems: String,
    val contractsDue: List<AdminListItem>,
    val recentWorkOrders: List<AdminListItem>,
    val financeMonths: List<AdminChartPoint>,
)

data class AdminListItem(
    val title: String,
    val subtitle: String,
    val badge: String,
)

data class AdminChartPoint(
    val label: String,
    val value: Int,
)

data class AdminMetric(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val tint: Color,
)

data class AdminNavItem(
    val section: AdminSection,
    val label: String,
    val icon: ImageVector,
)

enum class AdminSection(
    val title: String,
    val subtitle: String,
) {
    DASHBOARD("Visão de Gestão", "Resumo operacional de contratos e ordens de serviço."),
    CLIENTES("Clientes", "Cadastro, busca e ficha do cliente."),
    MOTOS("Motos", "Frota, disponibilidade e manutenção."),
    RASTREAMENTO("Rastreamento", "Mapa, alertas e localização dos rastreadores."),
    CONTRATOS("Contratos", "Assinatura, vencimentos e pagamentos."),
    CONTRATOS_JURIDICOS("Contratos Jurídicos", "Termos, aditivos e documentos legais."),
    FINANCEIRO("Financeiro", "Receitas, despesas e fluxo de caixa."),
    SANTANDER("Santander", "Boletos, remessas e retorno CNAB."),
    MANUTENCAO("Manutenção", "OS, agenda e ordens abertas."),
    ORDEM_SERVICO("Ordem de Serviço", "Detalhes, peças e etapas da OS."),
    RELATORIOS("Relatórios", "Indicadores e exportações da operação.");
}

enum class MotoSectionTab(
    val label: String,
) {
    FROTA("Frota"),
    MODELOS("Modelos"),
}

val adminNavItems = listOf(
    AdminNavItem(AdminSection.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    AdminNavItem(AdminSection.CLIENTES, "Clientes", Icons.Default.Groups),
    AdminNavItem(AdminSection.MOTOS, "Motos", Icons.Default.DirectionsBike),
    AdminNavItem(AdminSection.RASTREAMENTO, "Rastreamento", Icons.Default.LocationOn),
    AdminNavItem(AdminSection.CONTRATOS, "Contratos", Icons.Default.FileCopy),
    AdminNavItem(AdminSection.CONTRATOS_JURIDICOS, "Contratos Jurídicos", Icons.Default.WorkOutline),
    AdminNavItem(AdminSection.FINANCEIRO, "Financeiro", Icons.Default.AttachMoney),
    AdminNavItem(AdminSection.SANTANDER, "Santander", Icons.Default.Wallet),
    AdminNavItem(AdminSection.MANUTENCAO, "Manutenção", Icons.Default.Tune),
    AdminNavItem(AdminSection.ORDEM_SERVICO, "Ordem de Serviço", Icons.Default.Inventory2),
    AdminNavItem(AdminSection.RELATORIOS, "Relatórios", Icons.Default.Analytics),
)

fun adminDashboardSnapshot() = AdminDashboardSnapshot(
    activeContracts = 0,
    dueIn60Days = 0,
    monthlyBilling = "R$ 0",
    monthlyReceived = "R$ 0",
    openItems = "R$ 0",
    contractsDue = emptyList(),
    recentWorkOrders = emptyList(),
    financeMonths = listOf(
        AdminChartPoint("Fev/26", 0),
        AdminChartPoint("Mar/26", 0),
        AdminChartPoint("Abr/26", 0),
        AdminChartPoint("Mai/26", 0),
        AdminChartPoint("Jun/26", 0),
        AdminChartPoint("Jul/26", 0),
    ),
)

fun adminMetrics(snapshot: AdminDashboardSnapshot): List<AdminMetric> {
    return listOf(
        AdminMetric("Contratos ativos", snapshot.activeContracts.toString(), Icons.Default.FileCopy, Color(0xFF20E65B)),
        AdminMetric("Vencendo em 60 dias", snapshot.dueIn60Days.toString(), Icons.Default.NotificationsNone, Color(0xFFFFB300)),
        AdminMetric("Faturamento do mês", snapshot.monthlyBilling, Icons.Default.AttachMoney, Color(0xFF20E65B)),
        AdminMetric("Recebido no mês", snapshot.monthlyReceived, Icons.Default.Dashboard, Color(0xFF20E65B)),
        AdminMetric("Em aberto", snapshot.openItems, Icons.Default.Analytics, Color(0xFFFF4A4A)),
    )
}
