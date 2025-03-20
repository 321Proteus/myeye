package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.dom.isElement
import me.proteus.myeye.GrammarType
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.modelName
import me.proteus.myeye.resources.phonetic
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

external fun initSpeech(name: String, language: String, grammar: String)
external fun closeSpeech()

external fun initResultContainer()

actual class ASRViewModel: ComposeViewModel() {

    private val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer: StateFlow<List<String>> = _wordBuffer

    actual var grammarMapping: MutableMap<String, String>? = null
    private var isActive: Boolean = false

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
        val modelName = stringResource(Res.string.modelName)

        grammarMapping = loadGrammarMapping(grammarTypes.toList())
        if (!isActive) {
            println("Skipping initial load")
            isActive = true
        } else {
            // TODO: Figure out why it runs twice
            initRecognition(modelName)
        }

    }

    fun initRecognition(modelName: String) {
        viewModelScope.launch {
            var grammar = ""
            if (grammarMapping!!.isNotEmpty()) {
                grammar = "[\"" + grammarMapping!!.values.joinToString(
                    separator = "\",\"",
                ) + "\"]"
            }

            initResultContainer()

            initSpeech(modelName, Locale.current.language, grammar)

            val resultObservable = document.getElementById("speech-result")
            println("ID " + resultObservable?.id)
            println("Type: ${resultObservable?.isConnected} ${resultObservable?.isElement}")

            isActive = true

            var previousContent = ""

            // TODO: Replace polling with some
            while (isActive) {
                delay(500)
                val element = document.getElementById("speech-result")
                val content = element?.textContent
                if (!content.isNullOrEmpty() && content != previousContent) {
                    val processed = deserialize(content)
                    _wordBuffer.value += processed.map { it.word }
                    println(processed.size)
                    previousContent = content
                }
            }

        }
    }

    actual fun close() {
        if (isActive) {
            closeSpeech()
            isActive = false
        }
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
    }

}