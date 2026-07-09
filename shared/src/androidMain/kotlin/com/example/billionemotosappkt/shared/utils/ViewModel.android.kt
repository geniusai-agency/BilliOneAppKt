package com.example.billionemotosappkt.shared.utils

import androidx.lifecycle.viewModelScope as androidxViewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class ViewModel : androidx.lifecycle.ViewModel() {
    actual val viewModelScope: CoroutineScope
        get() = androidxViewModelScope

    public actual override fun onCleared() {
        super.onCleared()
    }
}
