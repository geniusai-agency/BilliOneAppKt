package com.example.billionemotosappkt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.screens.Screens
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import com.example.billionemotosappkt.shared.auth.BillioneMotosAppContainer
import com.example.billionemotosappkt.shared.utils.AndroidPlatformContext
import com.example.billionemotosappkt.data.AndroidTokenSessionStore
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
		enableEdgeToEdge()
		setContent {
			val context = LocalContext.current.applicationContext
			val container = remember {
				AndroidPlatformContext.appContext = context
				val tokenStore = AndroidTokenSessionStore(context)
				BillioneMotosAppContainer(
					tokenStore = tokenStore,
					baseUrl = BILLIONE_API_BASE_URL,
				)
			}
			val authViewModel: AuthViewModel = viewModel(
				factory = object : androidx.lifecycle.ViewModelProvider.Factory {
					@Suppress("UNCHECKED_CAST")
					override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
						return AuthViewModel(container.sessionManager) as T
					}
				}
			)
			BilliOneMotosAppKtTheme {
				Screens(
					viewModel = authViewModel,
					api = container.api,
				)
			}
		}
	}
}
