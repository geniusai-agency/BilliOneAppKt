package com.example.billionemotosappkt.desktop.admin.data

import com.example.billionemotosappkt.shared.api.MotoStatus

data class MotosFilters(
	val query: String = "",
	val status: MotoStatus? = null,
)