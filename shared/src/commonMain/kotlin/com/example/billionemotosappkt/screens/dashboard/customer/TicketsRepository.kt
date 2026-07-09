package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.CreateTicketCommentRequest
import com.example.billionemotosappkt.shared.api.CreateTicketRequest
import com.example.billionemotosappkt.shared.api.ListTicketsQuery
import com.example.billionemotosappkt.shared.api.TicketArquivoResponse
import com.example.billionemotosappkt.shared.api.TicketCommentResponse
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.UpdateTicketRequest
import com.example.billionemotosappkt.shared.api.UploadFileRequest

/**
 * Thin wrapper around [BillioneMotosApi.tickets] following the same style as
 * [CustomerDashboardRepository]. The backend already scopes every ticket call to
 * the logged client, so we only forward what the UI collects.
 */
class TicketsRepository(
	private val api: BillioneMotosApi,
) {
	suspend fun list(): List<TicketResponse> {
		println("[TicketsRepository] list started")
		val response = api.tickets.list(
			ListTicketsQuery(
				origem = TicketOrigem.CLIENTE,
				page = 1,
				limit = 50,
			),
		)
		println("[TicketsRepository] list completed size=${response.items.size}")
		return response.items
	}

	suspend fun get(id: String): TicketResponse {
		println("[TicketsRepository] get id=$id")
		return api.tickets.get(id)
	}

	suspend fun create(request: CreateTicketRequest): TicketResponse {
		println("[TicketsRepository] create titulo=${request.titulo}")
		return api.tickets.create(request)
	}

	suspend fun update(id: String, request: UpdateTicketRequest): TicketResponse {
		println("[TicketsRepository] update id=$id")
		return api.tickets.update(id, request)
	}

	suspend fun addComment(id: String, request: CreateTicketCommentRequest): TicketCommentResponse {
		println("[TicketsRepository] addComment id=$id")
		return api.tickets.addComment(id, request)
	}

	suspend fun addPhoto(id: String, file: UploadFileRequest, descricao: String? = null): TicketArquivoResponse {
		println("[TicketsRepository] addPhoto id=$id file=${file.fileName}")
		return api.tickets.addPhoto(id, file, descricao)
	}
}
