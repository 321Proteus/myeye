package me.proteus.myeye.io

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.client.utils.DEFAULT_HTTP_BUFFER_SIZE
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import me.proteus.myeye.getDriver
import okio.Path
import okio.buffer
import okio.use

actual class HTTPRequestViewModel : ViewModel() {

    val client = HttpClient(getDriver())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var _progressFlow = MutableStateFlow(0f)
    val progressFlow: StateFlow<Float> = _progressFlow

    private var _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> =_showDialog

    init {
        println("Started model")
    }

    fun setShowDialog(value: Boolean) {
        _showDialog.value = value
    }

    fun downloadFile(
        url: String,
        output: Path,
        post: Path,
        then: suspend () -> Unit
    ) {
        scope.launch {
            val fs = getFS()

            val path = output.parent ?: output
            fs.createDirectories(path)

            if (!fs.exists(path)) {
                throw RuntimeException("Sciezka do katalogu nie istnieje i nie mogla zostac utworzona")
            }

            if (fs.exists(post)) {
                println("plik juz istnieje")
                return@launch
            } else {
                println("download: lecim dalej")
            }

            _showDialog.value = true

            try {
                println("start at $url")

                client.prepareGet(url).execute { res ->

                    println("Res: $res")
                    val size = res.contentLength() ?: 0L
                    println("running dl")

                    println(res)
                    val channel: ByteReadChannel = res.body()

                    fs.sink(output, false).buffer().use { sink ->
                        var bytesRead = 0L
                        while (!channel.isClosedForRead) {
                            val packet = channel.readRemaining(DEFAULT_HTTP_BUFFER_SIZE.toLong())
                            while (!packet.exhausted()) {
                                val bytes = packet.readByteArray()
                                sink.write(bytes)
                                bytesRead += bytes.size
                                _progressFlow.value = bytesRead.toFloat() / size
//                                println("Received $bytesRead bytes from $size")
                            }
                        }
                        sink.flush()
                        _showDialog.value = false
                        then()
                    }
                }
            } catch (e: Exception) {
                println("Cause: " + e.message)
            } //finally {
//                client.close()
//            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

}