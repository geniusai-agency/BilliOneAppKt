package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform PDF rendering. Implemented with Apache PDFBox on the JVM/desktop target;
 * Android and iOS provide no-op stubs (PDF preview is a desktop-only feature for now).
 */
expect fun renderPdfSummary(bytes: ByteArray): DocumentPreviewSummary?

expect fun pdfPageCount(bytes: ByteArray): Int

expect fun renderPdfPageBitmap(bytes: ByteArray, pageIndex: Int): ImageBitmap?
