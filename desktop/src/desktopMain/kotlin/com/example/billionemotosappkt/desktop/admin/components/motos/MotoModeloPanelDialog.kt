package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.data.MotoModeloFormState
import com.example.billionemotosappkt.desktop.admin.data.toFormState
import com.example.billionemotosappkt.desktop.admin.`fun`.getMotoHeroImage
import com.example.billionemotosappkt.desktop.admin.`fun`.resolveImageSource
import com.example.billionemotosappkt.desktop.admin.screens.getMotoModelCardImage
import com.example.billionemotosappkt.shared.api.MotoModeloResponse

@Composable
fun MotoModeloPanelDialog(
	title: String,
	model: MotoModeloResponse?,
	onDismiss: () -> Unit,
	onSubmit: ((MotoModeloFormState) -> Unit)?,
	submitLabel: String,
	apiBaseUrl: String,
	apiAccessToken: String?,
) {
	var form by remember(model?.id, title) {
		mutableStateOf(
			model?.toFormState() ?: MotoModeloFormState(),
		)
	}
	val heroResource = remember(form.imagemReferenciaUrl, form.marca, form.modelo, apiBaseUrl) {
		resolveImageSource(form.imagemReferenciaUrl, apiBaseUrl).ifBlank {
			getMotoHeroImage(form.marca, form.modelo)
		}
	}
	val cardResource = remember(form.imagemReferenciaUrl, form.marca, form.modelo, apiBaseUrl) {
		resolveImageSource(form.imagemReferenciaUrl, apiBaseUrl).ifBlank {
			getMotoModelCardImage(form.marca, form.modelo)
		}
	}
	
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth(0.88f)
				.fillMaxHeight(0.88f),
			shape = RoundedCornerShape(30.dp),
			color = Color(0xFF060906),
			border = androidx.compose.foundation.BorderStroke(
				1.dp,
				Color.White.copy(alpha = 0.08f)
			),
		) {
			BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
				val stacked = maxWidth < 1100.dp
				if (stacked) {
					Column(modifier = Modifier.fillMaxSize()) {
						MotoModeloPreviewPane(
							title = title,
							heroResource = heroResource,
							cardResource = cardResource,
							form = form,
							onDismiss = onDismiss,
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
						)
						MotoModeloFormPane(
							form = form,
							onFormChange = {
								form = it
							},
							onSubmit = onSubmit,
							onDismiss = onDismiss,
							submitLabel = submitLabel,
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
						)
					}
				} else {
					Row(modifier = Modifier.fillMaxSize()) {
						MotoModeloPreviewPane(
							title = title,
							heroResource = heroResource,
							cardResource = cardResource,
							form = form,
							onDismiss = onDismiss,
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
							modifier = Modifier.weight(0.44f),
						)
						MotoModeloFormPane(
							form = form,
							onFormChange = {
								form = it
							},
							onSubmit = onSubmit,
							onDismiss = onDismiss,
							submitLabel = submitLabel,
							apiBaseUrl = apiBaseUrl,
							apiAccessToken = apiAccessToken,
							modifier = Modifier.weight(0.56f),
						)
					}
				}
			}
		}
	}
}