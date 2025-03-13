package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.proteus.myeye.GrammarType

actual class ASRViewModel {


    actual val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer: StateFlow<List<String>> = _wordBuffer

    @Composable
    actual fun start(vararg grammarTypes: GrammarType) {
    }

    actual fun close() {
    }

}