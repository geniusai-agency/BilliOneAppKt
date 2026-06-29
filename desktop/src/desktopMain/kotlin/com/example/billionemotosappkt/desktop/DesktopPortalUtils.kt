package com.example.billionemotosappkt.desktop

fun generateCpfForEmail(email: String): String {
    val digits = buildString {
        email.lowercase().forEach { char ->
            append((char.code % 10).toString())
        }
    }
    val padded = (digits + "123456789").take(9)
    val first = cpfDigit(padded)
    val second = cpfDigit(padded + first)
    return padded + first.toString() + second.toString()
}

private fun cpfDigit(base: String): Int {
    var sum = 0
    var weight = base.length + 1
    for (char in base) {
        sum += char.digitToInt() * weight
        weight -= 1
    }
    val remainder = (sum * 10) % 11
    return if (remainder == 10) 0 else remainder
}

fun String.isValidEmail(): Boolean {
    return contains("@") && contains(".") && trim().length in 6..255
}
