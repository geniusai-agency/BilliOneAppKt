package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionLinkButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		modifier = modifier,
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.16f)),
		border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
	) {
		Row(
			modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = text,
				color = MaterialTheme.colorScheme.onSurface,
				fontSize = 13.sp,
				fontWeight = FontWeight.SemiBold,
			)
			Spacer(modifier = Modifier.width(10.dp))
			Icon(
				imageVector = Icons.Filled.ChevronRight,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
			)
		}
	}
}
