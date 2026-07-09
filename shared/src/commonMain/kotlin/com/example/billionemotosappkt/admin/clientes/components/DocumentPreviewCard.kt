package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.shared.api.BillioneMotosApi

data class DocumentPreviewData(
    val title: String,
    val subtitle: String,
    val url: String?,
)

@Composable
fun DocumentPreviewCard(
    data: DocumentPreviewData,
    api: BillioneMotosApi,
    modifier: Modifier = Modifier,
) {
    var preview by remember(data.url) { mutableStateOf<DocumentPreviewSummary?>(null) }
    var loading by remember(data.url) { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(data.url) {
        preview = null
        loading = false
        val resolved = data.url?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        loading = true
        try {
            preview = api.loadDocumentPreviewSummary(resolved)
        } finally {
            loading = false
        }
    }

    Card(
        modifier = modifier
            .clickable(enabled = preview?.thumbnail != null) { expanded = true },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(154.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    loading -> CircularProgressIndicator(color = Color(0xFF20E65B), modifier = Modifier.size(28.dp))
                    preview?.thumbnail != null -> androidx.compose.foundation.Image(
                        bitmap = preview!!.thumbnail!!,
                        contentDescription = data.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Description, null, tint = Color.White.copy(alpha = 0.24f), modifier = Modifier.size(30.dp))
                        Text("Sem arquivo", color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(data.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    data.subtitle,
                    color = Color.White.copy(alpha = 0.46f),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Surface(
                color = Color.Transparent,
            ) {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgePill(
                        text = if (preview?.thumbnail != null) "Disponivel" else "Pendente",
                        accent = if (preview?.thumbnail != null) Color(0xFF20E65B) else Color(0xFFFFB300),
                    )
                    Text(
                        text = if (preview?.thumbnail != null) "Clique para ampliar" else "Sem visualizacao",
                        color = Color.White.copy(alpha = 0.36f),
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
    if (expanded) {
        DocumentPreviewViewerDialog(
            api = api,
            title = data.title,
            subtitle = data.subtitle,
            url = data.url,
            summary = preview,
            loading = loading,
            error = if (preview == null && !loading) "Nao foi possivel carregar o arquivo." else null,
            onDismiss = { expanded = false },
            onOpenExternally = { openDocumentExternally(api, data.url) },
        )
    }
}
