package com.example.billionemotosappkt.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.data.auth.createBillioneMotosAuthStack
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    BilliOneMotosAppKtTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current.applicationContext
            val container = remember {
                createBillioneMotosAuthStack(
                    context = context,
                    baseUrl = BILLIONE_API_BASE_URL,
                )
            }
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.factory(container.sessionManager),
            )
            
            AuthScreen(authViewModel)
        }
    }
}
