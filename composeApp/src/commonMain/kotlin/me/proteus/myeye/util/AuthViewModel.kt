package me.proteus.myeye.util

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.proteus.myeye.io.ComposeViewModel

class BiometryViewModel(
    private val biometryAuthenticator: BiometryAuth
) : ComposeViewModel() {

    private var _isAuthorized = MutableStateFlow(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var isAuthorized: StateFlow<Boolean> = _isAuthorized

    fun tryToAuth(vararg texts: String, callback: () -> Unit) = scope.launch {
        try {
            val isSuccess = biometryAuthenticator.authorize(texts.toList())

            if (isSuccess) {
                _isAuthorized.value = true
                println("okay")
                callback()
            }
        } catch (throwable: Throwable) {
            println(throwable.message)
        }
    }
}

interface BiometryAuth {
    suspend fun authorize(text: List<String>): Boolean
}

@Composable
expect fun biometryViewModelProvider(): BiometryViewModel