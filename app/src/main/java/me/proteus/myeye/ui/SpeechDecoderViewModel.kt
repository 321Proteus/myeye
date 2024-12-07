package me.proteus.myeye.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.proteus.myeye.io.SpeechDecoderResult

class SpeechDecoderViewModel : ViewModel() {

    var result: List<SpeechDecoderResult> by mutableStateOf(listOf())

    fun addWord(word: SpeechDecoderResult) {
        result = result + word
    }

}