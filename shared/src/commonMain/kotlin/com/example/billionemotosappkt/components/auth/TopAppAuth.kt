package com.example.billionemotosappkt.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopAppAuth() {
	Row(
		modifier = Modifier
			.padding(top = 12.dp)
			.fillMaxWidth()
			.background(Color.Transparent),
		horizontalArrangement = Arrangement.Absolute.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Image(
			painter = rememberAssetPainter("logo_billione.png"),
			contentDescription = "Icone Da Billione",
			modifier = Modifier.size(56.dp)
		)
		Text("BRASIL", fontSize = 14.sp, fontWeight = FontWeight.Black)
	}
}
