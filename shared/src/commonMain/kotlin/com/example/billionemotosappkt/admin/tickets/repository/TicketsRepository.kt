package com.example.billionemotosappkt.desktop.admin.tickets.repository

import com.example.billionemotosappkt.desktop.admin.tickets.model.TicketsLoadResult
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.CreateTicketCommentRequest
import com.example.billionemotosappkt.shared.api.CreateTicketRequest
import com.example.billionemotosappkt.shared.api.ListTicketsQuery
import com.example.billionemotosappkt.shared.api.TicketArquivoResponse
import com.example.billionemotosappkt.shared.api.TicketCommentResponse
import com.example.billionemotosappkt.shared.api.TicketOrigem
import com.example.billionemotosappkt.shared.api.TicketResponse
import com.example.billionemotosappkt.shared.api.TicketStatus
import com.example.billionemotosappkt.shared.api.UpdateTicketRequest
import com.example.billionemotosappkt.shared.api.UpdateTicketStatusRequest

class TicketsRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun loadTickets(query: ListTicketsQuery = ListTicketsQuery(limit = 20)): TicketsLoadResult {
        val response = api.tickets.list(query)
        return TicketsLoadResult(
            tickets = response.items,
            total = response.total,
            page = response.page,
            limit = response.limit,
            isFallbackData = false,
        )
    }

    suspend fun getTicket(id: String): TicketResponse = api.tickets.get(id)

    suspend fun createTicket(request: CreateTicketRequest): TicketResponse =
        api.tickets.create(request)

    suspend fun updateTicket(id: String, request: UpdateTicketRequest): TicketResponse =
        api.tickets.update(id, request)

    suspend fun updateStatus(id: String, request: UpdateTicketStatusRequest): TicketResponse =
        api.tickets.updateStatus(id, request)

    suspend fun addComment(id: String, request: CreateTicketCommentRequest): TicketCommentResponse =
        api.tickets.addComment(id, request)

    suspend fun listComments(id: String): List<TicketCommentResponse> =
        api.tickets.listComments(id)

    suspend fun listPhotos(id: String): List<TicketArquivoResponse> =
        api.tickets.listPhotos(id)

    suspend fun addPhoto(id: String, file: com.example.billionemotosappkt.shared.api.UploadFileRequest, descricao: String? = null): TicketArquivoResponse =
        api.tickets.addPhoto(id, file, descricao)

    /** Quantidade de chamados novos de cliente (status ABERTO + origem CLIENTE). */
    suspend fun countNovos(): Int =
        api.tickets.list(
            ListTicketsQuery(
                status = TicketStatus.ABERTO,
                origem = TicketOrigem.CLIENTE,
                limit = 1,
            ),
        ).total
}
