package com.example.billionemotosappkt.`data-class`

/**
 * Slide model for the login/landing carousel.
 *
 * [img] is a multiplatform asset name resolved at runtime via
 * [com.example.billionemotosappkt.shared.utils.rememberAssetPainter] (loaded from the
 * classpath resources), replacing the Android-only `R.drawable` integer id.
 */
data class SlideLogin(
    val title: String,
    val img: String,
    val desc: String,
)
