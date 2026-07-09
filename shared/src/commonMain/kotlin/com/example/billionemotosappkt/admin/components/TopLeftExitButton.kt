package com.example.billionemotosappkt.desktop.admin.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopLeftExitButton(onBackToSite: () -> Unit, compactLayout: Boolean = false) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    OutlinedButton(
        onClick = onBackToSite,
        modifier = Modifier
            .padding(if (compactLayout) 14.dp else 20.dp)
            .background(Color.Transparent)
            .hoverable(interactionSource),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = if (hovered) 0.26f else 0.12f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White.copy(alpha = if (hovered) 1f else 0.74f),
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = if (compactLayout) 12.dp else 14.dp, vertical = if (compactLayout) 7.dp else 8.dp),
    ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(if (compactLayout) 13.dp else 14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Voltar ao site", fontSize = 11.sp)
    }
}