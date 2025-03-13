package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import me.proteus.myeye.getDriver
import me.proteus.myeye.navigate
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.*
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource

const val hostname = "https://6976-37-30-55-4.ngrok-free.app"

val httpClient = HttpClient(getDriver())

@Composable
fun ArticleBrowserScreen() {

    val articles = remember { mutableStateListOf<Int>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        scope.launch {
            try {
                val hash = getLocalArticleHash(".".toPath()).toString()
                val path = "$hostname/list/$hash"

                println("running")

                val res = httpClient.get(path).body<String>()

                articles.addAll(res
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
            bottomBar = { BottomBar() }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(Res.string.article_browser_local))
                Column {
                    articles.forEach { Article(it) }
                }
            }
        }
    }
}

@Composable
fun Article(id: Int) {

    println("Display article $id")

    var articleText by remember { mutableStateOf("") }
    DownloadArticle(id) { articleText = it }

    val metadata = articleText.split('\n', ignoreCase = false, limit = 4).toMutableList()
    if (metadata.isNotEmpty()) metadata.removeAt(metadata.size-1)

    val text = articleText
        .removePrefix(metadata.joinToString("\n"))
        .replace('\n', ' ')

    val opis = getLocalizedDescription(metadata, getReadingTime(text))

    val loading = stringResource(Res.string.loading)

    val title = if (metadata.isEmpty()) loading else metadata[0]

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxSize()
            .clickable { navigate("article/$id") },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
//        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            Modifier.height(120.dp)
//                .height(IntrinsicSize.Max)
        ) {

            val url = "$hostname/header/$id"
            val painter = rememberAsyncImagePainter(url)

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
            )

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
    val errorMessage = stringResource(Res.string.error)
    LaunchedEffect(Unit) {
        downloaderScope.launch {
            val text = try {
                println("running dl $id")
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

    val by = stringResource(Res.string.article_author)
    val readTime = stringResource(Res.string.article_read_time)

    var opis = "$by: $author • $time $readTime"

    val lang = Locale.current.language

    if (lang == "pl") {
        if (obrazki != "0") opis += " • $obrazki grafik"
        if (obrazki == "1") opis += 'a'
        val j = obrazki.last()
        if (j == '2' || j == '3' || j == 'u') opis += 'i'
    } else {
        if (obrazki != "0") opis += " • $obrazki image"
        if (obrazki.toInt() > 1) opis += 's'
    }

    return opis
}

fun getReadingTime(text: String): Int {
    val words = text.split("\\s+".toRegex()).size
    val wpm = 180
    return (words / wpm) + if (words % wpm > 0) 1 else 0
}

expect fun getLocalArticleFiles(path: Path): MutableList<Int>

fun getLocalArticleHash(path: Path): ULong {
    val files = getLocalArticleFiles(path)
    var found = "1"

    for (i in 0..<63) {
        found += if (files.contains(i)) '1' else '0'
    }

    println(found)

    return found.toULong(2)
}

//fun getLocalArticleHash(): ULong {
//    var found = "1"
//
//    for (i in 0..<63) {
//        found += if (Random.nextBoolean()) '1' else '0'
//    }
//
//    println(found)
//
//    return found.toULong(2)
//}
