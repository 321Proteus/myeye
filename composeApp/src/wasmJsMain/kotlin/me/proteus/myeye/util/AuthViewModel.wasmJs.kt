package me.proteus.myeye.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class WasmBiometryAuth : BiometryAuth {

    override suspend fun authorize(text: List<String>): Boolean {
        return true
    }
}

@Composable
actual fun biometryViewModelProvider(): BiometryViewModel {
    val authenticator = remember { WasmBiometryAuth() }
    return remember { BiometryViewModel(authenticator) }
}