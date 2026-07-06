package com.example.billionemotosappkt.desktop.admin.data

import com.example.billionemotosappkt.desktop.admin.`fun`.isLocalImagePath
import com.example.billionemotosappkt.desktop.admin.`fun`.toUploadFileRequest
import com.example.billionemotosappkt.shared.api.CreateMotoModeloRequest
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.UpdateMotoModeloRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest

data class MotoModeloFormState(
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

fun MotoModeloFormState.toCreateRequest(): CreateMotoModeloRequest {
	return CreateMotoModeloRequest(
		marca = marca,
		nome = nome,
		cilindrada = cilindrada.toIntOrNull(),
		precoInicial = precoInicial.ifBlank { null },
		combustivel = combustivel.ifBlank { null },
		categoria = categoria.ifBlank { null },
		tipo = tipo.ifBlank { null },
		ano = ano.toIntOrNull(),
		codigoFipe = codigoFipe.ifBlank { null },
		descricao = descricao.ifBlank { null },
		fotoUrls = emptyList(),
	)
}

fun MotoModeloFormState.toUpdateRequest(): UpdateMotoModeloRequest {
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

fun MotoModeloFormState.toImageUpload(): UploadFileRequest? {
	return imagemReferenciaUrl.toUploadFileRequest()
}

fun MotoModeloFormState.imagemReferenciaUploadUrl(): String? {
	val value = imagemReferenciaUrl.trim()
	return value.takeIf { it.isNotBlank() && !isLocalImagePath(it) && !value.startsWith("/") }
}


fun MotoModeloFormState.toResponseLike(previous: MotoModeloResponse? = null): MotoModeloResponse {
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

fun MotoModeloResponse.toFormState(): MotoModeloFormState {
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