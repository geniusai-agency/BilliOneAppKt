package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.billionemotosappkt.shared.utils.rememberAssetPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotoCategorySelector(
	variant: MotoVariant,
	isSelected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = Color(0xFF1FE36A)

	Card(
		onClick = onClick,
		modifier = modifier.size(width = 104.dp, height = 108.dp),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(
			containerColor = Color(0xFF09110C),
		),
		border = BorderStroke(
			width = if (isSelected) 1.5.dp else 1.dp,
			color = if (isSelected) accent else Color.White.copy(alpha = 0.10f),
		),
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			Image(
				painter = rememberAssetPainter(variant.imageRes),
				contentDescription = variant.title,
				modifier = Modifier
					.align(Alignment.Center)
					.padding(top = 6.dp)
					.size(width = 76.dp, height = 54.dp),
				contentScale = ContentScale.Fit,
			)
			Box(
				modifier = Modifier
					.align(Alignment.TopStart)
					.padding(10.dp)
					.size(8.dp)
					.background(
						color = if (isSelected) accent else Color.White.copy(alpha = 0.12f),
						shape = RoundedCornerShape(99.dp),
					),
			)
			Text(
				text = variant.title,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 8.dp),
				color = if (isSelected) accent else Color.White.copy(alpha = 0.78f),
				fontSize = 11.sp,
				fontWeight = FontWeight.Bold,
				maxLines = 1,
			)
		}
	}
}
