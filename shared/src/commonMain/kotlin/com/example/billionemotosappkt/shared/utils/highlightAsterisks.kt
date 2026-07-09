package com.example.billionemotosappkt.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

fun highlightAsterisks(
	text: String,
	baseColor: Color,
	highlightColor: Color,
): AnnotatedString {
	val start = text.indexOf('*')
	val end = if (start >= 0) text.indexOf('*', startIndex = start + 1) else -1
	
	if (start < 0 || end <= start) return AnnotatedString(text.replace("*", ""))
	
	return buildAnnotatedString {
		withStyle(
			SpanStyle(color = baseColor),
		) {
			append(text.substring(0, start))
		}
		withStyle(
			SpanStyle(
				color = highlightColor,
				fontWeight = FontWeight.Bold,
				textDecoration = TextDecoration.None,
			),
		) {
			append(text.substring(start + 1, end))
		}
		withStyle(
			SpanStyle(color = baseColor),
		) {
			append(text.substring(end + 1).replace("*", ""))
		}
	}
}
