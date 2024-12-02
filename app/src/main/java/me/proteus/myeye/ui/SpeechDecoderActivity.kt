package me.proteus.myeye.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.vosk.Model
import org.vosk.Recognizer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SpeechDecoderActivity : ComponentActivity() {

    private lateinit var recognizer: Recognizer
    private lateinit var model: Model
    private lateinit var audioRecord: AudioRecord
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    initializeVosk()
                } else {
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
        var result by remember { mutableStateOf("Nasłuchuję...") }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Rozpoznawanie Mowy") })
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = result, style = MaterialTheme.typography.bodyLarge)
                }
            }
        )

        LaunchedEffect(Unit) {
            startRecognition { newResult ->
                result = newResult
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeVosk() {

        executor.execute {

            val bufferSize = AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            var modelPath: String = this.filesDir.path + "/model"

            println(modelPath)

            model = Model(modelPath)
            recognizer = Recognizer(model, 16000.0f)
            recognizer.setWords(true)
            recognizer.setPartialWords(true)

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