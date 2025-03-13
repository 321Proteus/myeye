package me.proteus.myeye.io

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
                val res = client.get(url) {
                    headers {
                        append("Referer", "https://alphacephei.com/vosk/models")
                    }
                }
                println("Res: $res")
                val size = res.contentLength() ?: 0L
                val data = res.bodyAsChannel()
                println("running dl")

                fs.sink(output, false).buffer().use { sink ->

                    var bytesRead = 0L
                    val buffer = ByteArray(8192)

                    while (!data.isClosedForRead) {
                        val read = data.readAvailable(buffer)
                        if (read == -1) break

                        sink.write(buffer, 0, read)
                        bytesRead += read

                        if (size > 0) {
                            val progress = (bytesRead.toFloat() / size) * 100
                            println(progress)
                            _progressFlow.value = progress
                        }
                    }

                    sink.flush()
                    _showDialog.value = false
                    then()

                }
            } catch (e: Exception) {
                println("Cause: " + e.message)
            } //finally {
//                client.close()
//            }
        }

        println("sigma")
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

}