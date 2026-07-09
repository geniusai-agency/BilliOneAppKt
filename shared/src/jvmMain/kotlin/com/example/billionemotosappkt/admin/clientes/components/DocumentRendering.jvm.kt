package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.skia.Image

actual fun renderPdfSummary(bytes: ByteArray): DocumentPreviewSummary? {
    return runCatching {
        Loader.loadPDF(bytes).use { document ->
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

actual fun pdfPageCount(bytes: ByteArray): Int {
    return runCatching {
        Loader.loadPDF(bytes).use { document -> document.numberOfPages.coerceAtLeast(1) }
    }.getOrDefault(1)
}

actual fun renderPdfPageBitmap(bytes: ByteArray, pageIndex: Int): ImageBitmap? {
    return runCatching {
        Loader.loadPDF(bytes).use { document ->
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
