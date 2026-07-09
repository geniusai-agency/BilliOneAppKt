package com.example.billionemotosappkt.desktop.admin.tickets.screens.parts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billionemotosappkt.desktop.admin.components.BadgePill
import com.example.billionemotosappkt.desktop.admin.tickets.model.accent
import com.example.billionemotosappkt.desktop.admin.tickets.model.clienteLabel
import com.example.billionemotosappkt.desktop.admin.tickets.model.formatTicketDate
import com.example.billionemotosappkt.desktop.admin.tickets.model.label
import com.example.billionemotosappkt.shared.api.TicketCategoria
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus

internal data class TicketsSummary(
    val total: Int,
    val abertos: Int,
    val novosCliente: Int,
    val resolvidos: Int,
) {
    companion object {
        fun from(items: List<TicketResponse>, total: Int): TicketsSummary = TicketsSummary(
            total = total,
            abertos = items.count { it.status == TicketStatus.ABERTO },
            novosCliente = items.count {
                it.status == TicketStatus.ABERTO && it.origem == TicketOrigem.CLIENTE
            },
            resolvidos = items.count { it.status == TicketStatus.RESOLVIDO },
        )
    }
}

@Composable
internal fun TicketsHeader(
    compact: Boolean,
    totalCount: Int,
    novosCount: Int,
    onNovos: () -> Unit,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Chamados",
                color = Color.White,
                fontSize = if (compact) 28.sp else 34.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$totalCount chamado(s) • $novosCount novo(s) de cliente",
                color = Color.White.copy(alpha = 0.42f),
                fontSize = 13.sp,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onNovos,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB300)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 13.dp),
            ) {
                Text("Novos", fontWeight = FontWeight.SemiBold)
                if (novosCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    CountBadge(novosCount, Color(0xFFFFB300))
                }
            }
            OutlinedButton(
                onClick = onRefresh,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 13.dp),
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Atualizar", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onCreate,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF20E65B),
                    contentColor = Color.Black,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Novo chamado", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CountBadge(count: Int, accent: Color) {
    Box(
        modifier = Modifier
            .background(accent, CircleShape)
            .heightIn(min = 18.dp)
            .widthIn(min = 18.dp)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(count.toString(), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun TicketsSummaryRow(summary: TicketsSummary, compact: Boolean) {
    val items = listOf(
        "Total" to summary.total,
        "Abertos" to summary.abertos,
        "Novos cliente" to summary.novosCliente,
        "Resolvidos" to summary.resolvidos,
    )
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val narrow = maxWidth < 980.dp
        if (narrow) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { SummaryCard(it.first, it.second.toString(), Modifier.weight(1f)) }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items.forEach { SummaryCard(it.first, it.second.toString(), Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(0.45f)
                    .background(Color(0xFF20E65B).copy(alpha = 0.65f), RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
internal fun TicketsFilterPanel(
    search: String,
    onSearchChange: (String) -> Unit,
    statusFilter: TicketStatus?,
    onStatusChange: (TicketStatus?) -> Unit,
    origemFilter: TicketOrigem?,
    onOrigemChange: (TicketOrigem?) -> Unit,
    categoriaFilter: TicketCategoria?,
    onCategoriaChange: (TicketCategoria?) -> Unit,
    onClearFilters: () -> Unit,
    compact: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            val activeCount = listOf(
                search.isNotBlank(),
                statusFilter != null,
                origemFilter != null,
                categoriaFilter != null,
            ).count { it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        Icons.Default.FilterAlt,
                        null,
                        tint = if (activeCount > 0) Color(0xFF20E65B) else Color.White.copy(alpha = 0.42f),
                        modifier = Modifier.size(18.dp),
                    )
                    Text("Filtros", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                TextButton(
                    onClick = onClearFilters,
                    enabled = activeCount > 0,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Icon(
                        Icons.Default.FilterAltOff,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = if (activeCount > 0) Color(0xFFFF4A4A) else Color.White.copy(alpha = 0.25f),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Limpar tudo",
                        color = if (activeCount > 0) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.25f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            SearchField(
                value = search,
                onValueChange = onSearchChange,
                placeholder = "Buscar por título, descrição ou número...",
            )

            CategoriaSelectField(
                value = categoriaFilter,
                onSelect = onCategoriaChange,
                modifier = Modifier.fillMaxWidth(),
            )

            OrigemStrip(origemFilter = origemFilter, onOrigemChange = onOrigemChange, compact = compact)
            StatusStrip(statusFilter = statusFilter, onStatusChange = onStatusChange, compact = compact)
        }
    }
}

@Composable
internal fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                null,
                tint = if (value.isBlank()) Color.White.copy(alpha = 0.35f) else Color(0xFF20E65B),
            )
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                Surface(onClick = { onValueChange("") }, shape = CircleShape, color = Color.White.copy(alpha = 0.06f)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Limpar busca",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(6.dp).size(16.dp),
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.02f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
            focusedBorderColor = if (value.isBlank()) Color.White.copy(alpha = 0.10f) else Color(0xFF20E65B).copy(alpha = 0.45f),
            unfocusedBorderColor = if (value.isBlank()) Color.White.copy(alpha = 0.08f) else Color(0xFF20E65B).copy(alpha = 0.30f),
            cursorColor = Color(0xFF20E65B),
        ),
    )
}

@Composable
internal fun CategoriaSelectField(
    value: TicketCategoria?,
    onSelect: (TicketCategoria?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val active = value != null
    val selectedName = value?.label() ?: "Todas as categorias"

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            label = { Text("Categoria", color = if (active) Color(0xFF20E65B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.45f)) },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    null,
                    tint = if (active) Color(0xFF20E65B) else Color.White.copy(alpha = 0.40f),
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.02f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.10f),
                unfocusedBorderColor = if (active) Color(0xFF20E65B).copy(alpha = 0.30f) else Color.White.copy(alpha = 0.08f),
                cursorColor = Color(0xFF20E65B),
            ),
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = {
                Text(
                    "Todas as categorias",
                    color = if (!active) Color(0xFF20E65B) else Color.White,
                    fontWeight = if (!active) FontWeight.SemiBold else FontWeight.Normal,
                )
            },
            trailingIcon = if (!active) {
                { Icon(Icons.Default.Check, null, tint = Color(0xFF20E65B), modifier = Modifier.size(18.dp)) }
            } else null,
            onClick = { onSelect(null); expanded = false },
        )
        TicketCategoria.entries.forEach { categoria ->
            val isSelected = categoria == value
            DropdownMenuItem(
                text = {
                    Text(
                        categoria.label(),
                        color = if (isSelected) Color(0xFF20E65B) else Color.White,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                trailingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, null, tint = Color(0xFF20E65B), modifier = Modifier.size(18.dp)) }
                } else null,
                onClick = { onSelect(categoria); expanded = false },
            )
        }
    }
}

@Composable
private fun OrigemStrip(
    origemFilter: TicketOrigem?,
    onOrigemChange: (TicketOrigem?) -> Unit,
    compact: Boolean,
) {
    val chips: List<Pair<TicketOrigem?, String>> = listOf(
        null to "Todas origens",
        TicketOrigem.CLIENTE to "Cliente",
        TicketOrigem.INTERNO to "Interno",
    )
    ChipStrip(
        chips = chips,
        isSelected = { it == origemFilter },
        onClick = onOrigemChange,
        compact = compact,
    )
}

@Composable
private fun StatusStrip(
    statusFilter: TicketStatus?,
    onStatusChange: (TicketStatus?) -> Unit,
    compact: Boolean,
) {
    val chips: List<Pair<TicketStatus?, String>> = buildList {
        add(null to "Todos status")
        TicketStatus.entries.forEach { add(it to it.label()) }
    }
    ChipStrip(
        chips = chips,
        isSelected = { it == statusFilter },
        onClick = onStatusChange,
        compact = compact,
    )
}

@Composable
private fun <T> ChipStrip(
    chips: List<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onClick: (T) -> Unit,
    compact: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.FilterAlt, null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(18.dp))
        chips.forEach { (value, label) ->
            val selected = isSelected(value)
            Surface(
                onClick = { onClick(value) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) Color(0xFF20E65B).copy(alpha = 0.16f) else Color.White.copy(alpha = 0.03f),
                border = BorderStroke(
                    1.dp,
                    if (selected) Color(0xFF20E65B).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                ),
            ) {
                Text(
                    text = label,
                    color = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.72f),
                    fontSize = if (compact) 11.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
internal fun TicketsTable(
    tickets: List<TicketResponse>,
    isLoading: Boolean,
    compact: Boolean,
    onView: (TicketResponse) -> Unit,
    onEdit: (TicketResponse) -> Unit,
    onStatus: (TicketResponse) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C120E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            when {
                isLoading -> LoadingState()
                tickets.isEmpty() -> EmptyState("Nenhum chamado encontrado.")
                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
                    tickets.forEach { ticket ->
                        TicketRow(
                            ticket = ticket,
                            compact = compact,
                            onView = { onView(ticket) },
                            onEdit = { onEdit(ticket) },
                            onStatus = { onStatus(ticket) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketRow(
    ticket: TicketResponse,
    compact: Boolean,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onStatus: () -> Unit,
) {
    Surface(
        onClick = onView,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.025f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ticket.status.accent().copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "#${ticket.numero}",
                    color = ticket.status.accent(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    ticket.titulo.ifBlank { "Sem título" },
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    listOf(
                        ticket.categoria.label(),
                        ticket.clienteLabel(),
                        formatTicketDate(ticket.createdAt),
                    ).joinToString(" • "),
                    color = Color.White.copy(alpha = 0.44f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!compact) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (ticket.origem == TicketOrigem.CLIENTE) {
                        BadgePill(text = "CLIENTE", accent = Color(0xFF5BC0FF))
                    }
                    BadgePill(text = ticket.prioridade.label(), accent = ticket.prioridade.accent())
                    BadgePill(text = ticket.status.label(), accent = ticket.status.accent())
                }
            } else {
                BadgePill(text = ticket.status.label(), accent = ticket.status.accent())
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(Icons.Default.Visibility, Color(0xFF20E65B), onView)
                RoundIconButton(Icons.Default.SwapHoriz, Color(0xFF5BC0FF), onStatus)
                RoundIconButton(Icons.Default.Edit, Color.White.copy(alpha = 0.7f), onEdit)
            }
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.20f)),
        modifier = Modifier.size(36.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
internal fun TicketsPagerBar(
    currentPage: Int,
    totalPages: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Mostrando página $currentPage de $totalPages · $totalItems registros",
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 12.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onPrev,
                enabled = currentPage > 1,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Anterior")
            }
            Button(
                onClick = onNext,
                enabled = currentPage < totalPages,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20E65B), contentColor = Color.Black),
            ) {
                Text("Próxima")
            }
        }
    }
}

@Composable
internal fun LoadingState() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF20E65B))
    }
}

@Composable
internal fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp)
    }
}

@Composable
internal fun TicketsErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f)),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Close, null, tint = Color(0xFFFF4A4A))
            Spacer(Modifier.width(10.dp))
            Text("Erro: $message", color = Color.White, fontSize = 14.sp)
        }
    }
}
