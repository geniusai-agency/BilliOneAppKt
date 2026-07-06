package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun MotoModeloFormSection(
	title: String,
	subtitle: String,
	content: @Composable ColumnScope.() -> Unit,
) {
	Box(
		modifier = Modifier.fillMaxWidth(),
		contentAlignment = Alignment.TopStart,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(18.dp))
				.background(Color(0xFF0D1210))
				.border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
				Text(
					text = title,
					color = Color.White,
					fontSize = 13.sp,
					fontWeight = FontWeight.Bold
				)
				Text(text = subtitle, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
			}
			content()
		}
	}
}