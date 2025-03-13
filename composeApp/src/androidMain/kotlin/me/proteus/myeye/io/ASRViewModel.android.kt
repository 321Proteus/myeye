package me.proteus.myeye.io

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.proteus.myeye.GrammarType
import org.vosk.Model
import org.vosk.Recognizer
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.modelName
import me.proteus.myeye.resources.model_download
import me.proteus.myeye.resources.phonetic
import me.proteus.myeye.ui.components.HTTPDownloaderDialog
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import java.io.File

actual class ASRViewModel : ComposeViewModel() {

    private var recognizer: Recognizer? = null
    private var model: Model? = null
    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var samplerate: Int? = null

    actual val _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer = _wordBuffer.asStateFlow()

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

    @SuppressLint("MissingPermission")
    private fun getMaximumSampleRate(): Int {

        val all = listOf(48000, 44100, 22050, 16000, 11025, 8000)

        for (rate in all) {
            val bufsize = AudioRecord.getMinBufferSize(
                rate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            if (bufsize != AudioRecord.ERROR && bufsize != AudioRecord.ERROR_BAD_VALUE) {
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    rate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufsize
                )

                if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.release()
                    return rate
                }

                audioRecord.release()
            }
        }

        return 0

    }

    @SuppressLint("MissingPermission")
    @Composable
     actual fun start(vararg grammarTypes: GrammarType) {

         println("running")

        samplerate = getMaximumSampleRate()
        val bufferSize = AudioRecord.getMinBufferSize(
            samplerate!!,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            samplerate!!,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val modelName = stringResource(Res.string.modelName)
        val fs = getFS()
        val modelPath = getPath("models/$modelName")

        var isDownloaded by remember { mutableStateOf(false) }

        if (fs.exists(modelPath)) {
            model = Model(modelPath.toString())
            println(modelPath.toString())
            InitRecognizer(*grammarTypes)
        } else {
            val dlModel = remember { HTTPRequestViewModel() }
            val showDialog = dlModel.showDialog.collectAsState()
            val progress = dlModel.progressFlow.collectAsState()

            val rootUrl = "https://alphacephei.com/vosk/models/"

            dlModel.downloadFile(
                url = "$rootUrl$modelName.zip",
                output = "$modelPath.zip".toPath(),
                post = "$modelPath".toPath()
            ) {
                unzip("$modelPath.zip".toPath(), false)
                model = Model(modelPath.toString())
            }

            if (showDialog.value) {
                HTTPDownloaderDialog(
                    text = stringResource(Res.string.model_download),
                    percent = progress.value
                ) { dlModel.setShowDialog(false) }
            }
            if (isDownloaded) InitRecognizer()
        }
    }

    @Composable
    fun InitRecognizer(vararg grammarTypes: GrammarType) {

        val grammarMapping = loadGrammarMapping(grammarTypes.toList())
        println("init recognizer")
        recognizer = Recognizer(model, samplerate!!.toFloat()).apply {
            setWords(true)
            setPartialWords(true)
//            setMaxAlternatives(2)

                if (grammarMapping.isNotEmpty()) {
                    setGrammar("[\"" + grammarMapping.values.joinToString(
                        separator = "\",\"",
                    ) + "\"]")
                }

            }

        audioRecord?.startRecording()
        isListening = true

        val buffer = ByteArray(4096)

        while (isListening) {
            val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (bytesRead > 0) {
                if (recognizer?.acceptWaveForm(buffer, bytesRead) == true) {
                    println(recognizer?.result)
                }
            }
        }

    }

    actual fun close() {
    }

}