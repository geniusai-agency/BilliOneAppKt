package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.admin.components.AdminSidebar
import com.example.billionemotosappkt.desktop.admin.components.CompactNavigationBar
import com.example.billionemotosappkt.desktop.admin.components.SectionContent
import com.example.billionemotosappkt.desktop.admin.components.TopCommandBar
import com.example.billionemotosappkt.desktop.admin.model.AdminSection
import com.example.billionemotosappkt.desktop.admin.model.adminDashboardSnapshot
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Composable
fun AdminDashboardScreen(
    authContext: AuthenticationContextResponse?,
    api: BillioneMotosApi,
    onOpenSite: () -> Unit,
    onLogout: () -> Unit,
) {
    val snapshot = remember { adminDashboardSnapshot() }
    var section by remember { mutableStateOf(AdminSection.DASHBOARD) }

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
                    item {
                        SectionContent(section = section, snapshot = snapshot, compact = true, api = api)
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    AdminSidebar(
                        authContext = authContext,
                        section = section,
                        onSectionChange = { section = it },
                        onLogout = onLogout,
                        modifier = Modifier.width(252.dp),
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
                        item {
                            SectionContent(section = section, snapshot = snapshot, compact = false, api = api)
                        }
                    }
                }
            }
        }
    }
}
