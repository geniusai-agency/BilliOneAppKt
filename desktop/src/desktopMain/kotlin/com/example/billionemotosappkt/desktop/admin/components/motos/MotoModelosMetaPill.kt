package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotoModelosMetaPill(
	label: String,
	value: String,
	tint: Color,
) {
	Box(
		modifier = Modifier
			.background(Color(0xFF0E1411), RoundedCornerShape(14.dp))
			.border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
			.padding(horizontal = 14.dp, vertical = 10.dp),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(8.dp)
					.clip(RoundedCornerShape(999.dp))
					.background(tint),
			)
			Column {
				Text(text = label, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
				Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
			}
		}
	}
}