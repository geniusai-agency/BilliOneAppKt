package com.example.billionemotosappkt.desktop.admin.clientes.repository

import com.example.billionemotosappkt.desktop.admin.clientes.model.ClienteListItem
import com.example.billionemotosappkt.desktop.admin.clientes.model.toListItem
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListClientesQuery

data class ClientesLoadResult(
    val clientes: List<ClienteListItem>,
    val isFallbackData: Boolean,
)

class ClientesRepository(
    private val api: BillioneMotosApi,
) {
    suspend fun loadClientes(): ClientesLoadResult {
        val response = api.clientes.list(
            ListClientesQuery(
                page = 1,
                limit = 20,
            )
        )
        return ClientesLoadResult(
            clientes = response.items.map { it.toListItem() },
            isFallbackData = false,
        )
    }
}
