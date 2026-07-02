package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val hovered = remember { mutableStateOf(false) }
    val bgColor by animateFloatAsState(
        targetValue = when {
            selected -> 1f
            hovered.value -> 0.15f
            else -> 0f
        },
        label = "toggle_bg_alpha",
    )

    Box(
        modifier = modifier
            .height(if (compactLayout) 36.dp else 40.dp)
            .pointerMoveFilter(
                onEnter = {
                    hovered.value = true
                    false
                },
                onExit = {
                    hovered.value = false
                    false
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected) Color.White else Color.Transparent,
                contentColor = if (selected) Color.Black else Color.White.copy(alpha = 0.68f),
            ),
            border = if (selected) null else BorderStroke(0.dp, Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = if (compactLayout) 10.dp else 12.dp,
                vertical = if (compactLayout) 5.dp else 6.dp,
            ),
        ) {
            Text(
                text = text,
                fontSize = if (compactLayout) 11.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (!selected && bgColor > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = bgColor * 0.18f), RoundedCornerShape(10.dp)),
            )
        }
    }
}