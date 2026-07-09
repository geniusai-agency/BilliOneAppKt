package com.example.billionemotosappkt.desktop.admin.screens.planosparts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.billionemotosappkt.shared.api.PlanoResponse

/** Dados coletados no formulário de plano (criação ou edição). */
internal data class PlanoFormData(
    val nome: String,
    val nivel: String,
    val valor: Double,
    val descricao: String?,
    val tags: List<String>,
    val ativo: Boolean,
)

/**
 * Diálogo de criação/edição de plano.
 * @param plano quando nulo, o formulário opera em modo de criação.
 */
@Composable
internal fun PlanoFormDialog(
    plano: PlanoResponse?,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (PlanoFormData) -> Unit,
) {
    val isEditing = plano != null
    var nome by remember { mutableStateOf(plano?.nome.orEmpty()) }
    var nivel by remember { mutableStateOf(plano?.nivel.orEmpty()) }
    var valor by remember { mutableStateOf(plano?.valor?.let { formatMoneyBRL(parseMoney(it)) }.orEmpty()) }
    var descricao by remember { mutableStateOf(plano?.descricao.orEmpty()) }
    var tags by remember { mutableStateOf(plano?.tags?.joinToString(", ").orEmpty()) }
    var ativo by remember { mutableStateOf(plano?.ativo ?: true) }

    val valorParsed = parseMoney(valor)
    val canSave = nome.isNotBlank() && nivel.isNotBlank() && valorParsed > 0 && !isSaving

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF0B0F0C),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier.widthIn(min = 460.dp, max = 520.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            if (isEditing) "Editar plano" else "Novo plano",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (isEditing) "Atualize as condições comerciais do plano." else "Defina as condições do novo plano comercial.",
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 12.sp,
                        )
                    }
                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.06f),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(8.dp).size(16.dp),
                        )
                    }
                }

                FormField(label = "Nome", value = nome, onValueChange = { nome = it }, placeholder = "Ex.: Plano Start")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        label = "Nível",
                        value = nivel,
                        onValueChange = { nivel = it },
                        placeholder = "Ex.: Basic, Plus, Premium",
                        modifier = Modifier.weight(1f),
                    )
                    FormField(
                        label = "Valor mensal",
                        value = valor,
                        onValueChange = { valor = it },
                        placeholder = "R$ 0,00",
                        modifier = Modifier.weight(1f),
                    )
                }
                FormField(
                    label = "Descrição",
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = "Resumo do que o plano oferece",
                    singleLine = false,
                )
                FormField(
                    label = "Tags (separadas por vírgula)",
                    value = tags,
                    onValueChange = { tags = it },
                    placeholder = "Ex.: km ilimitado, seguro, manutenção",
                )

                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Plano ativo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (ativo) "Disponível para novos contratos" else "Oculto para novas assinaturas",
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 11.sp,
                            )
                        }
                        Switch(
                            checked = ativo,
                            onCheckedChange = { ativo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = PlanoPalette.accent,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                            ),
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(errorMessage, color = PlanoPalette.danger, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = {
                            onSubmit(
                                PlanoFormData(
                                    nome = nome.trim(),
                                    nivel = nivel.trim(),
                                    valor = valorParsed,
                                    descricao = descricao.trim().ifBlank { null },
                                    tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                    ativo = ativo,
                                ),
                            )
                        },
                        enabled = canSave,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlanoPalette.accent,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.08f),
                            disabledContentColor = Color.White.copy(alpha = 0.35f),
                        ),
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(if (isEditing) "Salvar alterações" else "Criar plano", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp) },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 2,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.02f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                focusedBorderColor = PlanoPalette.accent.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                cursorColor = PlanoPalette.accent,
            ),
        )
    }
}
