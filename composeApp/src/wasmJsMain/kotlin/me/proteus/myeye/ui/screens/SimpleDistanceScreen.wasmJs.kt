package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable
import me.proteus.myeye.navigate

@Composable
actual fun SimpleDistanceScreen(
    countdown: Boolean,
    testID: String
) {
    println(testID)
    navigate("visiontest/$testID/1/0/0f")
}

