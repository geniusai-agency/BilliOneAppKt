package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.runtime.Composable
import com.example.billionemotosappkt.desktop.admin.clientes.screens.ClientesSection
import com.example.billionemotosappkt.desktop.admin.model.AdminSection
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.desktop.admin.screens.ContratosJuridicosSection
import com.example.billionemotosappkt.desktop.admin.screens.ContratosSection
import com.example.billionemotosappkt.desktop.admin.screens.FinanceiroSection
import com.example.billionemotosappkt.desktop.admin.screens.ManutencaoSection
import com.example.billionemotosappkt.desktop.admin.screens.MotosSection
import com.example.billionemotosappkt.desktop.admin.screens.OrdemServicoSection
import com.example.billionemotosappkt.desktop.admin.screens.RastreamentoSection
import com.example.billionemotosappkt.desktop.admin.screens.RelatoriosSection
import com.example.billionemotosappkt.desktop.admin.screens.SantanderSection
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun SectionContent(
    section: AdminSection,
    snapshot: com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot,
    compact: Boolean,
    api: BillioneMotosApi,
    apiBaseUrl: String,
    apiAccessToken: String?,
    motoTab: MotoSectionTab,
    onMotoTabChange: (MotoSectionTab) -> Unit,
) {
    when (section) {
        AdminSection.DASHBOARD -> DashboardSection(snapshot = snapshot, compact = compact)
        AdminSection.CLIENTES -> ClientesSection(api = api, compact = compact)
        AdminSection.MOTOS -> MotosSection(
            api = api,
            apiBaseUrl = apiBaseUrl,
            apiAccessToken = apiAccessToken,
            selectedTab = motoTab,
            onTabChange = onMotoTabChange,
        )
        AdminSection.RASTREAMENTO -> RastreamentoSection()
        AdminSection.CONTRATOS -> ContratosSection(snapshot = snapshot, api = api)
        AdminSection.CONTRATOS_JURIDICOS -> ContratosJuridicosSection()
        AdminSection.FINANCEIRO -> FinanceiroSection(snapshot = snapshot)
        AdminSection.SANTANDER -> SantanderSection()
        AdminSection.MANUTENCAO -> ManutencaoSection()
        AdminSection.ORDEM_SERVICO -> OrdemServicoSection()
        AdminSection.RELATORIOS -> RelatoriosSection()
    }
}
