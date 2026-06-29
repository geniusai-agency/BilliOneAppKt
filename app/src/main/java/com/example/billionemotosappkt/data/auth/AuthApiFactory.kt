package com.example.billionemotosappkt.data.auth

import android.content.Context

/**
 * Factory for creating the Android auth/session stack.
 */
fun createBillioneMotosAuthStack(
    context: Context,
    baseUrl: String,
): BillioneMotosAppContainer = BillioneMotosAppContainer(
    context = context.applicationContext,
    baseUrl = baseUrl,
)
