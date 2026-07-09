package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotoModeloSmallTag(
	text: String,
	tint: Color,
) {
	Box(
		modifier = Modifier
			.background(tint.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
			.border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
			.padding(horizontal = 10.dp, vertical = 6.dp),
	) {
		Text(
			text = text,
			color = tint,
			fontSize = 11.sp,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}