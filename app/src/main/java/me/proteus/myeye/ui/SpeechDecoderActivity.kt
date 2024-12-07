package me.proteus.myeye.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import me.proteus.myeye.io.FileSaver
import me.proteus.myeye.io.HTTPDownloader
import me.proteus.myeye.io.SpeechDecoderResult
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.plus

class SpeechDecoderActivity : ComponentActivity() {

    private lateinit var recognizer: Recognizer
    private lateinit var model: Model
    private lateinit var audioRecord: AudioRecord
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var modelName: String = "vosk-model-small-pl-0.22"

    private val viewModel: SpeechDecoderViewModel by viewModels()

    val modelGrammar = listOf<String>("a", "a", "a", "a", "be", "ce", "de", "e", "f",
        "gdzie", "ha", "i", "jod", "ka", "el", "m", "n", "o", "p", "q", "r", "er",
        "es", "te", "u", "wał", "wu", "ix", "igrek", "zet",
        "jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć", "zero"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    initializeVosk()
                } else {
                    println("Brak uprawnien")
                }
            }


        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initializeVosk()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            AppContent()
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun AppContent() {

        val result = viewModel.result

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Rozpoznawanie Mowy") })
            },
            content = { innerPadding ->

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    itemsIndexed(result) { index, item ->

                        Box() {

                            val line = "${item.word}"
                            val probabilityColor = Color(ColorUtils.blendARGB(Color.Red.toArgb(), Color.Green.toArgb(), item.confidence))

                            Text(
                                text = line,
                                fontSize = 24.sp,
                                lineHeight = 24.sp,
                                color = probabilityColor
                            )

                        }

                    }
                }
            }

        )

    }

    @SuppressLint("MissingPermission")
    fun getMaximumSampleRate(): Int {

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
    private fun initializeVosk() {
        executor.execute {

            val samplerate = getMaximumSampleRate()
            println(samplerate)

            val bufferSize = AudioRecord.getMinBufferSize(
                samplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                samplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            var modelDir: File = File(this.filesDir.path + "/models/" + modelName)

            if (!modelDir.exists()) {

                var rootUrl = "https://alphacephei.com/vosk/models/"
                var downloaderPromise = HTTPDownloader().download(
                    "$rootUrl$modelName.zip",
                    File(modelDir.path + ".zip")
                )

                downloaderPromise.thenRun {
                    FileSaver.unzip(File(modelDir.path + ".zip"))
                    model = Model(modelDir.path)

                    initRecognizer(samplerate)

                } .exceptionally { e ->
                    println("Error: $e")
                    return@exceptionally null
                }

            } else {
                model = Model(modelDir.path)
                initRecognizer(samplerate)
            }
        }
    }

    fun initRecognizer(samplerate: Int) {

        recognizer = Recognizer(model, samplerate.toFloat()).apply {
            setWords(true)
            setPartialWords(true)
            setGrammar("[\"" + modelGrammar.joinToString(
                separator = "\",\"",
            ) + "\"]")
        }

        startRecognition { newResult ->
            val words = SpeechDecoderResult.deserialize(newResult)
            for (el in words) {
                viewModel.addWord(el)
            }

        }
    }


    private fun startRecognition(onResult: (String) -> Unit) {
        executor.execute {
            audioRecord.startRecording()
            val buffer = ByteArray(4096)

            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord.read(buffer, 0, buffer.size)
                if (bytesRead > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        onResult(recognizer.result)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecord.stop()
        audioRecord.release()
        executor.shutdown()
    }
}