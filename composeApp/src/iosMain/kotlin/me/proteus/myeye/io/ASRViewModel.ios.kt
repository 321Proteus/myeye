package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.proteus.myeye.GrammarType
import swiftSrc.SpeechRecognizer

actual class ASRViewModel : ComposeViewModel() {

    private var _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer = _wordBuffer.asStateFlow()


    actual var grammarMapping: MutableMap<String, String>? = null

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun start(vararg grammarTypes: GrammarType) {
        grammarMapping = mutableMapOf()
        val list = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("").toMutableList()
        list.removeAll(listOf("", ""))
        println(list)

        for (el in list) {
            grammarMapping!![el] = el
        }

        val recognizer = SpeechRecognizer()
        SpeechRecognizer.requestAuthorizationWithCallback { auth ->
            println("Authorized: $auth")
            val values = grammarMapping!!.values.toList()
            recognizer.startRecognitionWithGrammar(values) {
                if (it != null) {
                    _wordBuffer.value += it.split(" ").toList()
                    println(_wordBuffer.value)
                }
            }
        }

    }

    actual fun close() {
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
    }
}