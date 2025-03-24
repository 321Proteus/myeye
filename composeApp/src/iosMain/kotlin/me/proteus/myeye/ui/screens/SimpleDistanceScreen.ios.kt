package me.proteus.myeye.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import me.proteus.myeye.navigate
import swiftSrc.DistanceViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun SimpleDistanceScreen(
    countdown: Boolean,
    testID: String
) {

    val distanceController = DistanceViewController()
    var avg by remember { mutableStateOf(0f) }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (avg == 0f) {
            avg = distanceController.getDistance()
            delay(500)
        }
        println("Distance $avg")
        isReady = true
    }

    LaunchedEffect(isReady) {
        if (isReady) {
            println("navigating with $avg")
            navigate("visiontest/$testID/0/0/$avg")
        }
    }

    if (!isReady) {
        UIKitViewController(
            factory = { distanceController },
            modifier = Modifier.fillMaxSize()
        )
    }

}