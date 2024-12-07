package me.proteus.myeye.ui

import android.annotation.SuppressLint
import android.app.Application
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import me.proteus.myeye.io.FileSaver
import me.proteus.myeye.io.HTTPDownloader
import me.proteus.myeye.io.SpeechDecoderResult
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SpeechDecoderViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var recognizer: Recognizer
    private lateinit var model: Model
    private lateinit var audioRecord: AudioRecord
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var modelName: String = "vosk-model-small-pl-0.22"
    private var context = getApplication<Application>().applicationContext

    val modelGrammar = listOf<String>("a", "a", "a", "a", "be", "ce", "de", "e", "f",
        "gdzie", "ha", "i", "jod", "ka", "el", "m", "n", "o", "p", "q", "r", "er",
        "es", "te", "u", "wał", "wu", "ix", "igrek", "zet",
        "jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć", "zero"
    )

    var result: List<SpeechDecoderResult> by mutableStateOf(listOf())

    fun addWord(word: SpeechDecoderResult) {
        result = result + word
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
    fun initialize() {
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

            var modelDir: File = File(context.filesDir.path + "/models/" + modelName)

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
                addWord(el)
            }

        }
    }


     fun startRecognition(onResult: (String) -> Unit) {
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

    fun close() {
        audioRecord.stop()
        audioRecord.release()
        executor.shutdown()
    }

}