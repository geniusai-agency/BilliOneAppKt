package com.example.billionemotosappkt.desktop.admin.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.billionemotosappkt.desktop.admin.components.*
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot
import com.example.billionemotosappkt.desktop.admin.model.MotoSectionTab
import com.example.billionemotosappkt.shared.api.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.net.URL
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun MotosSection(
    api: BillioneMotosApi,
    apiBaseUrl: String,
    apiAccessToken: String?,
    selectedTab: MotoSectionTab,
    onTabChange: (MotoSectionTab) -> Unit,
) {
    var uiState by remember { mutableStateOf(MotosUiState()) }
    var filters by remember { mutableStateOf(MotosFilters()) }

    LaunchedEffect(api) {
        uiState = uiState.copy(isLoading = true)
        runCatching {
            api.motos.list(ListMotosQuery(page = 1, limit = 50)).items
        }.onSuccess {
            uiState = uiState.copy(isLoading = false, motos = it, errorMessage = null)
        }.onFailure {
            uiState = uiState.copy(isLoading = false, errorMessage = it.message ?: "Erro ao carregar motos")
        }
    }
    
    val filteredMotos = remember(uiState.motos, filters) {
        uiState.motos.filter { moto ->
            val matchesQuery = filters.query.isBlank() ||
                    moto.modelo?.contains(filters.query, ignoreCase = true) == true ||
                    moto.placa.contains(filters.query, ignoreCase = true) ||
                    moto.marca?.contains(filters.query, ignoreCase = true) == true
            
            val matchesStatus = filters.status == null || moto.status == filters.status
            
            matchesQuery && matchesStatus
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            SectionHeader(
                title = "Motos",
                subtitle = "Gerenciamento de frota, disponibilidade e manutenção operacional."
            )
            
            if (selectedTab == MotoSectionTab.FROTA) {
                MotosSearchField(
                    value = filters.query,
                    onValueChange = { filters = filters.copy(query = it) }
                )
            }
        }

        MotosSubsectionToggle(
            selected = selectedTab,
            onSelected = onTabChange,
        )
        
        when (selectedTab) {
            MotoSectionTab.FROTA -> {
                MotoFiltersRow(
                    selectedStatus = filters.status,
                    onStatusChange = { filters = filters.copy(status = it) }
                )

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF20E65B))
                    }
                } else if (uiState.errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = Color(0xFFFF4A4A),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    MotosList(motos = filteredMotos)
                }
            }
            MotoSectionTab.MODELOS -> {
                MotoModelosSection(api = api, apiBaseUrl = apiBaseUrl, apiAccessToken = apiAccessToken)
            }
        }
    }
}

private data class MotoModeloFormState(
    val marca: String = "",
    val nome: String = "",
    val modelo: String = "",
    val cilindrada: String = "",
    val precoInicial: String = "",
    val combustivel: String = "",
    val categoria: String = "",
    val tipo: String = "",
    val ano: String = "",
    val codigoFipe: String = "",
    val descricao: String = "",
    val imagemReferenciaUrl: String = "",
    val imagemReferenciaUpload: UploadFileRequest? = null,
)

private fun MotoModeloFormState.toCreateRequest(): CreateMotoModeloRequest {
    return CreateMotoModeloRequest(
        marca = marca.trim(),
        nome = nome.trim(),
        cilindrada = cilindrada.toIntOrNull(),
        precoInicial = precoInicial.ifBlank { null },
        combustivel = combustivel.ifBlank { null },
        categoria = categoria.ifBlank { null },
        tipo = tipo.ifBlank { null },
        ano = ano.toIntOrNull(),
        codigoFipe = codigoFipe.ifBlank { null },
        descricao = descricao.ifBlank { null },
    )
}

private fun MotoModeloFormState.toUpdateRequest(): UpdateMotoModeloRequest {
    return UpdateMotoModeloRequest(
        marca = marca.ifBlank { null },
        nome = nome.ifBlank { null },
        cilindrada = cilindrada.toIntOrNull(),
        precoInicial = precoInicial.ifBlank { null },
        combustivel = combustivel.ifBlank { null },
        categoria = categoria.ifBlank { null },
        tipo = tipo.ifBlank { null },
        ano = ano.toIntOrNull(),
        codigoFipe = codigoFipe.ifBlank { null },
        descricao = descricao.ifBlank { null },
        imagemReferenciaUrl = imagemReferenciaUploadUrl(),
    )
}

private fun MotoModeloFormState.toImageUpload(): UploadFileRequest? {
    return imagemReferenciaUrl.toUploadFileRequest()
}

private fun MotoModeloFormState.imagemReferenciaUploadUrl(): String? {
    val value = imagemReferenciaUrl.trim()
    return value.takeIf { it.isNotBlank() && !isLocalImagePath(it) && !value.startsWith("/") }
}

private fun String?.toUploadFileRequest(): UploadFileRequest? {
    val path = this?.trim().orEmpty()
    if (!isLocalImagePath(path)) return null

    val file = File(path)
    return UploadFileRequest(
        bytes = file.readBytes(),
        fileName = file.name,
        contentType = guessImageContentType(file.name),
    )
}

private fun MotoModeloFormState.toResponseLike(previous: MotoModeloResponse? = null): MotoModeloResponse {
    return previous?.copy(
        marca = marca.ifBlank { previous.marca },
        nome = nome.ifBlank { previous.nome },
        modelo = modelo.ifBlank { previous.modelo },
        cilindrada = cilindrada.toIntOrNull() ?: previous.cilindrada,
        precoInicial = precoInicial.ifBlank { previous.precoInicial },
        combustivel = combustivel.ifBlank { previous.combustivel },
        categoria = categoria.ifBlank { previous.categoria },
        tipo = tipo.ifBlank { previous.tipo },
        ano = ano.toIntOrNull() ?: previous.ano,
        codigoFipe = codigoFipe.ifBlank { previous.codigoFipe },
        descricao = descricao.ifBlank { previous.descricao },
        imagemReferenciaUrl = imagemReferenciaUrl.ifBlank { previous.imagemReferenciaUrl },
    ) ?: MotoModeloResponse(
        marca = marca.ifBlank { null },
        nome = nome.ifBlank { null },
        modelo = modelo.ifBlank { null },
        cilindrada = cilindrada.toIntOrNull(),
        precoInicial = precoInicial.ifBlank { null },
        combustivel = combustivel.ifBlank { null },
        categoria = categoria.ifBlank { null },
        tipo = tipo.ifBlank { null },
        ano = ano.toIntOrNull(),
        codigoFipe = codigoFipe.ifBlank { null },
        descricao = descricao.ifBlank { null },
        imagemReferenciaUrl = imagemReferenciaUrl.ifBlank { null },
    )
}

private fun MotoModeloResponse.toFormState(): MotoModeloFormState {
    return MotoModeloFormState(
        marca = marca.orEmpty(),
        nome = nome.orEmpty(),
        modelo = modelo.orEmpty(),
        cilindrada = cilindrada?.toString().orEmpty(),
        precoInicial = precoInicial.orEmpty(),
        combustivel = combustivel.orEmpty(),
        categoria = categoria.orEmpty(),
        tipo = tipo.orEmpty(),
        ano = ano?.toString().orEmpty(),
        codigoFipe = codigoFipe.orEmpty(),
        descricao = descricao.orEmpty(),
        imagemReferenciaUrl = imagemReferenciaUrl.orEmpty(),
        imagemReferenciaUpload = null,
    )
}

private fun resolveImageSource(value: String?, apiBaseUrl: String? = null): String {
    val trimmed = value?.trim().orEmpty()
    if (trimmed.isBlank()) return ""
    if (isLocalImagePath(trimmed)) return trimmed
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
    if (trimmed.startsWith("/") && !apiBaseUrl.isNullOrBlank()) {
        return apiBaseUrl.trimEnd('/') + trimmed
    }
    return trimmed
}

@Composable
private fun MotoModeloPanelDialog(
    title: String,
    model: MotoModeloResponse?,
    onDismiss: () -> Unit,
    onSubmit: ((MotoModeloFormState) -> Unit)?,
    submitLabel: String,
    apiBaseUrl: String,
    apiAccessToken: String?,
) {
    var form by remember(model?.id, title) {
        mutableStateOf(
            model?.toFormState() ?: MotoModeloFormState(),
        )
    }
    val heroResource = remember(form.imagemReferenciaUrl, form.marca, form.modelo, apiBaseUrl) {
        resolveImageSource(form.imagemReferenciaUrl, apiBaseUrl).ifBlank {
            getMotoHeroImage(form.marca, form.modelo)
        }
    }
    val cardResource = remember(form.imagemReferenciaUrl, form.marca, form.modelo, apiBaseUrl) {
        resolveImageSource(form.imagemReferenciaUrl, apiBaseUrl).ifBlank {
            getMotoModelCardImage(form.marca, form.modelo)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(30.dp),
            color = Color(0xFF060906),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val stacked = maxWidth < 1100.dp
                if (stacked) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MotoModeloPreviewPane(
                            title = title,
                            heroResource = heroResource,
                            cardResource = cardResource,
                            form = form,
                            onDismiss = onDismiss,
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                        )
                        MotoModeloFormPane(
                            form = form,
                            onFormChange = {
                                form = it
                            },
                            onSubmit = onSubmit,
                            onDismiss = onDismiss,
                            submitLabel = submitLabel,
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        MotoModeloPreviewPane(
                            title = title,
                            heroResource = heroResource,
                            cardResource = cardResource,
                            form = form,
                            onDismiss = onDismiss,
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                            modifier = Modifier.weight(0.44f),
                        )
                        MotoModeloFormPane(
                            form = form,
                            onFormChange = {
                                form = it
                            },
                            onSubmit = onSubmit,
                            onDismiss = onDismiss,
                            submitLabel = submitLabel,
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                            modifier = Modifier.weight(0.56f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MotoModeloPreviewPane(
    title: String,
    heroResource: String,
    cardResource: String,
    form: MotoModeloFormState,
    onDismiss: () -> Unit,
    apiBaseUrl: String,
    apiAccessToken: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D140F), Color(0xFF060906)),
                ),
            )
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text(
                        text = listOf(form.marca, form.nome).joinToString(" ").ifBlank { "Pré-visualização do modelo" },
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 13.sp,
                    )
                }
                TextButton(onClick = onDismiss) { Text("Fechar") }
            }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp)),
            ) {
                Image(
                    painter = desktopImagePainter(heroResource, apiBaseUrl, apiAccessToken),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f)),
                            ),
                        ),
                )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = listOf(form.marca, form.nome).joinToString(" ").ifBlank { "Novo modelo" },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = form.descricao.ifBlank { "Preencha os campos ao lado para ver a ficha do modelo." },
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 12.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MotoModeloSmallTag(text = form.categoria.ifBlank { "Categoria" }, tint = Color(0xFF7DD3FC))
                        MotoModeloSmallTag(text = form.tipo.ifBlank { "Tipo" }, tint = Color(0xFFFFB300))
                        MotoModeloSmallTag(text = form.combustivel.ifBlank { "Combustível" }, tint = Color(0xFF20E65B))
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModelHeroStat(
                    title = "Imagem",
                    value = "catálogo",
                    modifier = Modifier.weight(1f),
                    imageResource = cardResource,
                    apiBaseUrl = apiBaseUrl,
                    apiAccessToken = apiAccessToken,
                )
                ModelHeroStat(
                    title = "Técnico",
                    value = listOfNotNull(form.cilindrada.takeIf { it.isNotBlank() }?.plus("cc"), form.ano.takeIf { it.isNotBlank() }).joinToString(" • ").ifBlank { "sem dados" },
                    modifier = Modifier.weight(1f),
                    imageResource = "moto_detail_engine.png",
                    apiBaseUrl = apiBaseUrl,
                    apiAccessToken = apiAccessToken,
                )
            }
        }
    }
}

@Composable
private fun ModelHeroStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    imageResource: String,
    apiBaseUrl: String,
    apiAccessToken: String?,
) {
    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF0D1210))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp)),
    ) {
        Image(
            painter = desktopImagePainter(imageResource, apiBaseUrl, apiAccessToken),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.58f)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(
                    text = if (title == "Imagem") "Visual da ficha" else "Especificação",
                    color = Color.White.copy(alpha = 0.62f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun MotoModeloFormPane(
    form: MotoModeloFormState,
    onFormChange: (MotoModeloFormState) -> Unit,
    onSubmit: ((MotoModeloFormState) -> Unit)?,
    onDismiss: () -> Unit,
    submitLabel: String,
    apiBaseUrl: String,
    apiAccessToken: String?,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF090D0B))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Ficha do modelo",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val split = maxWidth >= 760.dp
                if (split) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            MotoModeloFormSection(
                                title = "Identidade",
                                subtitle = "Dados principais exibidos na listagem e na ficha.",
                            ) {
                                FormGridField("Marca", form.marca) { onFormChange(form.copy(marca = it)) }
                                FormGridField("Nome", form.nome) { onFormChange(form.copy(nome = it)) }
                                FormGridField("Modelo", form.modelo) { onFormChange(form.copy(modelo = it)) }
                            }
                            MotoModeloFormSection(
                                title = "Especificações",
                                subtitle = "Medições e dados técnicos que ajudam no catálogo.",
                            ) {
                                FormGridField("Cilindrada", form.cilindrada) { onFormChange(form.copy(cilindrada = it)) }
                                FormGridField("Preço inicial", form.precoInicial) { onFormChange(form.copy(precoInicial = it)) }
                                FormGridField("Combustível", form.combustivel) { onFormChange(form.copy(combustivel = it)) }
                            }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            MotoModeloFormSection(
                                title = "Classificação",
                                subtitle = "Segmentação usada para organização e busca.",
                            ) {
                                FormGridField("Categoria", form.categoria) { onFormChange(form.copy(categoria = it)) }
                                FormGridField("Tipo", form.tipo) { onFormChange(form.copy(tipo = it)) }
                                FormGridField("Ano", form.ano) { onFormChange(form.copy(ano = it)) }
                                FormGridField("Código FIPE", form.codigoFipe) { onFormChange(form.copy(codigoFipe = it)) }
                            }
                            MotoModeloFormSection(
                                title = "Mídia",
                                subtitle = "Envie uma imagem real do modelo para o preview.",
                            ) {
                                MotoImagePickerField(
                                    imageValue = form.imagemReferenciaUrl,
                                    onImageChange = {
                                        onFormChange(
                                            form.copy(
                                                imagemReferenciaUrl = it,
                                                imagemReferenciaUpload = form.imagemReferenciaUpload,
                                            ),
                                        )
                                    },
                                    onUploadChange = { upload ->
                                        onFormChange(form.copy(imagemReferenciaUpload = upload))
                                    },
                                    apiBaseUrl = apiBaseUrl,
                                    apiAccessToken = apiAccessToken,
                                )
                            }
                            MotoModeloFormSection(
                                title = "Descrição",
                                subtitle = "Texto descritivo exibido no cartão e no detalhe.",
                            ) {
                                FormGridField("Descrição", form.descricao, singleLine = false, minLines = 4) {
                                    onFormChange(form.copy(descricao = it))
                                }
                            }
                        }
                    }
                } else {
                    MotoModeloFormSection(
                        title = "Identidade",
                        subtitle = "Dados principais do modelo.",
                    ) {
                        FormGridField("Marca", form.marca) { onFormChange(form.copy(marca = it)) }
                        FormGridField("Nome", form.nome) { onFormChange(form.copy(nome = it)) }
                        FormGridField("Modelo", form.modelo) { onFormChange(form.copy(modelo = it)) }
                    }
                    MotoModeloFormSection(
                        title = "Especificações",
                        subtitle = "Informações técnicas e de exibição.",
                    ) {
                        FormGridField("Cilindrada", form.cilindrada) { onFormChange(form.copy(cilindrada = it)) }
                        FormGridField("Preço inicial", form.precoInicial) { onFormChange(form.copy(precoInicial = it)) }
                        FormGridField("Combustível", form.combustivel) { onFormChange(form.copy(combustivel = it)) }
                        FormGridField("Categoria", form.categoria) { onFormChange(form.copy(categoria = it)) }
                        FormGridField("Tipo", form.tipo) { onFormChange(form.copy(tipo = it)) }
                        FormGridField("Ano", form.ano) { onFormChange(form.copy(ano = it)) }
                        FormGridField("Código FIPE", form.codigoFipe) { onFormChange(form.copy(codigoFipe = it)) }
                    }
                    MotoModeloFormSection(
                        title = "Mídia",
                        subtitle = "Use uma imagem real do modelo.",
                    ) {
                        MotoImagePickerField(
                            imageValue = form.imagemReferenciaUrl,
                            onImageChange = {
                                onFormChange(
                                    form.copy(
                                        imagemReferenciaUrl = it,
                                        imagemReferenciaUpload = form.imagemReferenciaUpload,
                                    ),
                                )
                            },
                            onUploadChange = { upload ->
                                onFormChange(form.copy(imagemReferenciaUpload = upload))
                            },
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                        )
                    }
                    MotoModeloFormSection(
                        title = "Descrição",
                        subtitle = "Descrição comercial ou técnica.",
                    ) {
                        FormGridField("Descrição", form.descricao, singleLine = false, minLines = 4) {
                            onFormChange(form.copy(descricao = it))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.weight(1f))
            if (onSubmit != null) {
                Button(
                    onClick = { onSubmit(form) },
                    enabled = form.marca.isNotBlank() && form.nome.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF20E65B),
                        contentColor = Color.Black,
                    ),
                ) {
                    Text(submitLabel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MotoImagePickerField(
    imageValue: String,
    onImageChange: (String) -> Unit,
    onUploadChange: (UploadFileRequest?) -> Unit,
    apiBaseUrl: String,
    apiAccessToken: String?,
) {
    val previewResource = remember(imageValue) {
        resolveMotoImagePreview(imageValue, apiBaseUrl)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Imagem do modelo",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Selecione um arquivo real do computador.",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF111915),
                                    Color(0xFF070A08),
                                ),
                            ),
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = desktopImagePainter(previewResource, apiBaseUrl, apiAccessToken),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                    )
                    if (imageValue.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onImageChange("")
                                onUploadChange(null)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(5.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.Black.copy(alpha = 0.55f)),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remover imagem",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = when {
                            imageValue.isBlank() -> "Nenhuma imagem selecionada"
                            isLocalImagePath(imageValue) -> File(imageValue).name
                            else -> imageValue
                        },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Prefira PNG, JPG ou WEBP. O arquivo local já aparece no preview.",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                pickDesktopImageFile()?.let { file ->
                                    onImageChange(file.absolutePath)
                                    onUploadChange(file.absolutePath.toUploadFileRequest())
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF20E65B),
                                contentColor = Color.Black,
                            ),
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (imageValue.isBlank()) "Selecionar imagem" else "Trocar imagem")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MotoModeloFormSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0D1210))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
            }
            content()
        }
    }
}

@Composable
private fun FormGridField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF20E65B),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            focusedContainerColor = Color(0xFF0F1411),
            unfocusedContainerColor = Color(0xFF0B100D),
        ),
    )
}

private fun MotoModeloResponse.toPanelImage(): String = getMotoModelCardImage(marca, modelo)

private fun getMotoImage(modelo: String?): String {
    return getMotoModelCardImage(null, modelo)
}

private fun resolveMotoImagePreview(imageValue: String?, apiBaseUrl: String? = null): String {
    val value = imageValue?.trim().orEmpty()
    if (value.isBlank()) return "moto_premium.png"
    if (isLocalImagePath(value)) return value
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.startsWith("/") && !apiBaseUrl.isNullOrBlank()) {
        return apiBaseUrl.trimEnd('/') + value
    }
    return value
}

private fun isLocalImagePath(value: String): Boolean {
    val file = File(value)
    return file.exists() && file.isFile
}

private fun guessImageContentType(fileName: String): String {
    return when (File(fileName).extension.lowercase()) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        else -> "application/octet-stream"
    }
}

private fun pickDesktopImageFile(): File? {
    return runCatching {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            isAcceptAllFileFilterUsed = true
            fileFilter = FileNameExtensionFilter("Imagens", "png", "jpg", "jpeg", "webp")
        }
        when (chooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> chooser.selectedFile
            else -> null
        }
    }.getOrNull()
}
@Composable
private fun MotosSubsectionToggle(
    selected: MotoSectionTab,
    onSelected: (MotoSectionTab) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MotosSubsectionChip(
            label = "Frota",
            selected = selected == MotoSectionTab.FROTA,
            onClick = { onSelected(MotoSectionTab.FROTA) },
        )
        MotosSubsectionChip(
            label = "Modelos",
            selected = selected == MotoSectionTab.MODELOS,
            onClick = { onSelected(MotoSectionTab.MODELOS) },
        )
    }
}

@Composable
private fun MotosSubsectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) Color(0xFF20E65B).copy(alpha = 0.15f) else Color(0xFF111614)
    val borderColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.1f)
    val textColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.6f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MotosSearchField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.width(320.dp),
        placeholder = { Text("Buscar placa, modelo...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF20E65B),
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedContainerColor = Color(0xFF111614),
            unfocusedContainerColor = Color(0xFF0D1210)
        )
    )
}

@Composable
private fun MotoFiltersRow(selectedStatus: MotoStatus?, onStatusChange: (MotoStatus?) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        MotoFilterChip(label = "Todas", selected = selectedStatus == null, onClick = { onStatusChange(null) })
        val visibleStatuses = listOf(MotoStatus.DISPONIVEL, MotoStatus.ALUGADA, MotoStatus.MANUTENCAO, MotoStatus.BLOQUEADA)
        visibleStatuses.forEach { status ->
            MotoFilterChip(
                label = when(status) {
                    MotoStatus.DISPONIVEL -> "Disponível"
                    MotoStatus.ALUGADA -> "Alugada"
                    MotoStatus.MANUTENCAO -> "Manutenção"
                    MotoStatus.BLOQUEADA -> "Bloqueada"
                    else -> status.name
                },
                selected = selectedStatus == status,
                onClick = { onStatusChange(status) }
            )
        }
    }
}

@Composable
private fun MotoFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Color(0xFF20E65B).copy(alpha = 0.15f) else Color(0xFF111614)
    val borderColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.1f)
    val textColor = if (selected) Color(0xFF20E65B) else Color.White.copy(alpha = 0.6f)
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MotoModelosSection(api: BillioneMotosApi, apiBaseUrl: String, apiAccessToken: String?) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var models by remember { mutableStateOf<List<MotoModeloResponse>>(emptyList()) }
    var selectedModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
    var creatingModel by remember { mutableStateOf(false) }
    var editingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
    var deletingModel by remember { mutableStateOf<MotoModeloResponse?>(null) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(api) {
        isLoading = true
        runCatching { api.motos.modelos() }
            .onSuccess {
                models = it
                errorMessage = null
            }
            .onFailure {
                errorMessage = it.message ?: "Erro ao carregar modelos"
            }
        isLoading = false
    }

    val filteredModels = remember(models, query) {
        val normalized = query.trim()
        if (normalized.isBlank()) {
            models
        } else {
            models.filter { model ->
                listOfNotNull(
                    model.marca,
                    model.nome,
                    model.modelo,
                    model.categoria,
                    model.tipo,
                    model.combustivel,
                    model.codigoFipe,
                    model.descricao,
                ).any { it.contains(normalized, ignoreCase = true) }
            }
        }
    }

    val totalBrands = remember(models) {
        models.mapNotNull { it.marca?.takeIf(String::isNotBlank) }.distinct().size
    }

    val trackedModels = remember(models) {
        models.count { it.possuiRastreador }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Modelos",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Leitura, edição e exclusão de modelos cadastrados.",
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 14.sp,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.width(320.dp),
                    placeholder = { Text("Buscar marca, nome ou categoria...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF20E65B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF111614),
                        unfocusedContainerColor = Color(0xFF0D1210),
                    ),
                )
                Button(
                    onClick = { creatingModel = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF20E65B),
                        contentColor = Color.Black,
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Novo modelo", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        TwoUpCards(
            first = { modifier ->
                AdminSectionCard(
                    title = "Modelos carregados",
                    subtitle = "Total disponível no catálogo",
                    icon = Icons.Default.DirectionsBike,
                    accent = Color(0xFF20E65B),
                    value = models.size.toString(),
                    modifier = modifier,
                )
            },
            second = { modifier ->
                AdminSectionCard(
                    title = "Com rastreador",
                    subtitle = "Modelos com suporte ativo",
                    icon = Icons.Default.LocationOn,
                    accent = Color(0xFF7DD3FC),
                    value = trackedModels.toString(),
                    modifier = modifier,
                )
            },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MotoModelosMetaPill("Marcas", totalBrands.toString(), Color(0xFF20E65B))
            MotoModelosMetaPill("Exibidos", filteredModels.size.toString(), Color(0xFF7DD3FC))
            MotoModelosMetaPill("Catálogo", models.size.toString(), Color(0xFFFFB300))
        }

        actionMessage?.let { message ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1711)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF20E65B))
            }
        } else if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4A4A).copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4A4A).copy(alpha = 0.2f)),
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF4A4A),
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else if (filteredModels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Nenhum modelo encontrado.", color = Color.White.copy(alpha = 0.45f))
            }
        } else {
            MotoModelosGrid(
                models = filteredModels,
                onView = { selectedModel = it },
                onEdit = { editingModel = it },
                onDelete = { deletingModel = it },
                apiBaseUrl = apiBaseUrl,
                apiAccessToken = apiAccessToken,
            )
        }
    }

    selectedModel?.let { model ->
        MotoModeloPanelDialog(
            title = "Detalhes do modelo",
            model = model,
            onDismiss = { selectedModel = null },
            onSubmit = null,
            submitLabel = "Fechar",
            apiBaseUrl = apiBaseUrl,
            apiAccessToken = apiAccessToken,
        )
    }

    if (creatingModel) {
        MotoModeloPanelDialog(
            title = "Novo modelo",
            model = null,
            onDismiss = { creatingModel = false },
            onSubmit = { form ->
                scope.launch {
                    runCatching {
                        api.motos.createModelo(
                            request = form.toCreateRequest(),
                            imagemReferenciaImage = form.imagemReferenciaUpload,
                        )
                    }
                        .onSuccess { created ->
                            models = listOf(created) + models
                            actionMessage = "Modelo criado com sucesso."
                            creatingModel = false
                        }
                        .onFailure {
                            actionMessage = "Não foi possível criar o modelo no backend."
                        }
                }
            },
            submitLabel = "Criar",
            apiBaseUrl = apiBaseUrl,
            apiAccessToken = apiAccessToken,
        )
    }

    editingModel?.let { model ->
        MotoModeloPanelDialog(
            title = "Editar modelo",
            model = model,
            onDismiss = { editingModel = null },
            onSubmit = { form ->
                val targetId = model.id
                if (targetId.isNullOrBlank()) {
                    models = models.map { current -> if (current == model) form.toResponseLike(model) else current }
                    actionMessage = "Modelo atualizado localmente."
                    editingModel = null
                } else {
                    val request = form.toUpdateRequest()
                    scope.launch {
                        runCatching {
                            api.motos.updateModelo(
                                id = targetId,
                                request = request,
                                imagemReferenciaImage = form.imagemReferenciaUpload,
                            ).let {
                                println(it)
                                it
                            }
                        }
                            .onSuccess { response ->
                                models = models.map { current -> if (current.id == targetId) response else current }
                                actionMessage = "Modelo salvo com sucesso."
                                editingModel = null
                            }
                            .onFailure {
                                models = models.map { current -> if (current.id == targetId) form.toResponseLike(model).copy(id = targetId) else current }
                                actionMessage = "Não foi possível salvar no backend; alteração aplicada localmente."
                                editingModel = null
                            }
                    }
                }
            },
            submitLabel = "Salvar",
            apiBaseUrl = apiBaseUrl,
            apiAccessToken = apiAccessToken,
        )
    }

    deletingModel?.let { model ->
        Dialog(
            onDismissRequest = { deletingModel = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .heightIn(min = 260.dp)
                    .padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF0B100D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Desativar modelo?", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text(
                        text = "Essa ação desativa ${model.marca.orEmpty()} ${model.nome.orEmpty()} da lista de modelos exibidos.",
                        color = Color.White.copy(alpha = 0.72f),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TextButton(onClick = { deletingModel = null }) { Text("Cancelar") }
                            Button(
                                onClick = {
                                    val targetId = model.id
                                    if (targetId.isNullOrBlank()) {
                                        models = models.filterNot { it == model }
                                        actionMessage = "Modelo desativado localmente."
                                        deletingModel = null
                                    } else {
                                        scope.launch {
                                            runCatching { api.motos.deleteModelo(targetId) }
                                                .onSuccess {
                                                    models = models.filterNot { it.id == targetId }
                                                    actionMessage = "Modelo desativado com sucesso."
                                                    deletingModel = null
                                                }
                                                .onFailure {
                                                    models = models.filterNot { it.id == targetId }
                                                    actionMessage = "Não foi possível desativar no backend; remoção aplicada localmente."
                                                    deletingModel = null
                                                }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4A4A), contentColor = Color.White),
                            ) {
                                Text("Desativar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MotoModelosGrid(
    models: List<MotoModeloResponse>,
    onView: (MotoModeloResponse) -> Unit,
    onEdit: (MotoModeloResponse) -> Unit,
    onDelete: (MotoModeloResponse) -> Unit,
    apiBaseUrl: String,
    apiAccessToken: String?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = if (maxWidth >= 1280.dp) 2 else 1
        val rows = models.chunked(columns)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { model ->
                        MotoModeloCard(
                            model = model,
                            onView = { onView(model) },
                            onEdit = { onEdit(model) },
                            onDelete = { onDelete(model) },
                            apiBaseUrl = apiBaseUrl,
                            apiAccessToken = apiAccessToken,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MotoModelosMetaPill(
    label: String,
    value: String,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .background(Color(0xFF0E1411), RoundedCornerShape(14.dp))
            .border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(tint),
            )
            Column {
                Text(text = label, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
                Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MotoModeloCard(
    model: MotoModeloResponse,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    apiBaseUrl: String,
    apiAccessToken: String?,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val imageResource = remember(model.imagemReferenciaUrl, model.marca, model.modelo, apiBaseUrl) {
        resolveImageSource(model.imagemReferenciaUrl, apiBaseUrl).ifBlank {
            getMotoModelCardImage(model.marca, model.modelo)
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(168.dp, 118.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF101915),
                                Color(0xFF080D0A),
                            ),
                        ),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = desktopImagePainter(imageResource, apiBaseUrl, apiAccessToken),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = listOfNotNull(model.marca, model.nome).joinToString(" ").ifBlank { "Modelo sem nome" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = model.descricao?.takeIf { it.isNotBlank() } ?: "Ficha técnica e ações de catálogo.",
                            color = Color.White.copy(alpha = 0.56f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    MotoModeloSmallTag(text = model.categoria ?: "Categoria não definida", tint = Color(0xFF7DD3FC))
                    MotoModeloSmallTag(text = model.tipo ?: "Tipo não definido", tint = Color(0xFFFFB300))
                    MotoModeloSmallTag(text = model.combustivel ?: "Combustível não definido", tint = Color(0xFF20E65B))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    MotoModeloSmallTag(text = model.cilindrada?.let { "${it}cc" } ?: "cc", tint = Color(0xFF20E65B))
                    MotoModeloSmallTag(text = model.ano?.toString() ?: "ano", tint = Color(0xFF7DD3FC))
                    if (!model.id.isNullOrBlank()) {
                        MotoModeloSmallTag(text = model.id.orEmpty().take(6), tint = Color.White.copy(alpha = 0.68f))
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .widthIn(min = 110.dp, max = 160.dp)
                    .align(Alignment.Top),
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Abrir ações",
                            tint = Color.White.copy(alpha = 0.8f),
                        )
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        modifier = Modifier.background(Color(0xFF0E1411)),
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ver detalhes") },
                            onClick = {
                                menuOpen = false
                                onView()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                menuOpen = false
                                onEdit()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Desativar", color = Color(0xFFFF4A4A)) },
                            onClick = {
                                menuOpen = false
                                onDelete()
                            },
                        )
                    }
                }
                Text(
                    text = model.marca?.uppercase()?.take(12) ?: "CATÁLOGO",
                    color = Color(0xFF20E65B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "R$ ${model.precoInicial ?: "n/a"}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Ações no menu",
                    color = Color.White.copy(alpha = 0.42f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun MotoModeloSmallTag(
    text: String,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .background(tint.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
            .border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text = text, color = tint, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun MotosList(motos: List<MotoResponse>) {
    // Usamos Column aqui porque o AdminDashboardScreen já tem um scroll global
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
    ) {
        motos.forEach { moto ->
            MotoCard(moto = moto)
        }
        if (motos.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Nenhuma moto encontrada.", color = Color.White.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun MotoCard(moto: MotoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1210)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val imageName = remember(moto.modelo) { getMotoImage(moto.modelo) }
            Box(
                modifier = Modifier.size(120.dp, 90.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = desktopImagePainter(imageName), contentDescription = null, modifier = Modifier.fillMaxSize().padding(8.dp))
            }
            
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${moto.marca ?: ""} ${moto.modelo ?: ""}".trim().ifBlank { "Moto #${moto.id.take(5)}" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Box(modifier = Modifier.background(Color(0xFF20E65B).copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(text = moto.placa, color = Color(0xFF20E65B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoItem(icon = Icons.Default.Speed, text = "${moto.kmAtual} km")
                    InfoItem(icon = Icons.Default.CalendarToday, text = moto.ano?.toString() ?: "N/A")
                    InfoItem(icon = if (moto.rastreador?.ativo == true) Icons.Default.LocationOn else Icons.Default.LocationOff,
                        text = if (moto.rastreador?.ativo == true) "Rastreador ativo" else "Sem rastreamento")
                }
            }
            
            MotoStatusBadge(status = moto.status)
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.4f))
        Text(text = text, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
    }
}

@Composable
private fun MotoStatusBadge(status: MotoStatus) {
    val (color, label) = when (status) {
        MotoStatus.DISPONIVEL -> Color(0xFF20E65B) to "Disponível"
        MotoStatus.ALUGADA -> Color(0xFF7DD3FC) to "Alugada"
        MotoStatus.MANUTENCAO -> Color(0xFFFFB300) to "Manutenção"
        MotoStatus.BLOQUEADA -> Color(0xFFFF4A4A) to "Bloqueada"
        MotoStatus.CONTRATADA -> Color(0xFF8B5CF6) to "Contratada"
        MotoStatus.PENDENTE_CONTRATO -> Color(0xFFFFB300).copy(alpha = 0.8f) to "Pendente"
        else -> Color.White.copy(alpha = 0.4f) to status.name
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.12f)).border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) {
        Text(text = label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TwoUpCards(
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gap = 16.dp
        val cardWidth = (maxWidth - gap) / 2
        if (cardWidth >= 280.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap), modifier = Modifier.fillMaxWidth()) {
                first(Modifier.width(cardWidth))
                second(Modifier.width(cardWidth))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                first(Modifier.fillMaxWidth())
                second(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun desktopImagePainter(
    resourceName: String,
    apiBaseUrl: String? = null,
    apiAccessToken: String? = null,
): BitmapPainter {
    return remember(resourceName, apiBaseUrl, apiAccessToken) {
        runCatching {
            val resolved = resolveImageSource(resourceName, apiBaseUrl)
            when {
                resolved.isBlank() -> fallbackMotoPainter()
                isLocalImagePath(resolved) -> {
                    FileInputStream(File(resolved)).use { BitmapPainter(loadImageBitmap(it)) }
                }
                resolved.startsWith("http://") || resolved.startsWith("https://") -> {
                    val connection = (URL(resolved).openConnection() as java.net.HttpURLConnection).apply {
                        connectTimeout = 15_000
                        readTimeout = 15_000
                        requestMethod = "GET"
                        setRequestProperty("ngrok-skip-browser-warning", "true")
                        if (!apiAccessToken.isNullOrBlank()) {
                            setRequestProperty("Authorization", "Bearer $apiAccessToken")
                        }
                    }
                    try {
                        val code = runCatching { connection.responseCode }.getOrDefault(500)
                        if (code !in 200..299) {
                            fallbackMotoPainter()
                        } else {
                            connection.inputStream.use { BitmapPainter(loadImageBitmap(it)) }
                        }
                    } finally {
                        connection.disconnect()
                    }
                }
                else -> {
                    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(resolved)
                    if (stream == null) {
                        fallbackMotoPainter()
                    } else {
                        stream.use { BitmapPainter(loadImageBitmap(it)) }
                    }
                }
            }
        }.getOrElse { fallbackMotoPainter() }
    }
}

private fun fallbackMotoPainter(): BitmapPainter {
    val fallback = Thread.currentThread().contextClassLoader.getResourceAsStream("moto_premium.png")
    return fallback?.use { BitmapPainter(loadImageBitmap(it)) }
        ?: error("Fallback image resource `moto_premium.png` not found.")
}

private fun getMotoModelCardImage(marca: String?, modelo: String?): String {
    val combined = listOfNotNull(marca, modelo).joinToString(" ")
    return when {
        combined.contains("Avelloz", ignoreCase = true) -> "avelloz_160_black_new.png"
        combined.contains("Ninja", ignoreCase = true) -> "moto_sport_updated.png"
        combined.contains("Eletrica", ignoreCase = true) -> "moto_eletrica_new.png"
        combined.contains("CG", ignoreCase = true) -> "moto_premium.png"
        else -> "moto_premium.png"
    }
}

private fun getMotoHeroImage(marca: String?, modelo: String?): String {
    val combined = listOfNotNull(marca, modelo).joinToString(" ")
    return when {
        combined.contains("Avelloz", ignoreCase = true) -> "hero_bg_moto1.png"
        combined.contains("Eletrica", ignoreCase = true) -> "hero_bg_moto2.png"
        combined.contains("Ninja", ignoreCase = true) -> "hero_bg_moto1.png"
        else -> "hero_bg_moto2.png"
    }
}

data class MotosUiState(
    val isLoading: Boolean = true,
    val motos: List<MotoResponse> = emptyList(),
    val errorMessage: String? = null,
)

data class MotosFilters(
    val query: String = "",
    val status: MotoStatus? = null,
)
