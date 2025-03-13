package me.proteus.myeye.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import kotlinx.cinterop.ExperimentalForeignApi
import swiftSrc.MapsViewController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.UIKitViewController
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import kotlin.native.concurrent.ThreadLocal


@Composable
actual fun PlaceDetailsScreen(placeId: String) {

}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapScreen() {

    val data by remember { mutableStateOf("") }

    val viewController = remember {
        MapsViewController()
    }

    println(data)

    MyEyeTheme {
        Scaffold(bottomBar = { BottomBar() }) {
            UIKitViewController(
                factory = { viewController },
                modifier = Modifier
                    .fillMaxSize(),
                properties = UIKitInteropProperties(
                    isInteractive = true,
                    isNativeAccessibilityEnabled = true,
                ),
                update = { println("update") },
                onReset = { println("reset") },
                onRelease = { println("rel") }
            )
        }
    }

//    MyEyeTheme {
//        Scaffold (
//            topBar = {
//                Column {
////                    PlaceSearchBar(client, currentPos) { newPos ->
////                        currentPos = newPos
////                        println(currentPos)
////                    }
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color.LightGray),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        Text(stringResource(Res.string.map_distance))
//
//                        Slider(
//                            modifier = Modifier.fillMaxWidth(0.5f),
//                            value = distance.toFloat(),
//                            steps = 7,
//                            onValueChange = { distance = it.toDouble() },
//                            onValueChangeFinished = {
//                                search(client, markerPos, distance) { foundPlaces ->
//                                    println(distance)
//                                    places = foundPlaces.toList()
//                                }
//                            },
//                            valueRange = 0.5f..6f,
//                            thumb = {
//                                val colors = MaterialTheme.colorScheme
//                                Canvas(modifier = Modifier.size(24.dp)) {
//                                    drawCircle(
//                                        color = colors.primary,
//                                        radius = 12.dp.toPx()
//                                    )
//                                }
//                            },
//                            track = { slider ->
//                                val colors = MaterialTheme.colorScheme
//                                Canvas(modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(4.dp)) {
//                                    drawRoundRect(
//                                        color = colors.surfaceVariant,
//                                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
//                                    )
//                                    val activeWidth = slider.value / slider.valueRange.endInclusive * size.width
//                                    drawRoundRect(
//                                        color = colors.primary,
//                                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
//                                        size = androidx.compose.ui.geometry.Size(activeWidth, size.height)
//                                    )
//                                }
//                            }
//                        )
//                    }
//                }
//            },
//            bottomBar = { BottomBar(controller) }
//        ) { innerPadding ->

//            LaunchedEffect(currentPos) {
//                markerPos = currentPos
//                val update = CameraUpdateFactory.newLatLng(currentPos)
//                cameraPositionState.animate(update, 1000)
//            }
//
//            LaunchedEffect(markerPos) {
//                search(client, markerPos, distance) { foundPlaces ->
//                    println(distance)
//                    println("Search over with ${foundPlaces.size} places")
//                    places = foundPlaces.toList()
//                }
//                val update = CameraUpdateFactory.newLatLng(markerPos)
//                cameraPositionState.animate(update, 250)
//            }

//            GoogleMap(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(bottom = innerPadding.calculateBottomPadding()),
//                cameraPositionState = cameraPositionState,
//                onMapClick = { markerPos = it },
//                onPOIClick = { poi ->
//                    println("Clicked on ${poi.name}")
//                }
//            ) {
//                Marker (
//                    state = rememberUpdatedMarkerState(position = markerPos)
//                )
//
//                places.forEach { place -> PlaceMarker(controller, place) }
//                RangeDisplay(markerPos, distance)
//
//            }
//        }
//    }

//    DisposableEffect(Unit) {
//        onDispose {
//            client.finalize()
//        }
//    }

}