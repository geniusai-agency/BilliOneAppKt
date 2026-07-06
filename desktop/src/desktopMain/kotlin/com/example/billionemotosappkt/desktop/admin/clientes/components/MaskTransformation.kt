package com.example.billionemotosappkt.desktop.admin.clientes.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskTransformation(private val mask: String) : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText {
		val trimmed = text.text.replace(Regex("[^0-9a-zA-Z]"), "")
		var out = ""
		var maskIndex = 0
		var textIndex = 0
		
		while (maskIndex < mask.length && textIndex < trimmed.length) {
			if (mask[maskIndex] == '#') {
				out += trimmed[textIndex]
				textIndex++
			} else {
				out += mask[maskIndex]
			}
			maskIndex++
		}
		
		val offsetMapping = object : OffsetMapping {
			override fun originalToTransformed(offset: Int): Int {
				if (offset <= 0) return 0
				var transformedOffset = 0
				var sourceChars = 0
				while (transformedOffset < out.length && sourceChars < offset) {
					if (mask[transformedOffset] == '#') {
						sourceChars++
					}
					transformedOffset++
				}
				return transformedOffset
			}
			
			override fun transformedToOriginal(offset: Int): Int {
				if (offset <= 0) return 0
				var originalOffset = 0
				var maskIndex = 0
				while (maskIndex < offset && maskIndex < out.length) {
					if (mask[maskIndex] == '#') {
						originalOffset++
					}
					maskIndex++
				}
				return originalOffset
			}
		}
		
		return TransformedText(AnnotatedString(out), offsetMapping)
	}
}