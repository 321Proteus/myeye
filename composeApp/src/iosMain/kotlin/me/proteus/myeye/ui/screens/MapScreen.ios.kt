package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import kotlinx.cinterop.ExperimentalForeignApi
import swiftSrc.MapsViewController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.coroutines.delay
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import kotlin.native.concurrent.ThreadLocal


@Composable
actual fun PlaceDetailsScreen(placeId: String) {

}


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapScreen() {

    var isPlace by remember { mutableStateOf(false) }

    val viewController = remember { MapsViewController() }

    LaunchedEffect(Unit) {
        while (true) {
            isPlace = viewController.getDetailStatus()
            delay(500)
        }
    }

    MyEyeTheme {
        Scaffold(
            topBar = {
                TopBar(isReturning = isPlace) {
                    viewController.updateView()
                }
            },
            bottomBar = { BottomBar() }
        ) {
            UIKitViewController(
                factory = { viewController },
                modifier = Modifier
                    .fillMaxSize(),
                properties = UIKitInteropProperties(
                    isInteractive = true,
                    isNativeAccessibilityEnabled = true,
                )
            )
        }
    }

}