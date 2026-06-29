package com.example.billionemotosappkt.screens.dashboard.customer

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun formatCurrency(value: String?): String {
	val numeric = value?.replace(Regex("[^0-9,.-]"), "")?.replace(".", "")?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
	return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(numeric)
}

fun formatCurrency(value: Double): String {
	return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)
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
	return runCatching {
		val parser = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
		val date = parser.parse(value)
		date?.let { parser.format(it) } ?: value
	}.getOrDefault(value)
}
