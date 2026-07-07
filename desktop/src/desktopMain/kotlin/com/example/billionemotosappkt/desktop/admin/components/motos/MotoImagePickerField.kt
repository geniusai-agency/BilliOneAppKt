package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.`fun`.desktopImagePainter
import com.example.billionemotosappkt.desktop.admin.`fun`.isLocalImagePath
import com.example.billionemotosappkt.desktop.admin.`fun`.pickDesktopImageFile
import com.example.billionemotosappkt.desktop.admin.`fun`.resolveMotoImagePreview
import com.example.billionemotosappkt.desktop.admin.`fun`.toUploadFileRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import java.io.File

@Composable
fun MotoImagePickerField(
	imageValue: String,
	onImageSelected: (String, UploadFileRequest?) -> Unit,
	apiBaseUrl: String,
	apiAccessToken: String?,
	title: String = "Imagem do modelo",
	description: String = "Selecione um arquivo real do computador.",
	buttonLabel: String = "Selecionar imagem",
) {
	val previewResource = remember(imageValue) {
		resolveMotoImagePreview(imageValue, apiBaseUrl)
	}
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
		border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = title,
						color = Color.White,
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold,
					)
					Text(
						text = description,
						color = Color.White.copy(alpha = 0.55f),
						fontSize = 11.sp,
					)
				}
			}
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier
						.size(116.dp)
						.clip(RoundedCornerShape(16.dp))
						.background(
							Brush.linearGradient(
								listOf(
									Color(0xFF111915),
									Color(0xFF070A08),
								),
							),
						)
						.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
					contentAlignment = Alignment.Center,
				) {
					Image(
						painter = desktopImagePainter(previewResource, apiBaseUrl, apiAccessToken),
						contentDescription = null,
						modifier = Modifier.fillMaxSize().padding(8.dp),
					)
					if (imageValue.isNotBlank()) {
						IconButton(
							onClick = { onImageSelected("", null) },
							modifier = Modifier
								.align(Alignment.TopEnd)
								.padding(5.dp)
								.size(24.dp)
								.clip(RoundedCornerShape(999.dp))
								.background(Color.Black.copy(alpha = 0.55f)),
						) {
							Icon(
								imageVector = Icons.Default.Close,
								contentDescription = "Remover imagem",
								tint = Color.White,
								modifier = Modifier.size(14.dp),
							)
						}
					}
				}
				
				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = when {
							imageValue.isBlank() -> "Nenhuma imagem selecionada"
							isLocalImagePath(imageValue) -> File(imageValue).name
							else -> imageValue
						},
						color = Color.White,
						fontWeight = FontWeight.SemiBold,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
					Text(
						text = "Prefira PNG, JPG ou WEBP. O arquivo local já aparece no preview.",
						color = Color.White.copy(alpha = 0.55f),
						fontSize = 11.sp,
					)
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Button(
							onClick = {
								pickDesktopImageFile()?.let { file ->
									onImageSelected(file.absolutePath, file.absolutePath.toUploadFileRequest())
								}
							},
							shape = RoundedCornerShape(14.dp),
							colors = ButtonDefaults.buttonColors(
								containerColor = Color(0xFF20E65B),
								contentColor = Color.Black,
							),
						) {
							Icon(Icons.Default.Image, contentDescription = null)
							Spacer(modifier = Modifier.width(8.dp))
							Text(if (imageValue.isBlank()) buttonLabel else "Trocar imagem")
						}
					}
				}
			}
		}
	}
}
