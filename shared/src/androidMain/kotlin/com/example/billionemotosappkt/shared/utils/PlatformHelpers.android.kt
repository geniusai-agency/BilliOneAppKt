@file:kotlin.jvm.JvmName("AndroidPlatformHelpers")

package com.example.billionemotosappkt.shared.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.net.toUri


object AndroidPlatformContext {
    @Volatile
    var appContext: Context? = null
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}

actual fun openUri(uri: String) {
    val context = AndroidPlatformContext.appContext ?: return
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@androidx.compose.runtime.Composable
actual fun rememberImagePicker(onImagePicked: (UploadFileRequest?) -> Unit): () -> Unit {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            onImagePicked(null)
            return@rememberLauncherForActivityResult
        }
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                val name = uri.lastPathSegment ?: "image.jpg"
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                onImagePicked(UploadFileRequest(bytes, name, mimeType))
            }
        }.onFailure {
            onImagePicked(null)
        }
    }
    return { launcher.launch("image/*") }
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val resName = name.substringBeforeLast('.').replace('-', '_')
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) {
        androidx.compose.ui.res.painterResource(resId)
    } else {
        androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Transparent)
    }
}

@androidx.compose.runtime.Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled, onBack)
}
