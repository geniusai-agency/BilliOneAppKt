@file:kotlin.jvm.JvmName("DesktopPlatformHelpers")

package com.example.billionemotosappkt.shared.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import org.jetbrains.skia.Image
import java.awt.Desktop
import java.net.URI

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

actual fun openUri(uri: String) {
    if (Desktop.isDesktopSupported()) {
        runCatching {
            Desktop.getDesktop().browse(URI(uri))
        }
    }
}

@androidx.compose.runtime.Composable
actual fun rememberImagePicker(onImagePicked: (UploadFileRequest?) -> Unit): () -> Unit {
    return {
        val file = runCatching {
            val chooser = javax.swing.JFileChooser().apply {
                fileSelectionMode = javax.swing.JFileChooser.FILES_ONLY
                isAcceptAllFileFilterUsed = true
                fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Imagens", "png", "jpg", "jpeg", "webp")
            }
            if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile
            } else {
                null
            }
        }.getOrNull()
        if (file != null) {
            val contentType = when (file.extension.lowercase()) {
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                else -> "application/octet-stream"
            }
            onImagePicked(com.example.billionemotosappkt.shared.api.UploadFileRequest(file.readBytes(), file.name, contentType))
        } else {
            onImagePicked(null)
        }
    }
}

actual fun getDaysUntil(dateStr: String?): Int? {
    if (dateStr.isNullOrBlank()) return null
    val normalized = dateStr.take(10)
    val parser = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply { isLenient = false }
    val parsed = runCatching { parser.parse(normalized) }.getOrNull() ?: return null
    val today = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val target = java.util.Calendar.getInstance().apply {
        time = parsed
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val diff = target.timeInMillis - today.timeInMillis
    return (diff / (24 * 60 * 60 * 1000L)).toInt()
}

actual fun getHourOfDay(): Int {
    return java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
}

actual fun currentEpochMillis(): Long = System.currentTimeMillis()

actual fun currentYearMonth(): String = java.time.YearMonth.now().toString()

@androidx.compose.runtime.Composable
actual fun rememberAssetPainter(name: String): androidx.compose.ui.graphics.painter.Painter {
    return androidx.compose.runtime.remember(name) {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(name)
        if (stream != null) {
            stream.use {
                androidx.compose.ui.graphics.painter.BitmapPainter(androidx.compose.ui.res.loadImageBitmap(it))
            }
        } else {
            androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Transparent)
        }
    }
}

@androidx.compose.runtime.Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Desktop doesn't have physical back button, so this is a no-op
}
