package com.example.billionemotosappkt.components.motos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlanOptionGroup(
	title: String,
	options: List<PlanOption>,
	selectedIndex: Int,
	onSelected: (Int) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = title,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 13.sp,
			fontWeight = FontWeight.Black,
		)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			options.forEachIndexed { index, option ->
				PlanOptionCard(
					option = option,
					isSelected = index == selectedIndex,
					modifier = Modifier.weight(1f),
					onClick = { onSelected(index) },
				)
			}
		}
	}
}