package me.proteus.myeye.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.proteus.myeye.io.FileSaver
import me.proteus.myeye.io.HTTPDownloaderDialog
import me.proteus.myeye.io.HTTPRequestViewModel
import me.proteus.myeye.R
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.io.File

const val hostname = "https://tldrhostname.pl"

object HttpClientFactory {
    fun create(): HttpClient {
        return HttpClient(getEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private fun getEngine(): HttpClientEngineFactory<*> = Android
}

val httpClient = HttpClientFactory.create()

@Composable
fun ArticleBrowserScreen(controller: NavController) {

    val context = LocalContext.current

    var local = remember { mutableStateListOf<Int>() }
    var downloadable = remember { mutableStateListOf<Int>() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        local += getLocalArticleFiles(context)

        scope.launch {
            try {
                val hash = getLocalArticleHash(context).toString()
                val path = "$hostname/list/$hash"

                val res = httpClient.get(path).body<String>()

                downloadable.addAll(res
                    .removeSuffix(" ")
                    .split(' ')
                    .filter { it != " " }
                    .map { it.toInt() }
                )

            } catch (e: Exception) {
                println("Błąd: ${e.message}")
            }

        }
    }

    MyEyeTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar(controller) }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.article_browser_local))
                Column {
                    local.forEach {
                        Article(controller, it, false) {
                            newId -> Log.d("ARB", "Skipping update of local $newId")
                        }
                    }
                }

                Text(stringResource(R.string.article_browser_remote))
                Column {
                    downloadable.forEach {
                        Article(controller, it, true) { newId ->
                            local.add(newId)
                            downloadable.removeAt(downloadable.find { it == newId }!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Article(
    controller: NavController,
    id: Int,
    downloadable: Boolean,
    updateList: (Int) -> Unit
) {

    var articleText by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (downloadable) DownloadArticle(id) { text -> articleText = text }
    else articleText = readArticle(id)

    var metadata = articleText.split('\n', ignoreCase = false, limit = 4).toMutableList()
    if (metadata.isNotEmpty()) metadata.removeAt(metadata.size-1)

    val text = articleText
        .removePrefix(metadata.joinToString("\n"))
        .replace('\n', ' ')

    val opis = getLocalizedDescription(metadata, getReadingTime(text))

    val loading = stringResource(R.string.loading)

    val title = if (metadata.isEmpty()) loading else metadata[0]

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxSize()
            .clickable { controller.navigate("article/$id") },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
//        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            Modifier
                .height(IntrinsicSize.Max)
        ) {
            val url = "$hostname/header/$id"
            val painter = if (downloadable) {
                rememberAsyncImagePainter(url)
            } else {
                val file = File(context.filesDir, "articles/$id/img01.jpg")
                if (file.exists()) rememberAsyncImagePainter(file)
                else rememberAsyncImagePainter(url)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .fillMaxHeight()
                    .background(Color.White)
                    .paint(
                        painter,
                        contentScale = ContentScale.FillHeight,
                        alignment = Alignment.TopEnd
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (downloadable) {

                    val model: HTTPRequestViewModel = viewModel()
                    val showDialog = model.showDialog.collectAsState()
                    val progress = model.progressFlow.collectAsState()

                    val scope = rememberCoroutineScope()

                    if (showDialog.value) {
                        HTTPDownloaderDialog(progress.value) { model.setShowDialog(false) }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = me.proteus.myeye.ui.theme.MyEyeBlue),
                        onClick = {
                            model.setShowDialog(true)
                            scope.launch {
                                val url = "$hostname/$id"
                                val downloaderPromise = model.download(url,
                                    File(context.filesDir.path + "/articles/$id/$id.zip"),
                                    File(context.filesDir.path + "/articles/$id/article.txt")
                                )
                                downloaderPromise.thenRun {
                                    updateList(id)
                                    model.setShowDialog(false)
                                    FileSaver.unzip(File(context.filesDir.path + "/articles/$id/$id.zip"))
                                }.exceptionally { ex ->
                                    Log.e("ArticleBrowser", ex.message!!)
                                    null
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                }
            }

            Column {
                Text(
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                    text = text,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                    text = opis,
                    fontSize = 11.sp
                )

            }


        }
    }
}

@Composable
fun DownloadArticle(id: Int, update: (String) -> Unit) {
    val downloaderScope = rememberCoroutineScope()
    val errorMessage = stringResource(R.string.error)
    LaunchedEffect(Unit) {
        downloaderScope.launch {
            val text = try {
                val path = "$hostname/article/$id"
                httpClient.get(path).body()
            } catch (e: Exception) {
                "$errorMessage: ${e.message}"
            }
            update(text)
        }
    }
}

@Composable
fun getLocalizedDescription(metadata: List<String>, time: Int): String {

    if (metadata.isEmpty()) return ""

    val author = metadata[1]
    val obrazki = metadata[2]

    val by = stringResource(R.string.article_author)
    val readTime = stringResource(R.string.article_read_time)

    var opis = "$by: $author • $time $readTime"

    val lang = LocalConfiguration.current.locales[0]

    if (lang.displayName == "pl") {
        if (obrazki != "0") opis += " • $obrazki grafik"
        if (obrazki == "1") opis += 'a'
        val j = obrazki.last()
        if (j == '2' || j == '3' || j == 'u') opis += 'i'
    } else {
        if (obrazki != "0") opis += " • $obrazki image"
        if (obrazki.toInt() > 1) opis += 's'
    }

    println(lang)
    return opis
}

@Composable
fun readArticle(id: Int): String {

    val context = LocalContext.current
    val filePath = File(context.filesDir, "articles/$id/article.txt")

    val fileContent = if (filePath.exists()) {
        filePath.readText()
    } else {
        "Plik nie istnieje!"
    }

    return fileContent

}

fun getReadingTime(text: String): Int {
    val words = text.split("\\s+".toRegex()).size
    val wpm = 180
    return (words / wpm) + if (words % wpm > 0) 1 else 0
}

fun getLocalArticleFiles(context: Context): MutableList<Int> {
    val path = File(context.filesDir, "articles")
    path.mkdirs()

    return FileSaver
        .scanDirectory(path, false)
        .map { it.name.toInt() }
        .toMutableList()
}

fun getLocalArticleHash(context: Context): ULong {
    val files = getLocalArticleFiles(context)
    var found = "1"

    for (i in 0..<63) {
        found += if (files.contains(i)) '1' else '0'
    }

    println(found)

    return found.toULong(2)
}