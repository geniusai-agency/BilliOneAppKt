package com.example.billionemotosappkt.desktop.api.data

data class UploadFileRequest(
	val bytes: ByteArray,
	val fileName: String,
	val contentType: String,
)