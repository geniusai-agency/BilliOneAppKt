package com.example.billionemotosappkt.components.auth

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun onlyDigits(value: String): String = value.filter(Char::isDigit)

object CpfVisualTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val digits = onlyDigits(text.text).take(11)
		val formatted = buildString {
			digits.forEachIndexed { index, char ->
				if (index == 3 || index == 6) append('.')
				if (index == 9) append('-')
				append(char)
			}
		}

		return TransformedText(
			AnnotatedString(formatted),
			object : OffsetMapping {
				override fun originalToTransformed(offset: Int): Int {
					val separators = when {
						offset <= 3 -> 0
						offset <= 6 -> 1
						offset <= 9 -> 2
						else -> 3
					}
					return (offset + separators).coerceAtMost(formatted.length)
				}

				override fun transformedToOriginal(offset: Int): Int {
					val separators = listOf(3, 7, 11).count { it < offset }
					return (offset - separators).coerceIn(0, digits.length)
				}
			},
		)
	}
}

object PhoneVisualTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val digits = onlyDigits(text.text).take(11)
		val formatted = when (digits.length) {
			in 0..2 -> digits
			in 3..6 -> buildString {
				append('(')
				append(digits.take(2))
				append(") ")
				append(digits.drop(2))
			}
			in 7..10 -> buildString {
				append('(')
				append(digits.take(2))
				append(") ")
				append(digits.drop(2).take(4))
				append('-')
				append(digits.drop(6))
			}
			else -> buildString {
				append('(')
				append(digits.take(2))
				append(") ")
				append(digits.drop(2).take(5))
				append('-')
				append(digits.drop(7))
			}
		}

		return TransformedText(
			AnnotatedString(formatted),
			object : OffsetMapping {
				override fun originalToTransformed(offset: Int): Int {
					val separators = when {
						offset <= 2 -> 0
						offset <= 6 -> 3
						offset <= 10 -> 4
						else -> 5
					}
					return (offset + separators).coerceAtMost(formatted.length)
				}

				override fun transformedToOriginal(offset: Int): Int {
					val separators = when {
						offset <= 2 -> 0
						offset <= 5 -> 2
						offset <= 10 -> 3
						else -> 4
					}
					return (offset - separators).coerceIn(0, digits.length)
				}
			},
		)
	}
}

fun isValidCpf(value: String): Boolean {
	val digits = onlyDigits(value)
	if (digits.length != 11) return false
	if (digits.all { it == digits.first() }) return false

	fun calculateDigit(length: Int): Int {
		var sum = 0
		var weight = length + 1
		for (index in 0 until length) {
			sum += (digits[index].code - '0'.code) * weight
			weight--
		}
		val remainder = (sum * 10) % 11
		return if (remainder == 10) 0 else remainder
	}

	val firstDigit = calculateDigit(9)
	val secondDigit = calculateDigit(10)
	return digits[9].digitToInt() == firstDigit && digits[10].digitToInt() == secondDigit
}

fun isValidPhone(value: String): Boolean {
	val digits = onlyDigits(value)
	return digits.length in 10..11
}
