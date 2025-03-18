package me.proteus.myeye.io

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.microphone.RECORD_AUDIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import kotlin.math.PI
import kotlin.math.sin

actual class ASRViewModel : ViewModel() {

    private var recognizer: Recognizer? = null
    private var model: Model? = null
    private var audioRecord: AudioRecord? = null
    private var samplerate: Int? = null
    actual var grammarMapping: MutableMap<String, String>? = null

    private var _wordBuffer = MutableStateFlow<List<String>>(emptyList())
    actual val wordBuffer: StateFlow<List<String>> = _wordBuffer.asStateFlow()

    private fun updateBuffer(list: List<String>) {
        _wordBuffer.value += list
    }

    init {
        println("Init model")
    }

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

        if (isRunning) {

            Log.e("ASR", "Running")

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

            if (fs.exists(modelPath) && fs.list(modelPath).isNotEmpty()) {
                model = Model(modelPath.toString())
                println(modelPath.toString())
                initRecognizer()
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
                            model = Model(modelPath.toString())
                            initRecognizer()
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
    }

    fun initRecognizer() {

        println("init recognizer")
        var grammar: String
        if (grammarMapping!!.isNotEmpty()) {
            grammar = "[\"" + grammarMapping!!.values.joinToString(
                separator = "\",\"",
            ) + "\"]"
            recognizer = Recognizer(model, samplerate!!.toFloat(), grammar).apply {
                setWords(true)
                setPartialWords(true)
            }
        } else {
            recognizer = Recognizer(model, samplerate!!.toFloat()).apply {
                setWords(true)
                setPartialWords(true)
//            setMaxAlternatives(2)
            }
        }

        audioRecord?.startRecording()

        viewModelScope.launch(Dispatchers.Default) {

            val buffer = ByteArray(4096)

            while (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0) {
                    if (recognizer?.acceptWaveForm(buffer, bytesRead) == true) {
                        withContext(Dispatchers.Main) {
                            processWords(deserialize(recognizer!!.result))
                        }
                        println("update")

                    }
                }
            }

        }

    }

    private fun playSineWave(frequency: Double, durationMs: Int, sampleRate: Int) {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val generatedSound = ShortArray(numSamples)

        for (i in generatedSound.indices) {
            val angle = 2.0 * PI * i * frequency / sampleRate
            generatedSound[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSound.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(generatedSound, 0, generatedSound.size)
        audioTrack.play()
    }

    private fun processWords(words: List<SpeechDecoderResult>) {
        val doubtThreshold = 0.7
        if (words.isNotEmpty()) {
            for (el in words) {
                println("${el.word} ${el.confidence}")
                if (el.confidence < doubtThreshold) {
                    playSineWave(2800.0, 400, samplerate!!)
                    return
                }
            }
            playSineWave(700.0, 400, samplerate!!)
            updateBuffer(words.map { it.word })
        }
        return
    }

    actual fun clearBuffer() {
        _wordBuffer.value = emptyList()
        return
    }

    actual fun close() {
        viewModelScope.launch(Dispatchers.IO) {

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.stop()
                audioRecord?.release()
            }

            audioRecord = null
            recognizer?.close()
            recognizer = null
            model?.close()
            model = null
        }
    }

}