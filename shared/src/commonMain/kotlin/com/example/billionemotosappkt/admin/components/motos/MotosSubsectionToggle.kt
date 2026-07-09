package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab

@Composable
fun MotosSubsectionToggle(
	selected: MotoSectionTab,
	onSelected: (MotoSectionTab) -> Unit,
) {
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
		MotosSubsectionChip(
			label = "Frota",
			selected = selected == MotoSectionTab.FROTA,
			onClick = { onSelected(MotoSectionTab.FROTA) },
		)
		MotosSubsectionChip(
			label = "Modelos",
			selected = selected == MotoSectionTab.MODELOS,
			onClick = { onSelected(MotoSectionTab.MODELOS) },
		)
	}
}