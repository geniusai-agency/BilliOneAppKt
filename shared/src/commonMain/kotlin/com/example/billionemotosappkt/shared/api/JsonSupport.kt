package com.example.billionemotosappkt.shared.api

import kotlinx.serialization.json.Json

/**
 * JSON configuration shared by all platform clients.
 */
val BillioneMotosJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = false
    explicitNulls = false
    encodeDefaults = true
    coerceInputValues = true
}
