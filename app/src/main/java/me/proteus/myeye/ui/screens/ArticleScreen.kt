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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleScreen(controller: NavController, id: Int) {

    val articleText = readArticle(id)

    val metadata = articleText.split('\n', ignoreCase = false, limit = 4).toMutableList()
    if (metadata.isNotEmpty()) metadata.removeAt(metadata.size-1)

    val text = articleText
        .removePrefix(metadata.joinToString("\n"))
        .replace('\n', ' ')

//    val opis = getLocalizedDescription(metadata, getReadingTime(text))

//    val loading = stringResource(Res.string.loading)

    val title = metadata[0]
//    val headerPath = findImage(getPath("articles/$id"), "img01")
    val headerPath = findImage(id, "01")
    println("path $headerPath")

    MyEyeTheme {
        Scaffold(
//            topBar = { TopBar() },
            bottomBar = { BottomBar(controller) }
        ) { innerPadding ->

            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
//                    .verticalScroll(rememberScrollState())
            ) {
                item {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        painter = rememberAsyncImagePainter(headerPath.toString()),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }

                stickyHeader {
                    Text(title + "\n", fontSize = 24.sp)
                }

                item {
                    Text(text)
                }
            }
        }
    }
}

@Composable
fun findImage(articleId: Int, imgId: String): String {
    val ctx = LocalContext.current
    val path = File(ctx.filesDir, "articles/$articleId")
    val file = path.listFiles()?.filter {
        println(it.nameWithoutExtension)
        it.nameWithoutExtension == "img$imgId"
    }[0]

    return file?.path ?: "img01.jpg"
}