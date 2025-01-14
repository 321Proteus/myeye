package me.proteus.myeye.io

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.netty.handler.codec.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl
import org.asynchttpclient.HttpResponseBodyPart
import org.asynchttpclient.HttpResponseStatus
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture

class HTTPRequestViewModel : ViewModel() {

    private var client: AsyncHttpClient? = null
    private val _progressFlow = MutableStateFlow(0f)
    val progressFlow: StateFlow<Float> = _progressFlow

    private var _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> =_showDialog

    init {
        client = Dsl.asyncHttpClient(
            Dsl.config()
                .setFollowRedirect(true)
        )
    }


    fun setShowDialog(value: Boolean) {
        _showDialog.value = value
    }

    suspend fun download(url: String?, output: File): CompletableFuture<Void?> {
        return withContext(Dispatchers.IO) {
            println(url)

            val promise = CompletableFuture<Void?>()

            val path = File(output.parent ?: output.path)

            if (!path.mkdirs() && !path.exists()) {
                throw RuntimeException("Sciezka do katalogu nie istnieje i nie mogla zostac utworzona")
            }

            try {
                val fos = FileOutputStream(output)

                client!!.prepareGet(url).execute(object : AsyncHandler<Void?> {
                    private var total: Long = 0
                    private var downloaded: Long = 0

                    override fun onStatusReceived(responseStatus: HttpResponseStatus): AsyncHandler.State {
                        println("Status: " + responseStatus.statusCode)
                        return AsyncHandler.State.CONTINUE
                    }

                    override fun onHeadersReceived(headers: HttpHeaders): AsyncHandler.State {
                        if (headers.contains("Content-Length")) {
                            total = headers["Content-Length"].toLong()
                            println("Do pobrania: $total")
                        }
                        return AsyncHandler.State.CONTINUE
                    }

                    @Throws(Exception::class)
                    override fun onBodyPartReceived(bodyPart: HttpResponseBodyPart): AsyncHandler.State {
                        fos.write(bodyPart.bodyPartBytes)
                        downloaded += bodyPart.length()
                        _progressFlow.value = downloaded.toFloat() / total
                        return AsyncHandler.State.CONTINUE
                    }

                    override fun onThrowable(t: Throwable) {
                        try {
                            fos.close()
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                        promise.completeExceptionally(t)
                    }

                    @Throws(Exception::class)
                    override fun onCompleted(): Void? {
                        fos.close()
                        promise.complete(null)
                        return null
                    }
                })

                promise
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

}