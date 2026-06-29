package com.example.billionemotosappkt.shared.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createPlatformHttpClient(
    requestTimeoutMillis: Long,
): HttpClient = HttpClient(OkHttp)
