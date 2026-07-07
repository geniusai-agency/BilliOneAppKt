package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.components.AdminSidebar
import com.example.billionemotosappkt.desktop.admin.components.CompactNavigationBar
import com.example.billionemotosappkt.desktop.admin.components.MotoSubsectionBar
import com.example.billionemotosappkt.desktop.admin.components.SectionContent
import com.example.billionemotosappkt.desktop.admin.components.TopCommandBar
import com.example.billionemotosappkt.desktop.admin.model.AdminSection
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.desktop.admin.model.adminDashboardSnapshot
import com.example.billionemotosappkt.desktop.admin.repository.AdminDashboardRepository
import com.example.billionemotosappkt.desktop.admin.clientes.screens.ClienteDetalheScreen
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun AdminDashboardScreen(
    authContext: AuthenticationContextResponse?,
    api: BillioneMotosApi,
    apiBaseUrl: String,
    apiAccessToken: String?,
    onOpenSite: () -> Unit,
    onLogout: () -> Unit,
) {
    var snapshot by remember { mutableStateOf(adminDashboardSnapshot()) }
    var section by remember { mutableStateOf(AdminSection.DASHBOARD) }
    var motoTab by remember { mutableStateOf(MotoSectionTab.FROTA) }
    var clientDetail by remember { mutableStateOf<ClienteListItem?>(null) }

    LaunchedEffect(api) {
        runCatching { AdminDashboardRepository(api).loadSnapshot() }
            .onSuccess { snapshot = it }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF050806), Color(0xFF0A100C), Color(0xFF050806)),
                ),
            ),
    ) {
        val compact = maxWidth < 1180.dp

        Surface(color = Color.Transparent, modifier = Modifier.fillMaxSize()) {
            if (compact) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        TopCommandBar(
                            authContext = authContext,
                            section = section,
                            compact = true,
                            onOpenSite = onOpenSite,
                        )
                    }
                    item {
                        CompactNavigationBar(
                            section = section,
                            onSectionChange = { section = it },
                        )
                    }
                    if (section == AdminSection.MOTOS) {
                        item {
                            MotoSubsectionBar(
                                selectedTab = motoTab,
                                onTabChange = { motoTab = it },
                            )
                        }
                    }
                    item {
                        SectionContent(
                            section = section,
                            snapshot = snapshot,
                            compact = true,
                            api = api,
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                            motoTab = motoTab,
                            onMotoTabChange = { motoTab = it },
                            onOpenClienteDetail = { clientDetail = it },
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    AdminSidebar(
                        authContext = authContext,
                        section = section,
                        motoTab = motoTab,
                        onSectionChange = { section = it },
                        onMotoTabChange = { motoTab = it },
                        onLogout = onLogout,
                        modifier = Modifier.width(252.dp).fillMaxHeight(),
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        item {
                            TopCommandBar(
                                authContext = authContext,
                                section = section,
                                compact = false,
                                onOpenSite = onOpenSite,
                            )
                        }

                        if (section == AdminSection.MOTOS) {
                            item {
                                MotoSubsectionBar(
                                    selectedTab = motoTab,
                                    onTabChange = { motoTab = it },
                                )
                            }
                        }

                        item {
                            SectionContent(
                                section = section,
                                snapshot = snapshot,
                                compact = false,
                                api = api,
                                apiBaseUrl = apiBaseUrl,
                                apiAccessToken = apiAccessToken,
                                motoTab = motoTab,
                                onMotoTabChange = { motoTab = it },
                                onOpenClienteDetail = { clientDetail = it },
                            )
                        }
                    }
                }
            }
        }
    }

    clientDetail?.let { cliente ->
        Dialog(
            onDismissRequest = { clientDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .fillMaxHeight(0.94f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF060907),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                ClienteDetalheScreen(
                    cliente = cliente,
                    api = api,
                    onBack = { clientDetail = null },
                )
            }
        }
    }
}
