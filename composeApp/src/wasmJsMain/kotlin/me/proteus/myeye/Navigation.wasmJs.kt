package me.proteus.myeye

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.isElement
import me.proteus.myeye.ui.components.TestSelector
import me.proteus.myeye.ui.screens.ArticleBrowserScreen
import me.proteus.myeye.ui.screens.ArticleScreen
import me.proteus.myeye.ui.screens.MapScreen
import me.proteus.myeye.ui.screens.MenuScreen
import me.proteus.myeye.ui.screens.PlaceDetailsScreen
import me.proteus.myeye.ui.screens.ResultBrowserScreen
import me.proteus.myeye.ui.screens.SimpleDistanceScreen
import me.proteus.myeye.ui.screens.TestResultScreen
import me.proteus.myeye.ui.screens.VisionTestConfigScreen
import me.proteus.myeye.ui.screens.VisionTestScreen
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

actual fun getCurrentRoute(): String {
    return window.location.hash.removePrefix("#/")
}

actual fun navigate(route: String) {
    window.location.hash = "#/$route"
}

actual external fun goBack(route: String, inclusive: Boolean)

@Composable
actual fun SetupNavigation() {

    var currentRoute by remember {
        mutableStateOf(getCurrentRoute())
    }
    LaunchedEffect(Unit) {
        window.onhashchange = {
            println("hash change")
            val otherElements = document.body!!.childNodes
                .asList().filter { el ->
                    el.isElement && (el as HTMLElement).id != "canvas"
                }
            otherElements.forEach { el -> document.body!!.removeChild(el) }

            println(it.oldURL)
            currentRoute = window.location.hash.removePrefix("#/")
            println(currentRoute)
        }
        window.onreset = {
            println("reset")
        }
        window.onload = {
            println("loaded")
        }
    }

    val parts = currentRoute.split("/")
    when (parts.firstOrNull()) {
        "menu" -> { MenuScreen() }
        "browser" -> { ResultBrowserScreen() }
        "result" -> {
            val sessionId = parts.getOrNull(1)?.toIntOrNull() ?: -1
            val isAfter = parts.getOrNull(2)?.toBooleanStrictOrNull() ?: false
            TestResultScreen(sessionId, isAfter)
        }
        "visiontest" -> {
            val testID = parts.getOrNull(1).toString()
            val navMode = parts.getOrNull(2)?.toIntOrNull() ?: 0
            val sessionId = parts.getOrNull(3)?.toIntOrNull() ?: -1
            val distance = parts.getOrNull(4)?.toFloatOrNull() ?: 0f
            if (navMode == 0) {
                VisionTestConfigScreen(testID)
            } else {
                VisionTestScreen(testID, navMode, sessionId, distance)
            }
        }
        "distance" -> {
            val countdown = parts.getOrNull(1).toBoolean()
            val testID = parts.getOrNull(2).toString()
            SimpleDistanceScreen(countdown, testID)
        }
        "map" -> { MapScreen() }
        "place" -> {
            val placeId = parts.getOrNull(1).toString()
            PlaceDetailsScreen(placeId)
        }
        "test_selector" -> { TestSelector() }
        "article_browser" -> { ArticleBrowserScreen() }
        "article" -> {
            val id = parts.getOrNull(1)?.toIntOrNull() ?: 0
            ArticleScreen(id)
        }
    }

}

@Composable
actual fun isLandscape(): Boolean {
    return window.innerWidth > window.innerHeight
}