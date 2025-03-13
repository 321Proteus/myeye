package me.proteus.myeye.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleScreen(id: Int) {

    var articleText by remember { mutableStateOf("") }
    DownloadArticle(id) { articleText = it }

    println("Started article with text: $articleText")

    if (articleText.isNotEmpty()) {

        val metadata = articleText.split('\n', ignoreCase = false, limit = 4).toMutableList()
        if (metadata.isNotEmpty()) metadata.removeAt(metadata.size-1)

        val text = articleText
            .removePrefix(metadata.joinToString("\n"))
            .replace('\n', ' ')

//    val opis = getLocalizedDescription(metadata, getReadingTime(text))

//    val loading = stringResource(Res.string.loading)

        val title = metadata[0]
        val headerPath = "$hostname/header/$id"
        println("path $headerPath")

        MyEyeTheme {
            Scaffold(
                topBar = { TopBar() },
                bottomBar = { BottomBar() }
            ) {innerPadding ->

                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
//                    .verticalScroll(rememberScrollState())
                ) {
                    item {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            painter = rememberAsyncImagePainter(headerPath),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    stickyHeader {
                        Text(title, fontSize = 36.sp)
                    }

                    item {
                        val article = articleText.split('\n')
                        Text(article.subList(3, article.size).joinToString("\n"))
//                        for (i in 0..100)
//                            Text("Treść")
                    }
                }
            }
        }
    }
}