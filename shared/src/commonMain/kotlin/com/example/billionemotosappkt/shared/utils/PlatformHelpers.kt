package com.example.billionemotosappkt.shared.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.example.billionemotosappkt.shared.api.UploadFileRequest

expect fun ByteArray.toImageBitmap(): ImageBitmap

expect fun openUri(uri: String)

@Composable
expect fun rememberImagePicker(onImagePicked: (UploadFileRequest?) -> Unit): () -> Unit

expect fun getDaysUntil(dateStr: String?): Int?

expect fun getHourOfDay(): Int

/** Current wall-clock time in epoch milliseconds. */
expect fun currentEpochMillis(): Long

/** Current year-month as `yyyy-MM` (used for month-over-month grouping). */
expect fun currentYearMonth(): String

@Composable
expect fun rememberAssetPainter(name: String): androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
