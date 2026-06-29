package com.example.billionemotosappkt.screens.dashboard.motos

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.motos.MotosScreen
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Preview(
	name = "Motos Dark",
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun MotosScreenDarkPreview() {
	BilliOneMotosAppKtTheme {
		MotosScreen(
			onBack = {},
			onRentClick = {},
			onOndeEstamosClick = {},
			onPlanosClick = {},
		)
	}
}
