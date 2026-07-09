package com.example.billionemotosappkt.desktop.admin.components.motos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.shared.api.MotoResponse
import com.example.billionemotosappkt.shared.api.MotoStatus

@Composable
fun MotosList(
    motos: List<MotoResponse>,
    onView: (MotoResponse) -> Unit = {},
    onEdit: (MotoResponse) -> Unit = {},
    onDelete: (MotoResponse) -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(0.05f))
    ) {
        Column {
            // Header da Tabela
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val headerColor = Color.White.copy(0.4f)
                Text("Placa", Modifier.weight(1f), color = headerColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Modelo", Modifier.weight(2.5f), color = headerColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Ano", Modifier.weight(0.8f), color = headerColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("KM", Modifier.weight(1f), color = headerColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Status", Modifier.weight(1.2f), color = headerColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(120.dp)) // Espaço para ações
            }

            HorizontalDivider(color = Color.White.copy(0.05f))

            if (motos.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma moto encontrada.", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                motos.forEach { moto ->
                    MotoRow(moto = moto, onView = onView, onEdit = onEdit, onDelete = onDelete)
                    HorizontalDivider(color = Color.White.copy(0.02f), modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Composable
private fun MotoRow(
    moto: MotoResponse,
    onView: (MotoResponse) -> Unit,
    onEdit: (MotoResponse) -> Unit,
    onDelete: (MotoResponse) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView(moto) }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = moto.placa,
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        
        Text(
            text = "${moto.marca ?: ""} ${moto.modelo ?: ""}".trim(),
            modifier = Modifier.weight(2.5f),
            color = Color.White.copy(0.8f),
            fontSize = 14.sp
        )
        
        Text(
            text = moto.ano?.toString() ?: "-",
            modifier = Modifier.weight(0.8f),
            color = Color.White.copy(0.6f),
            fontSize = 14.sp
        )
        
        Text(
            text = moto.kmAtual?.toString() ?: "0",
            modifier = Modifier.weight(1f),
            color = Color.White.copy(0.6f),
            fontSize = 14.sp
        )

        Box(modifier = Modifier.weight(1.2f)) {
            StatusBadge(moto.status)
        }

        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onView(moto) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Visibility, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { onEdit(moto) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Edit, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { onDelete(moto) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Delete, null, tint = Color(0xFFFF4A4A).copy(0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun StatusBadge(status: MotoStatus) {
    val (color, label) = when (status) {
        MotoStatus.DISPONIVEL -> Color(0xFF20E65B) to "Disponível"
        MotoStatus.ALUGADA -> Color(0xFF7DD3FC) to "Alugada"
        MotoStatus.MANUTENCAO -> Color(0xFFFFB300) to "Manutenção"
        MotoStatus.BLOQUEADA -> Color(0xFFFF4A4A) to "Bloqueada"
        MotoStatus.CONTRATADA -> Color(0xFF8B5CF6) to "Contratada"
        else -> Color.White.copy(0.4f) to status.name
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}
