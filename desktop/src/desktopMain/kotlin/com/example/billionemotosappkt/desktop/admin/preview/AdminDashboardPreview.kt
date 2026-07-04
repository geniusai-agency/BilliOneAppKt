package com.example.billionemotosappkt.desktop.admin.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.desktop.admin.screens.AdminDashboardScreen
import com.example.billionemotosappkt.shared.api.ApiConfig
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

@Preview(showBackground = true, widthDp = 1440, heightDp = 980)
@Composable
private fun AdminDashboardPreview() {
    val api = BillioneMotosApi(ApiConfig(baseUrl = "http://localhost"))
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize().background(Color(0xFF050806))) {
            AdminDashboardScreen(
                authContext = null,
                api = api,
                apiBaseUrl = "http://localhost",
                apiAccessToken = null,
                onOpenSite = {},
                onLogout = {},
            )
        }
    }
}
