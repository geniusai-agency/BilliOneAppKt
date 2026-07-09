package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.ui.graphics.ImageBitmap

// PDF preview is a desktop-only feature; Android currently has no thumbnail renderer.
actual fun renderPdfSummary(bytes: ByteArray): DocumentPreviewSummary? = null

actual fun pdfPageCount(bytes: ByteArray): Int = 1

actual fun renderPdfPageBitmap(bytes: ByteArray, pageIndex: Int): ImageBitmap? = null
