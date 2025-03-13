package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.proteus.myeye.GrammarType
import swiftSrc.SpeechRecognizer

actual class ASRViewModel : ComposeViewModel() {

    actual val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer = _wordBuffer.asStateFlow()

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun start(vararg grammarTypes: GrammarType) {
        val list = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("").toMutableList()
        list.removeAll(listOf("", ""))
        println(list)
        val recognizer = SpeechRecognizer()
        SpeechRecognizer.requestAuthorizationWithCallback { auth ->
            println("Authorized: $auth")
            recognizer.startRecognitionWithGrammar(list) {
                if (it != null) {
                    _wordBuffer.value = _wordBuffer
                        .value.toMutableList() + it.split(" ").toList()
                    println(_wordBuffer.value)
                }
            }
        }

    }

    actual fun close() {
    }
}