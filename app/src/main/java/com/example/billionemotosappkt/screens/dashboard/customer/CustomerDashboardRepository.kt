package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.screens.auth.AuthUiState
import com.example.billionemotosappkt.shared.api.ApiException
import com.example.billionemotosappkt.shared.api.BillioneMotosApi
import com.example.billionemotosappkt.shared.api.ListContratosQuery
import com.example.billionemotosappkt.shared.api.ListPagamentosQuery
import com.example.billionemotosappkt.shared.api.MotoModeloResponse
import com.example.billionemotosappkt.shared.api.PlanoResponse
import com.example.billionemotosappkt.shared.api.PagamentoResponse
import com.example.billionemotosappkt.shared.api.ContratoResponse
import com.example.billionemotosappkt.shared.api.PagedResponse
import com.example.billionemotosappkt.shared.api.UserResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class CustomerDashboardSnapshot(
	val user: UserResponse,
	val contratos: List<ContratoResponse>,
	val pagamentos: List<PagamentoResponse>,
	val planos: List<PlanoResponse>,
	val modelos: List<MotoModeloResponse>,
)

class CustomerDashboardRepository(
	private val api: BillioneMotosApi,
) {
	suspend fun load(authState: AuthUiState): CustomerDashboardSnapshot = coroutineScope {
		println("[CustomerDashboardRepository] load started")
		val userDeferred = async { api.users.me() }
		val planosDeferred = async {
			println("[CustomerDashboardRepository] loading planos")
			api.planos.list()
		}
		val modelosDeferred = async {
			println("[CustomerDashboardRepository] loading modelos")
			api.motos.modelos()
		}

		val user = userDeferred.await()
		val clienteId = authState.clienteId ?: user.clienteId

		val contratosDeferred = if (clienteId.isNullOrBlank()) null else async {
			println("[CustomerDashboardRepository] loading contratos clienteId=$clienteId")
			loadPagedOrEmpty("contratos clienteId=$clienteId") {
				api.contratos.list(
					ListContratosQuery(
						clienteId = clienteId,
						includeRelations = true,
						page = 1,
						limit = 20,
					),
				)
			}
		}

		val pagamentosDeferred = if (clienteId.isNullOrBlank()) null else async {
			println("[CustomerDashboardRepository] loading pagamentos clienteId=$clienteId")
			loadPagedOrEmpty("pagamentos clienteId=$clienteId") {
				api.pagamentos.list(
					ListPagamentosQuery(
						clienteId = clienteId,
						includeRelations = true,
						page = 1,
						limit = 20,
					),
				)
			}
		}

		CustomerDashboardSnapshot(
			user = user,
			contratos = contratosDeferred?.await()?.items.orEmpty(),
			pagamentos = pagamentosDeferred?.await()?.items.orEmpty(),
			planos = planosDeferred.await(),
			modelos = modelosDeferred.await(),
		).also {
			println(
				"[CustomerDashboardRepository] load completed contratos=${it.contratos.size} pagamentos=${it.pagamentos.size} planos=${it.planos.size} modelos=${it.modelos.size}"
			)
		}
	}

	private suspend inline fun <reified T> loadPagedOrEmpty(
		label: String,
		block: suspend () -> PagedResponse<T>,
	): PagedResponse<T> {
		return runCatching { block() }.getOrElse { error ->
			if (error is ApiException && error.statusCode == 403) {
				println("[CustomerDashboardRepository] $label blocked by 403: ${error.message}")
				PagedResponse(items = emptyList(), total = 0, page = 1, limit = 20)
			} else {
				throw error
			}
		}
	}
}
