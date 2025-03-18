package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.ExpandableGrid
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.menu_description
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val Int.fixedSp
    @Composable get() = (this / LocalDensity.current.fontScale).sp

@Composable
fun StringResource.res(): String = stringResource(this)

@Composable
fun MainMenu() {

//    Res.allStringResources.forEach {
//        println("${it.key} : ${stringResource(it.value)}")
//    }

    MyEyeTheme {
        MenuScreen()
    }

}

@Composable
fun MenuScreen() {

    var screenSize by remember { mutableStateOf(Pair(0, 0)) }

    val conn = ResultDataSaver.getConnection()
    ResultDataSaver.createTable(conn)

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() },
        content = { innerPadding ->
            Layout(
                 measurePolicy = { measurables, constraints ->
                    val width = constraints.maxWidth
                    val height = constraints.maxHeight

                    screenSize = Pair(width, height)
                    println("Width: $width, height: $height")

                    val placeables = measurables.map { measurable ->
                        measurable.measure(constraints)
                    }

                    layout(width, height) {
                        var yPosition = 0
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = 0, y = yPosition)
                            yPosition += placeable.height
                        }
                    }
            }, content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        fontSize = 12.sp,
                        text = stringResource(Res.string.menu_description),
                        textAlign = TextAlign.Center
                    )

//                val filePath = getPath("test.txt")
//                val appRoot = getPath(null)
//                println(appRoot)
//
//                println(listFiles(appRoot, false).map { it.relativeTo(appRoot) })
//
//                var text by remember { mutableStateOf(readFromFile(filePath)) }
//
//                Button(onClick = {
//                    writeToFile(filePath, (Random.nextUInt() % 50u).toString())
//                    text = readFromFile(filePath)
//                }) {
//                    Text(text)
//                }

                    Spacer(Modifier.padding(16.dp))

//                val height = LocalWindowInfo.current.containerSize.height

                    ExpandableGrid((screenSize.second / 2).dp, false)

                }
            })
        }
    )
}
