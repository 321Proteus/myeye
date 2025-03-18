package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.proteus.myeye.GrammarType
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.modelName
import me.proteus.myeye.resources.phonetic
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.w3c.dom.MessageEvent


external fun initSpeech(name: String, language: String, grammar: String)

external fun test(a: String, b: Int)

external fun getKey(obj: JsAny, key: String)

actual class ASRViewModel {

    private val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer: StateFlow<List<String>> = _wordBuffer

    actual var grammarMapping: MutableMap<String, String>? = null

    init {
        println("started model")
    }

    @Composable
    private fun loadGrammarMapping(grammarTypes: List<GrammarType>): MutableMap<String, String> {

        val grammar = mutableMapOf<String, String>()

        grammarTypes.forEach { type ->
            type.items.forEach { grammar[it] = it }
        }

        val phoneticWords = stringArrayResource(Res.array.phonetic)
        for (i in phoneticWords.indices) {
            val overrideKey = phoneticWords[i].split(':')[0]
            val overrideValue = phoneticWords[i].split(':')[1]

            if (grammar.contains(overrideKey)) {
                grammar[overrideKey] = overrideValue
            }

        }
        return grammar

    }

    @Composable
    actual fun start(vararg grammarTypes: GrammarType) {
        var grammar = ""
        val modelName = stringResource(Res.string.modelName)

        grammarMapping = loadGrammarMapping(grammarTypes.toList())

        try {
            if (grammarMapping!!.isNotEmpty()) {
                grammar = "[\"" + grammarMapping!!.values.joinToString(
                    separator = "\",\"",
                ) + "\"]"
            }

            test("abc", 3)
            initSpeech(modelName, Locale.current.language, grammar)

            window.onmessage = { event: MessageEvent ->
                println("key: " + getKey(event.data!!, "test"))
            }

        } catch (e: Exception) {
            println(e.message)
            e.cause?.printStackTrace()
        }

    }

    actual fun close() {
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
    }

}