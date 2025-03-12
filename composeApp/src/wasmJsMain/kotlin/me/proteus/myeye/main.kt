package me.proteus.myeye

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.farnsworth_colors
import org.jetbrains.compose.resources.stringArrayResource
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        stringArrayResource(Res.array.farnsworth_colors)
        App()
    }

    val composeCanvas = document.getElementsByTagName("canvas")[0] as HTMLElement
    composeCanvas.id = "canvas"
    composeCanvas.style.position = "fixed"
    composeCanvas.style.top = "0px"

}