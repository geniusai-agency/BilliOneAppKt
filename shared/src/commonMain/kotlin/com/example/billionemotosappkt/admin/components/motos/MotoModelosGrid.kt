package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import kotlin.collections.chunked
import kotlin.collections.forEach


@Composable
fun MotoModelosGrid(
	models: List<MotoModeloResponse>,
	onView: (MotoModeloResponse) -> Unit,
	onEdit: (MotoModeloResponse) -> Unit,
	onDelete: (MotoModeloResponse) -> Unit,
	apiBaseUrl: String,
	apiAccessToken: String?,
) {
	BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
		val columns = if (maxWidth >= 1280.dp) 2 else 1
		val rows = models.chunked(columns)
		
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			rows.forEach { rowItems ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
				) {
					rowItems.forEach { model ->
						MotoModeloCard(
							model = model,
							onView = { onView(model) },
							onEdit = { onEdit(model) },
							onDelete = { onDelete(model) },
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
							modifier = Modifier.weight(1f),
						)
					}
					repeat(columns - rowItems.size) {
						Spacer(modifier = Modifier.weight(1f))
					}
				}
			}
		}
	}
}