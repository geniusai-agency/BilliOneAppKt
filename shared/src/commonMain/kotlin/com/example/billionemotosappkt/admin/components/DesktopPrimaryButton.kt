package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
internal fun DesktopPrimaryButton(
    text: String,
    onClick: () -> Unit,
    primary: Color,
    loading: Boolean,
    compactLayout: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (hovered && !compactLayout) 1.01f else 1f,
        label = "primary_button_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactLayout) 52.dp else 58.dp)
            .hoverable(interactionSource)
            .shadow(
                elevation = if (hovered && !compactLayout) 30.dp else 22.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = primary.copy(alpha = 0.28f),
                spotColor = primary.copy(alpha = 0.42f),
            )
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = Color.Black,
                disabledContainerColor = primary.copy(alpha = 0.62f),
                disabledContentColor = Color.Black.copy(alpha = 0.8f),
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compactLayout) 15.sp else 16.sp,
                )
            }
        }
    }
}