package com.example.billionemotosappkt.shared.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClient(
    requestTimeoutMillis: Long,
): HttpClient = HttpClient(Darwin)
