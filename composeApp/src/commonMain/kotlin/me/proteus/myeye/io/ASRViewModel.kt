package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import me.proteus.myeye.GrammarType

expect class ASRViewModel() {

    val wordBuffer: StateFlow<List<String>>
    var grammarMapping: MutableMap<String, String>?

    @Composable
    fun start(vararg grammarTypes: GrammarType)

    fun clearBuffer()

    fun close()

}