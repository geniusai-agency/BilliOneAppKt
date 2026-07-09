package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.desktop.DesktopLoginMode

@Composable
fun ModeToggle(
    mode: DesktopLoginMode,
    primary: Color,
    compactLayout: Boolean,
    onModeChange: (DesktopLoginMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .padding(if (compactLayout) 3.dp else 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentedToggleButton(
            text = "Entrar",
            selected = mode == DesktopLoginMode.LOGIN,
            primary = primary,
            compactLayout = compactLayout,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DesktopLoginMode.LOGIN) },
        )
        SegmentedToggleButton(
            text = "Cadastrar",
            selected = mode == DesktopLoginMode.SIGNUP,
            primary = primary,
            compactLayout = compactLayout,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(DesktopLoginMode.SIGNUP) },
        )
    }
}
