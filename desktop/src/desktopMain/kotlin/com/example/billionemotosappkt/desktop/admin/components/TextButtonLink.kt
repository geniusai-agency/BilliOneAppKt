package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextButtonLink(
    text: String,
    accent: Color,
    compactLayout: Boolean = false,
    onClick: () -> Unit,
) {
    val hovered = remember { mutableStateOf(false) }
    TextButton(
        onClick = onClick,
        modifier = Modifier.pointerMoveFilter(
            onEnter = {
                hovered.value = true
                false
            },
            onExit = {
                hovered.value = false
                false
            },
        ),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (hovered.value) accent else accent.copy(alpha = 0.84f),
        ),
    ) {
        Text(text = text, fontSize = if (compactLayout) 11.sp else 12.sp)
    }
}