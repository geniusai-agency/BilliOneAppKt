package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.admin.model.AdminNavItem
import com.example.billionemotosappkt.desktop.admin.model.AdminSection
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.desktop.admin.model.adminNavItems

@Composable
fun AdminSidebar(
    authContext: AuthenticationContextResponse?,
    section: AdminSection,
    motoTab: MotoSectionTab,
    onSectionChange: (AdminSection) -> Unit,
    onMotoTabChange: (MotoSectionTab) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF080D0A))
            .border(1.dp, Color.White.copy(alpha = 0.08f)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF20E65B), Color(0xFFD4AF37)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "B",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                }
                Column {
                    Text(
                        text = "ADMIN SUITE",
                        color = Color(0xFFB8C3BC),
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp,
                    )
                    Text(
                        text = "Billione Motos",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Text(
                text = "NAVEGAÇÃO",
                color = Color(0xFF77827B),
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                adminNavItems.forEach { item ->
                    SidebarNavItem(
                        item = item,
                        selected = item.section == section,
                        onClick = { onSectionChange(item.section) },
                    )
                    if (item.section == AdminSection.MOTOS) {
                        MotosSidebarSubitems(
                            visible = section == AdminSection.MOTOS,
                            selectedTab = motoTab,
                            onTabChange = onMotoTabChange,
                        )
                    }
                }
            }
        }

        SidebarFooter(authContext = authContext, onLogout = onLogout)
    }
}

@Composable
fun CompactNavigationBar(
    section: AdminSection,
    onSectionChange: (AdminSection) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        adminNavItems.forEach { item ->
            val selected = item.section == section
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Color(0xFF0F1711) else Color(0xFF0A0F0B))
                    .border(
                        1.dp,
                        if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.07f),
                        RoundedCornerShape(999.dp),
                    )
                    .clickable { onSectionChange(item.section) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = item.label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.84f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun MotoSubsectionBar(
    selectedTab: MotoSectionTab,
    onTabChange: (MotoSectionTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MotoSectionTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Color(0xFF0F1711) else Color(0xFF0A0F0B))
                    .border(
                        1.dp,
                        if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.07f),
                        RoundedCornerShape(999.dp),
                    )
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = tab.label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.84f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun SidebarFooter(
    authContext: AuthenticationContextResponse?,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0E1411))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF20E65B)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = authContext?.user?.nome?.take(2)?.uppercase() ?: "GE",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Column {
                Text(
                    text = authContext?.user?.nome ?: "geniusagencyai",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "admin",
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 11.sp,
                )
            }
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Sair")
        }
    }
}

@Composable
fun TopCommandBar(
    authContext: AuthenticationContextResponse?,
    section: AdminSection,
    compact: Boolean,
    onOpenSite: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconButton(
            onClick = onOpenSite,
            modifier = Modifier
                .size(if (compact) 42.dp else 46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F1711))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp)),
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(20.dp),
            )
        }

        SearchBar(
            modifier = if (compact) Modifier.fillMaxWidth() else Modifier.widthIn(min = 340.dp, max = 620.dp),
        )

        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F1711)),
            ) {
                Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF20E65B)),
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF20E65B), Color(0xFF16B44A)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = authContext?.user?.nome?.take(2)?.uppercase() ?: section.title.take(2).uppercase(),
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0D1410))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Buscar clientes, contratos, motos...",
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ManagementHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Visão de Gestão",
            color = Color.White,
            fontSize = 31.sp,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = "Resumo operacional de contratos e ordens de serviço.",
            color = Color.White.copy(alpha = 0.56f),
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun SidebarNavItem(
    item: AdminNavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF0F1711) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Color(0xFF20E65B),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = item.label,
            color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.92f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MotosSidebarSubitems(
    visible: Boolean,
    selectedTab: MotoSectionTab,
    onTabChange: (MotoSectionTab) -> Unit,
) {
    if (!visible) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MotoSectionTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) Color(0xFF0F1711) else Color.Transparent)
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.35f)),
                )
                Text(
                    text = tab.label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(15.dp),
                )
            }
        }
    }
}
