package com.example.billionemotosappkt.screens.dashboard.customer

import com.example.billionemotosappkt.shared.utils.formatBRL
import com.example.billionemotosappkt.shared.utils.formatBrDate

fun formatCurrency(value: String?): String {
	val numeric = value?.replace(Regex("[^0-9,.-]"), "")?.replace(".", "")?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
	return formatBRL(numeric)
}

fun formatCurrency(value: Double): String {
	return formatBRL(value)
}

fun initialsFrom(name: String?): String {
	return name
		.orEmpty()
		.split(" ")
		.filter { it.isNotBlank() }
		.take(2)
		.joinToString("") { part -> part.first().uppercaseChar().toString() }
		.ifBlank { "B" }
}

fun labelFromContract(contractName: String?): String {
	return contractName?.takeIf { it.isNotBlank() } ?: "Sem contrato ativo"
}

fun formatDisplayDate(value: String?): String {
	if (value.isNullOrBlank()) return "Indisponivel"
	return formatBrDate(value) ?: value
}
