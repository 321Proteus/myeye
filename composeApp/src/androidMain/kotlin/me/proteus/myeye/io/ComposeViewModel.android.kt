package me.proteus.myeye.io

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual open class ComposeViewModel : ViewModel() {
    actual val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancel()
    }

    actual fun clear() {
        onCleared()
    }

}