package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.skia.Image

data class DocumentPreviewSummary(
    val pageCount: Int,
    val thumbnail: ImageBitmap?,
)

suspend fun BillioneMotosApi.loadDocumentPreviewSummary(url: String?): DocumentPreviewSummary? {
    val resolved = url?.trim().orEmpty()
    if (resolved.isBlank()) return null

    return withContext(Dispatchers.IO) {
        runCatching {
            fetchRawBytes(resolved).toPreviewSummary()
        }.getOrNull()
    }
}

@Composable
fun DocumentPreviewViewerDialog(
    api: BillioneMotosApi,
    title: String,
    subtitle: String,
    url: String?,
    summary: DocumentPreviewSummary?,
    loading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onOpenExternally: () -> Unit,
) {
    var documentBytes by remember(url) { mutableStateOf<ByteArray?>(null) }
    var pageCount by remember(url) { mutableIntStateOf(summary?.pageCount ?: 1) }
    var currentPage by remember(url) { mutableIntStateOf(0) }
    var pageBitmap by remember(url) { mutableStateOf(summary?.thumbnail) }
    var pageLoading by remember(url) { mutableStateOf(false) }
    var pageError by remember(url) { mutableStateOf<String?>(error) }

    LaunchedEffect(url) {
        val resolved = url?.trim().orEmpty()
        if (resolved.isBlank()) {
            documentBytes = null
            pageCount = 1
            currentPage = 0
            pageBitmap = null
            pageError = error
            return@LaunchedEffect
        }

        pageLoading = true
        pageError = null
        currentPage = 0

        runCatching {
            documentBytes = api.fetchRawBytes(resolved)
            pageCount = documentBytes?.documentPageCount() ?: 1
            pageBitmap = null
        }.onFailure { throwable ->
            documentBytes = null
            pageBitmap = null
            pageError = throwable.message ?: error ?: "Nao foi possivel carregar o arquivo."
        }

        pageLoading = false
    }

    LaunchedEffect(documentBytes, currentPage) {
        val bytes = documentBytes ?: return@LaunchedEffect
        pageLoading = true
        pageError = null
        runCatching {
            pageBitmap = bytes.renderPreviewPage(currentPage)
        }.onFailure { throwable ->
            pageBitmap = null
            pageError = throwable.message ?: "Nao foi possivel renderizar a pagina."
        }
        pageLoading = false
    }

    val hasPrevious = currentPage > 0
    val hasNext = currentPage < pageCount - 1

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.84f))
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.92f).fillMaxSize(0.92f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0E0C)),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 18.dp, end = 12.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                subtitle.ifBlank { "Documento" },
                                color = Color.White.copy(alpha = 0.46f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = onOpenExternally) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color(0xFF20E65B))
                                Text("Abrir", color = Color(0xFF20E65B))
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                    when {
                        loading || pageLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF20E65B))
                        }
                        pageBitmap != null -> Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                text = "Página ${currentPage + 1} de ${pageCount.coerceAtLeast(1)}",
                                color = Color.White.copy(alpha = 0.42f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                ComposeImage(
                                    bitmap = pageBitmap!!,
                                    contentDescription = "$title - página ${currentPage + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                TextButton(
                                    onClick = { if (hasPrevious) currentPage -= 1 },
                                    enabled = hasPrevious,
                                ) {
                                    Text(
                                        "Anterior",
                                        color = if (hasPrevious) Color(0xFF20E65B) else Color.White.copy(alpha = 0.24f),
                                    )
                                }
                                Text(
                                    text = if (pageCount > 1) "Use os botões para navegar entre as páginas." else "Documento de página única.",
                                    color = Color.White.copy(alpha = 0.38f),
                                    fontSize = 11.sp,
                                )
                                TextButton(
                                    onClick = { if (hasNext) currentPage += 1 },
                                    enabled = hasNext,
                                ) {
                                    Text(
                                        "Próxima",
                                        color = if (hasNext) Color(0xFF20E65B) else Color.White.copy(alpha = 0.24f),
                                    )
                                }
                            }
                        }
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Description, null, tint = Color.White.copy(alpha = 0.24f))
                                Text(
                                    text = pageError ?: error ?: "Sem preview disponivel.",
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ByteArray.toPreviewSummary(): DocumentPreviewSummary? {
    if (looksLikePdf()) return renderPdfSummary()
    return runCatching {
        DocumentPreviewSummary(
            pageCount = 1,
            thumbnail = Image.makeFromEncoded(this).toComposeImageBitmap(),
        )
    }.getOrNull()
}

private fun ByteArray.looksLikePdf(): Boolean {
    return size >= 4 &&
        this[0] == '%'.code.toByte() &&
        this[1] == 'P'.code.toByte() &&
        this[2] == 'D'.code.toByte() &&
        this[3] == 'F'.code.toByte()
}

private fun ByteArray.renderPdfSummary(): DocumentPreviewSummary? {
    return runCatching {
        Loader.loadPDF(this).use { document ->
            val renderer = PDFRenderer(document)
            val bufferedImage = renderer.renderImageWithDPI(0, 140f)
            val thumbnail = ByteArrayOutputStream().use { output ->
                ImageIO.write(bufferedImage, "png", output)
                Image.makeFromEncoded(output.toByteArray()).toComposeImageBitmap()
            }
            DocumentPreviewSummary(
                pageCount = document.numberOfPages.coerceAtLeast(1),
                thumbnail = thumbnail,
            )
        }
    }.getOrNull()
}

private fun ByteArray.documentPageCount(): Int {
    if (!looksLikePdf()) return 1
    return runCatching {
        Loader.loadPDF(this).use { document ->
            document.numberOfPages.coerceAtLeast(1)
        }
    }.getOrDefault(1)
}

private fun ByteArray.renderPreviewPage(pageIndex: Int): ImageBitmap? {
    if (looksLikePdf()) return renderPdfPage(pageIndex)
    return if (pageIndex == 0) {
        runCatching { Image.makeFromEncoded(this).toComposeImageBitmap() }.getOrNull()
    } else {
        null
    }
}

private fun ByteArray.renderPdfPage(pageIndex: Int): ImageBitmap? {
    return runCatching {
        Loader.loadPDF(this).use { document ->
            val safePageIndex = pageIndex.coerceIn(0, document.numberOfPages - 1)
            val renderer = PDFRenderer(document)
            val bufferedImage = renderer.renderImageWithDPI(safePageIndex, 140f)
            ByteArrayOutputStream().use { output ->
                ImageIO.write(bufferedImage, "png", output)
                Image.makeFromEncoded(output.toByteArray()).toComposeImageBitmap()
            }
        }
    }.getOrNull()
}

fun openDocumentExternally(api: BillioneMotosApi, url: String?) {
    val resolvedUrl = url?.trim().orEmpty()
    if (resolvedUrl.isBlank() || !java.awt.Desktop.isDesktopSupported()) return

    val fullUrl = if (resolvedUrl.startsWith("http://", ignoreCase = true) || resolvedUrl.startsWith("https://", ignoreCase = true)) {
        resolvedUrl
    } else {
        api.config.baseUrl.trimEnd('/') + "/" + resolvedUrl.removePrefix("/")
    }

    runCatching {
        java.awt.Desktop.getDesktop().browse(java.net.URI(fullUrl))
    }
}
