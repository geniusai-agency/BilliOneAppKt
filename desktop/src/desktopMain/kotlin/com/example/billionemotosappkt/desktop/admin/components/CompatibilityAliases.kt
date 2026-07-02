package com.example.billionemotosappkt.desktop.admin.components

import com.example.billionemotosappkt.desktop.auth.AuthenticationContextResponse
import com.example.billionemotosappkt.desktop.auth.UserKind
import com.example.billionemotosappkt.desktop.admin.model.AdminDashboardSnapshot

typealias AuthUiState = AuthenticationContextResponse
typealias DashboardOverviewData = AdminDashboardSnapshot

val AuthenticationContextResponse.userName: String
    get() = user.nome

val AuthenticationContextResponse.userEmail: String
    get() = user.email

val AuthenticationContextResponse.isAdmin: Boolean
    get() = user.kind == UserKind.INTERNAL
