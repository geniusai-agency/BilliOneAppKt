package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SegmentedToggleButton(
    text: String,
    selected: Boolean,
    primary: Color,
    compactLayout: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) primary.copy(alpha = 0.15f) else Color(0xFF111614)
    val borderColor = if (selected) primary else Color.White.copy(alpha = 0.10f)
    val textColor = if (selected) primary else Color.White.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .height(if (compactLayout) 36.dp else 40.dp)
            .pointerMoveFilter(
                onEnter = { false },
                onExit = { false },
            ),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = if (compactLayout) 14.dp else 16.dp)) {
            Text(
                text = text,
                color = textColor,
                fontSize = if (compactLayout) 11.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
