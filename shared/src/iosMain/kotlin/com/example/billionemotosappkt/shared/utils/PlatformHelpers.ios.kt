package com.example.billionemotosappkt.shared.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.billionemotosappkt.shared.api.UploadFileRequest
import org.jetbrains.skia.Image
import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

actual fun openUri(uri: String) {
    val nsUrl = NSURL.URLWithString(uri)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}

@androidx.compose.runtime.Composable
actual fun rememberImagePicker(onImagePicked: (UploadFileRequest?) -> Unit): () -> Unit {
    return {
        // No-op for now to ensure compilation
    }
}

actual fun getDaysUntil(dateStr: String?): Int? {
    if (dateStr.isNullOrBlank()) return null
    val normalized = dateStr.take(10)
    val formatter = platform.Foundation.NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd"
    }
    val targetDate = formatter.dateFromString(normalized) ?: return null
    val calendar = platform.Foundation.NSCalendar.currentCalendar
    val today = calendar.startOfDayForDate(platform.Foundation.NSDate())
    val target = calendar.startOfDayForDate(targetDate)
    val components = calendar.components(platform.Foundation.NSCalendarUnitDay, today, target, 0uL)
    return components.day.toInt()
}

actual fun getHourOfDay(): Int {
    val calendar = platform.Foundation.NSCalendar.currentCalendar
    val components = calendar.components(platform.Foundation.NSCalendarUnitHour, platform.Foundation.NSDate())
    return components.hour.toInt()
}

actual fun currentEpochMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000.0).toLong()

actual fun currentYearMonth(): String {
    val formatter = platform.Foundation.NSDateFormatter().apply { dateFormat = "yyyy-MM" }
    return formatter.stringFromDate(platform.Foundation.NSDate())
}

@androidx.compose.runtime.Composable
actual fun rememberAssetPainter(name: String): androidx.compose.ui.graphics.painter.Painter {
    return androidx.compose.ui.graphics.painter.ColorPainter(androidx.compose.ui.graphics.Color.Transparent)
}

@androidx.compose.runtime.Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have physical back button, so this is a no-op
}
