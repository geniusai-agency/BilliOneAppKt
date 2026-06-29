package com.example.billionemotosappkt.shared.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json

expect fun createPlatformHttpClient(
    requestTimeoutMillis: Long,
): HttpClient

fun createBillioneMotosHttpClient(config: ApiConfig): HttpClient {
    return createPlatformHttpClient(
        requestTimeoutMillis = config.requestTimeoutMillis,
    ).config {
        install(ContentNegotiation) {
            json(BillioneMotosJson)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeoutMillis
            connectTimeoutMillis = config.requestTimeoutMillis
            socketTimeoutMillis = config.requestTimeoutMillis
        }
    }
}
