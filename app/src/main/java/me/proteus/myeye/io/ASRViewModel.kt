package me.proteus.myeye.io

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.proteus.myeye.LanguageUtils
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import me.proteus.myeye.R

class ASRViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var recognizer: Recognizer
    private lateinit var model: Model
    private lateinit var audioRecord: AudioRecord
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    val grammarMapping = loadGrammarMapping()

    private val _wordBuffer = MutableLiveData<List<SpeechDecoderResult>>(emptyList())
    val wordBuffer: LiveData<List<SpeechDecoderResult>> get() = _wordBuffer

    fun getLocalizedContext(): Context {

        val appContext = getApplication<Application>().applicationContext
        val currentLang = LanguageUtils.getCurrentLanguage(appContext)
        val localizedContext = LanguageUtils.setLocale(appContext, currentLang)

        return localizedContext

    }

    fun loadGrammarMapping(): MutableMap<Char, List<String>> {

        val grammar = mutableMapOf<Char, List<String>>()

        val resources = getLocalizedContext().resources
        val letterArrays = resources.obtainTypedArray(R.array.grammar_mapping)

        for (i in 0 until letterArrays.length()) {
            val arrayId = letterArrays.getResourceId(i, 0)
            if (arrayId != 0) {

                val words = resources.getStringArray(arrayId).toMutableList()
                var charCode = 'a' + i

                if (words.isEmpty()) words.add(charCode.toString())
                grammar[charCode] = words

            }
        }
        letterArrays.recycle()
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
    fun initialize() {

        val context = getApplication<Application>().applicationContext

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

            var modelName = getLocalizedContext().getString(R.string.modelName)
            println("updated modelName to $modelName")

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

        val modelGrammar = mutableListOf<String>()
        for (sublist in grammarMapping.values) {
            modelGrammar.add(sublist.joinToString("\",\""))
        }

        recognizer = Recognizer(model, samplerate.toFloat()).apply {
            setWords(true)
            setPartialWords(true)
//            setMaxAlternatives(2)
            setGrammar("[\"" + modelGrammar.joinToString(
                separator = "\",\"",
            ) + "\"]")
        }

        startRecognition()
    }

     fun startRecognition() {
        executor.execute {
            audioRecord.startRecording()
            val buffer = ByteArray(4096)

            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord.read(buffer, 0, buffer.size)
                if (bytesRead > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        val words = SpeechDecoderResult.deserialize(recognizer.result)
                        for (el in words) {
                            println(el.word)
                        }
                        _wordBuffer.postValue(_wordBuffer.value?.plus(words))

                        var toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 1000)
                        Handler(Looper.getMainLooper()).postDelayed({
                            toneGenerator.release()
                        }, 1000)

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