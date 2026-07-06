package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.shared.api.MotoResponse
import kotlin.collections.forEach

@Composable
fun MotosList(motos: List<MotoResponse>) {
	// Usamos Column aqui porque o AdminDashboardScreen já tem um scroll global
	Column(
		verticalArrangement = Arrangement.spacedBy(14.dp),
		modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
	) {
		motos.forEach { moto ->
			MotoCard(moto = moto)
		}
		if (motos.isEmpty()) {
			Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
				Text("Nenhuma moto encontrada.", color = Color.White.copy(alpha = 0.4f))
			}
		}
	}
}