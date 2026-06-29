package com.example.billionemotosappkt.shared.api

/**
 * Normalized API error for the client layer.
 */
class ApiException(
    val statusCode: Int,
    message: String,
    val rawBody: String? = null,
) : Exception(message)
