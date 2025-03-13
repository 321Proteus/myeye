package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.proteus.myeye.GrammarType

expect class ASRViewModel() {

    val _wordBuffer: MutableStateFlow<List<String>>
    val wordBuffer: StateFlow<List<String>>

    @Composable
    fun start(vararg grammarTypes: GrammarType)

    fun close()

}