package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import me.proteus.myeye.ui.components.AppBarsHeightHolder
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import org.w3c.dom.HTMLElement

external fun initMap(top: Int, bottom: Int)

@Composable
actual fun MapScreen() {
    MyEyeTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar() }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Mapa")
            }
        }
    }
    println("init map")
    val mapDiv = document.createElement("div") as HTMLElement
    mapDiv.id = "map"

    document.body?.insertBefore(mapDiv, document.body?.firstChild)

    val topHeight = AppBarsHeightHolder.topBarHeight / 2
    val bottomHeight = AppBarsHeightHolder.bottomBarHeight / 2

    initMap(topHeight, bottomHeight)
}


@Composable
actual fun PlaceDetailsScreen(placeId: String) {

}