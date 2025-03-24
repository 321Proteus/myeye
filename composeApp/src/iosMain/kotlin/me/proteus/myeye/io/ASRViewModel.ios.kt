package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.microphone.RECORD_AUDIO
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.proteus.myeye.GrammarType
import myeye.composeapp.generated.resources.Res
import myeye.composeapp.generated.resources.modelName
import myeye.composeapp.generated.resources.model_download
import myeye.composeapp.generated.resources.phonetic
import me.proteus.myeye.ui.components.HTTPDownloaderDialog
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import platform.AudioToolbox.AudioServicesPlaySystemSound
import swiftSrc.NativeRecognizer

actual class ASRViewModel : ComposeViewModel() {

    private var _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer = _wordBuffer.asStateFlow()

    actual var grammarMapping: MutableMap<String, String>? = null
    @OptIn(ExperimentalForeignApi::class)
    private var nativeRecognizer: NativeRecognizer? = null

    @Composable
    private fun loadGrammarMapping(grammarTypes: List<GrammarType>): MutableMap<String, String> {

        val grammar = mutableMapOf<String, String>()

        grammarTypes.forEach{ type ->
            type.items.forEach { grammar[it] = it }
        }

        val phoneticWords = stringArrayResource(Res.array.phonetic)
        for (i in phoneticWords.indices) {
            println(i)

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

        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) { factory.createPermissionsController() }

        var isRunning by remember { mutableStateOf(false) }
        var fsbsbdjbjds by remember { mutableStateOf(false) }
        BindEffect(controller)

        LaunchedEffect(controller) {
            controller.providePermission(Permission.RECORD_AUDIO)
            isRunning = true
        }

        grammarMapping = loadGrammarMapping(grammarTypes.toList())

        val modelName = stringResource(Res.string.modelName)
        val fs = getFS()
        val modelPath = getPath("models/$modelName")

        if (fs.exists(modelPath) && fs.list(modelPath).isNotEmpty()) {
            println(modelPath.toString())
            initAudio(modelPath.toString())
        } else {
            val dlModel = remember { HTTPRequestViewModel() }
            val showDialog = dlModel.showDialog.collectAsState()
            val progress = dlModel.progressFlow.collectAsState()

            val rootUrl = "https://alphacephei.com/vosk/models/"
            LaunchedEffect(Unit) {
                dlModel.downloadFile(
                    url = "$rootUrl$modelName.zip",
                    output = "$modelPath.zip".toPath(),
                    post = "$modelPath".toPath()
                ) {
                    val isUnzipped = unzip("$modelPath.zip".toPath(), false)
                    if (isUnzipped) {
                        fsbsbdjbjds = true

                    }
                }
            }

            if (showDialog.value) {
                HTTPDownloaderDialog(
                    text = stringResource(Res.string.model_download),
                    percent = progress.value
                ) { dlModel.setShowDialog(false) }
            }
        }

    }

    @OptIn(ExperimentalForeignApi::class)
    fun initAudio(modelPath: String) {

        println("amogus")

        var grammar = "[\"" + grammarMapping!!.values
            .joinToString(separator = "\",\"",) + "\"]"

        nativeRecognizer = NativeRecognizer()
        NativeRecognizer.requestAuthorizationWithCallback { auth ->
            println("Authorized: $auth")
            nativeRecognizer!!.startRecognitionWithGrammar(grammar, modelPath) {
                if (it != null) {
                    processWords(deserialize(it))
                }
            }
        }
    }

    private fun processWords(words: List<SpeechDecoderResult>) {
        val doubtThreshold = 0.7
        if (words.isNotEmpty()) {
            for (el in words) {
                println("${el.word} ${el.confidence}")
                if (el.confidence < doubtThreshold) {
                    viewModelScope.launch(Dispatchers.Main) {
                        AudioServicesPlaySystemSound(1006u)
                    }
                    return
                }
            }
            viewModelScope.launch(Dispatchers.Main) {
                AudioServicesPlaySystemSound(1025u)
            }
            _wordBuffer.value += words.map { it.word }
        }
        return
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun close() {
        nativeRecognizer!!.stopRecognition()
    }
}
