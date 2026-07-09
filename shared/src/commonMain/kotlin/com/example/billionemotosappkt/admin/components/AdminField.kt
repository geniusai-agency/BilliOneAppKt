package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AdminField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    compactLayout: Boolean = false,
    secure: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor = when {
        focused -> Color(0xFF20E65B).copy(alpha = 0.65f)
        hovered -> Color.White.copy(alpha = 0.24f)
        else -> Color.White.copy(alpha = 0.12f)
    }
    val bgColor = if (focused) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.03f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = if (compactLayout) 14.dp else 16.dp, vertical = if (compactLayout) 10.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (compactLayout) 6.dp else 8.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.45f),
            fontSize = if (compactLayout) 9.sp else 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = if (compactLayout) 1.5.sp else 1.8.sp,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compactLayout) 10.dp else 12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.White.copy(alpha = if (focused) 0.95f else 0.42f),
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = if (enabled) 1f else 0.65f }
                    .onFocusChanged { focused = it.isFocused },
                enabled = enabled,
                singleLine = true,
                shape = RoundedCornerShape(0.dp),
                placeholder = { Text(placeholder) },
                trailingIcon = trailingIcon,
                visualTransformation = if (secure) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White.copy(alpha = 0.65f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.30f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.30f),
                    cursorColor = Color(0xFF20E65B),
                ),
            )
        }
    }
}
