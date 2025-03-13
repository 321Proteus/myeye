package me.proteus.myeye.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual open class ComposeViewModel actual constructor() {
    actual val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    actual fun clear() {
    }
}