package com.example.billionemotosappkt.shared.utils

import kotlinx.coroutines.CoroutineScope

expect open class ViewModel() {
    val viewModelScope: CoroutineScope
    open fun onCleared()
}
