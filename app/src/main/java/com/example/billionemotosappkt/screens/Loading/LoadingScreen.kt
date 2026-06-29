package com.example.billionemotosappkt.screens.Loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface,
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
		modifier = modifier.padding(24.dp),
	) {
		Row(
			modifier = Modifier.padding(20.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			CircularProgressIndicator(modifier = Modifier.height(24.dp))
			Text("Carregando sessão...")
		}
	}
}
