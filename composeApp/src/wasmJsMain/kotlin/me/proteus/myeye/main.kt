package me.proteus.myeye

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.categories
import me.proteus.myeye.resources.descriptions
import me.proteus.myeye.resources.farnsworth_colors
import me.proteus.myeye.resources.name
import me.proteus.myeye.resources.phonetic
import me.proteus.myeye.resources.test_ids
import org.jetbrains.compose.resources.stringArrayResource
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        stringArrayResource(Res.array.farnsworth_colors)
        stringArrayResource(Res.array.test_ids)
        stringArrayResource(Res.array.categories)
        stringArrayResource(Res.array.name)
        stringArrayResource(Res.array.phonetic)
        stringArrayResource(Res.array.descriptions)
        App()
    }

    val composeCanvas = document.getElementsByTagName("canvas")[0] as HTMLElement
    composeCanvas.id = "canvas"
    composeCanvas.style.position = "fixed"
    composeCanvas.style.top = "0px"

}