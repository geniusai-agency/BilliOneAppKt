package com.example.billionemotosappkt.shared.utils

/**
 * Multiplatform date/number formatting helpers (pure Kotlin, no java.* / no extra dependency).
 *
 * These replace the Android-incompatible `java.time` / `java.text` usages that were pulled into
 * commonMain during the desktop→multiplatform migration. Date inputs are ISO-8601 strings
 * (`yyyy-MM-dd` or `yyyy-MM-ddTHH:mm:ss...`); the wall-clock components are read directly from the
 * string (no timezone conversion), which matches how these admin/customer screens display values.
 */

private val MESES_ABREV = listOf(
    "jan", "fev", "mar", "abr", "mai", "jun",
    "jul", "ago", "set", "out", "nov", "dez",
)

private class IsoDateParts(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
)

private fun parseIso(value: String?): IsoDateParts? {
    if (value.isNullOrBlank()) return null
    val trimmed = value.trim()
    val datePart = trimmed.take(10)
    val pieces = datePart.split('-')
    if (pieces.size != 3) return null
    val year = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val day = pieces[2].toIntOrNull() ?: return null

    var hour = 0
    var minute = 0
    val tIndex = trimmed.indexOf('T')
    if (tIndex >= 0 && trimmed.length >= tIndex + 6) {
        val timePart = trimmed.substring(tIndex + 1)
        val timePieces = timePart.split(':')
        if (timePieces.size >= 2) {
            hour = timePieces[0].toIntOrNull() ?: 0
            minute = timePieces[1].take(2).toIntOrNull() ?: 0
        }
    }
    return IsoDateParts(year, month, day, hour, minute)
}

private fun Int.pad2(): String = if (this < 10) "0$this" else this.toString()

/** ISO date/datetime string → `dd/MM/yyyy`. Returns null for blank/invalid input. */
fun formatBrDate(value: String?): String? {
    val p = parseIso(value) ?: return null
    return "${p.day.pad2()}/${p.month.pad2()}/${p.year}"
}

/** ISO datetime string → `dd/MM/yyyy HH:mm`. Returns null for blank/invalid input. */
fun formatBrDateTime(value: String?): String? {
    val p = parseIso(value) ?: return null
    return "${p.day.pad2()}/${p.month.pad2()}/${p.year} ${p.hour.pad2()}:${p.minute.pad2()}"
}

/** ISO date string → `dd mmm` (pt-BR abbreviated month, e.g. `15 jan`). */
fun formatDayMonth(value: String?): String? {
    val p = parseIso(value) ?: return null
    val mes = MESES_ABREV.getOrNull(p.month - 1) ?: return null
    return "${p.day.pad2()} $mes"
}

/** `yyyy-MM` bucket of an ISO date string (used for month-over-month grouping). */
fun yearMonthOf(value: String?): String? {
    val p = parseIso(value) ?: return null
    return "${p.year}-${p.month.pad2()}"
}

/** Given a `yyyy-MM` string, returns the previous month as `yyyy-MM` (with year rollover). */
fun previousYearMonth(yearMonth: String): String {
    val pieces = yearMonth.split('-')
    val year = pieces.getOrNull(0)?.toIntOrNull() ?: return yearMonth
    val month = pieces.getOrNull(1)?.toIntOrNull() ?: return yearMonth
    return if (month <= 1) "${year - 1}-12" else "$year-${(month - 1).pad2()}"
}

/** Brazilian Real currency formatting: `1234.5` → `R$ 1.234,50`. */
fun formatBRL(value: Double): String {
    val negative = value < 0
    val cents = kotlin.math.round(kotlin.math.abs(value) * 100.0).toLong()
    val integral = cents / 100
    val frac = cents % 100
    val integralStr = integral.toString().reversed().chunked(3).joinToString(".").reversed()
    val fracStr = if (frac < 10) "0$frac" else "$frac"
    return (if (negative) "-R$ " else "R$ ") + "$integralStr,$fracStr"
}

fun formatBRL(value: Long): String = formatBRL(value.toDouble())

fun formatBRL(value: Int): String = formatBRL(value.toDouble())

// --- Formatting driven by the platform clock ---

private fun civilFromEpochDay(epochDay: Long): Triple<Int, Int, Int> {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
    val m = (if (mp < 10) mp + 3 else mp - 9).toInt()
    val year = (y + (if (m <= 2) 1L else 0L)).toInt()
    return Triple(year, m, d)
}

private fun epochMillisToParts(millis: Long): IsoDateParts {
    val totalSeconds = millis / 1000
    val secondsOfDay = ((totalSeconds % 86400) + 86400) % 86400
    val epochDay = (totalSeconds - secondsOfDay) / 86400
    val (year, month, day) = civilFromEpochDay(epochDay)
    val hour = (secondsOfDay / 3600).toInt()
    val minute = ((secondsOfDay % 3600) / 60).toInt()
    return IsoDateParts(year, month, day, hour, minute)
}

/** Format an epoch-millis timestamp as `dd/MM/yyyy HH:mm` (UTC wall clock). */
fun formatEpochMillisDateTime(millis: Long): String {
    val p = epochMillisToParts(millis)
    return "${p.day.pad2()}/${p.month.pad2()}/${p.year} ${p.hour.pad2()}:${p.minute.pad2()}"
}
