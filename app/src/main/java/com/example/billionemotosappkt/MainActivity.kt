package com.example.billionemotosappkt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.billionemotosappkt.screens.auth.AuthScreen
import com.example.billionemotosappkt.screens.auth.AuthViewModel
import com.example.billionemotosappkt.data.auth.createBillioneMotosAuthStack
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme
import com.example.billionemotosappkt.shared.api.BILLIONE_API_BASE_URL
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billionemotosappkt.screens.Screens

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
		enableEdgeToEdge()
		setContent {
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
			BilliOneMotosAppKtTheme {
				Screens(
					viewModel = authViewModel,
					api = container.api,
				)
			}
		}
	}
}
