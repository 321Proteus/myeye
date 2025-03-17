package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.proteus.myeye.GrammarType

actual class ASRViewModel {

    private val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer: StateFlow<List<String>> = _wordBuffer

    actual var grammarMapping: MutableMap<String, String>? = null

    @Composable
    actual fun start(vararg grammarTypes: GrammarType) {
    }

    actual fun close() {
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
    }

}