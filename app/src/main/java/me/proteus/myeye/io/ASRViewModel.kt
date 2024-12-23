package me.proteus.myeye.io

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.proteus.myeye.GrammarType
import me.proteus.myeye.LanguageUtils
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import me.proteus.myeye.R
import kotlin.math.sin

class ASRViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var recognizer: Recognizer
    private lateinit var model: Model
    private lateinit var audioRecord: AudioRecord

    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var isOpen: Boolean = false

    lateinit var grammarMapping: MutableMap<String, String>

    private val _wordBuffer = MutableLiveData<List<SpeechDecoderResult>>(emptyList())
    val wordBuffer: LiveData<List<SpeechDecoderResult>> get() = _wordBuffer

    fun getLocalizedContext(): Context {

        val appContext = getApplication<Application>().applicationContext
        val currentLang = LanguageUtils.getCurrentLanguage(appContext)
        val localizedContext = LanguageUtils.setLocale(appContext, currentLang)

        return localizedContext

    }

    fun loadGrammarMapping(grammarTypes: List<GrammarType>): MutableMap<String, String> {

        val grammar = mutableMapOf<String, String>()

        grammarTypes.forEach{ type ->
            type.items.forEach { it ->
                grammar[it] = it
            }
        }

        val resources = getLocalizedContext().resources
        val phoneticWords = resources.getStringArray(R.array.phonetic)

        for (i in 0 until phoneticWords.size) {

            var overrideKey = phoneticWords[i].split(':')[0]
            var overrideValue = phoneticWords[i].split(':')[1]

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
    fun initialize(vararg grammarTypes: GrammarType) {

        val context = getApplication<Application>().applicationContext
        grammarMapping = loadGrammarMapping(grammarTypes.toList())

        isOpen = true

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

        recognizer = Recognizer(model, samplerate.toFloat()).apply {
            setWords(true)
            setPartialWords(true)
//            setMaxAlternatives(2)
            setGrammar("[\"" + grammarMapping.values.joinToString(
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
                        processWords(words)

                    }
                }
            }
        }
    }

    fun processWords(words: List<SpeechDecoderResult>) {

        val doubtThreshold = 0.7

        if (words.isNotEmpty()) {

            for (el in words) {
                println("${el.word} ${el.confidence}")
                if (el.confidence < doubtThreshold) {
                    playTone(1000, 200)
                    Handler(Looper.getMainLooper()).postDelayed({
                    }, 100)
                    playTone(1000, 200)
                    return
                }
            }

            _wordBuffer.postValue(_wordBuffer.value?.plus(words))
            playTone(280, 500)

        }

        return

    }

    fun clearBuffer() {
        _wordBuffer.value = emptyList<SpeechDecoderResult>()
        return
    }

    fun playTone(frequency: Int, duration: Int) {
        val sampleRate = 44100
        val numSamples = duration * sampleRate / 1000
        val samples = ShortArray(numSamples)

        for (i in samples.indices) {
            samples[i] = (sin(2.0 * Math.PI * i / (sampleRate / frequency)) * Short.MAX_VALUE).toInt().toShort()
        }

        val audioManager = getApplication<Application>().applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(rate.toInt())
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .build()

        val audioTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            samples.size * 2,
            AudioTrack.MODE_STREAM,
            0
        )

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()

        Handler(Looper.getMainLooper()).postDelayed({
            audioTrack.release()
        }, duration.toLong())
    }

    fun close() {

        if (!isOpen) return
        isOpen = false

        audioRecord.stop()
        audioRecord.release()
        executor.shutdown()
    }

}