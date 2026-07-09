package com.example.billionemotosappkt.shared.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual open class ViewModel actual constructor() {
    actual val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    actual open fun onCleared() {}

    fun clear() {
        viewModelScope.cancel()
        onCleared()
    }
}
