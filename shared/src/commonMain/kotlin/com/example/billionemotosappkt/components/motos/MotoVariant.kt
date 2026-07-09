package com.example.billionemotosappkt.components.motos

data class MotoVariant(
	val title: String,
	val subtitle: String,
	val imageRes: String,
	val monthlyPrice: String,
	val consumption: String,
	val details: List<Pair<String, String>>,
	val specs: List<Pair<String, String>>,
)
