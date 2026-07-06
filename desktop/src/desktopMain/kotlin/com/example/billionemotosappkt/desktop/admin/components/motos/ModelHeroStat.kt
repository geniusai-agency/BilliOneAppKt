package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter

@Composable
fun ModelHeroStat(
	title: String,
	value: String,
	modifier: Modifier = Modifier,
	imageResource: String,
	apiBaseUrl: String,
	apiAccessToken: String?,
) {
	Box(
		modifier = modifier
			.height(112.dp)
			.clip(RoundedCornerShape(22.dp))
			.background(Color(0xFF0D1210))
			.border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp)),
	) {
		Image(
			painter = desktopImagePainter(imageResource, apiBaseUrl, apiAccessToken),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(),
		)
		Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.58f)))
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(14.dp),
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(
					text = value,
					color = Color.White,
					fontSize = 18.sp,
					fontWeight = FontWeight.Black
				)
				Text(
					text = if (title == "Imagem") "Visual da ficha" else "Especificação",
					color = Color.White.copy(alpha = 0.62f),
					fontSize = 11.sp,
				)
			}
		}
	}
}