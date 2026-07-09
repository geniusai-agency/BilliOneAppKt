package com.example.billionemotosappkt.desktop.admin.screens.motosparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.motos.MotoFiltersRow
import com.example.billionemotosappkt.desktop.admin.components.motos.MotosSearchField
import com.example.billionemotosappkt.shared.api.MotoStatus

/** Barra de busca + filtros de status da Frota, com contagem de resultados. */
@Composable
internal fun FrotaControls(
    query: String,
    onQueryChange: (String) -> Unit,
    status: MotoStatus?,
    onStatusChange: (MotoStatus?) -> Unit,
    resultCount: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MotosSearchField(value = query, onValueChange = onQueryChange)
            Text(
                text = "$resultCount ${if (resultCount == 1) "resultado" else "resultados"}",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 13.sp,
            )
        }
        MotoFiltersRow(selectedStatus = status, onStatusChange = onStatusChange)
    }
}

/** Barra de paginação reutilizável da Frota. */
@Composable
internal fun MotosPager(
    currentPage: Int,
    totalPages: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Página $currentPage de $totalPages · $totalItems motos",
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 12.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onPrev,
                enabled = currentPage > 1,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Anterior")
            }
            Button(
                onClick = onNext,
                enabled = currentPage < totalPages,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
            ) {
                Text("Próxima", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
