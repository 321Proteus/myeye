package me.proteus.myeye.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import me.proteus.myeye.navigate
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.PlaceSearchBar
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.map_prompt
import me.proteus.myeye.resources.result_distance
import me.proteus.myeye.resources.myeye_logo
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

const val MAPS_API_KEY = "AIzaSyD33Ph-HlL8gtc08VZ4d07LH6dPlp71v3M"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MapScreen() {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]

    Places.initializeWithNewPlacesApiEnabled(context, MAPS_API_KEY, locale)
    Places.initialize(context, MAPS_API_KEY, locale)

    val placesClient = Places.createClient(context)
    var currentPos by remember { mutableStateOf(LatLng(52.41, 16.93)) }
    var distance by remember { mutableDoubleStateOf(1.0) }

    var markerPos by remember { mutableStateOf(currentPos) }
    var places by remember { mutableStateOf(emptyList<Place>()) }

    val localizedQuery = Res.string.map_prompt.res()

    MyEyeTheme {
        Scaffold (
            topBar = {
                Column {
                    PlaceSearchBar(placesClient, currentPos) { newPos ->
                        currentPos = newPos
                        println(currentPos)
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(stringResource(Res.string.result_distance))

                        Slider(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            value = distance.toFloat(),
                            steps = 7,
                            onValueChange = { distance = it.toDouble() },
                            onValueChangeFinished = {
                                search(placesClient, markerPos, distance, localizedQuery) { foundPlaces ->
                                    println(distance)
                                    Log.e("Map", "Search over with ${foundPlaces.size} places")
                                    places = foundPlaces.toList()
                                }
                            },
                            valueRange = 0.5f..6f,
                            thumb = {
                                val colors = MaterialTheme.colorScheme
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    drawCircle(
                                        color = colors.primary,
                                        radius = 12.dp.toPx()
                                    )
                                }
                            },
                            track = { slider ->
                                val colors = MaterialTheme.colorScheme
                                Canvas(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)) {
                                    drawRoundRect(
                                        color = colors.surfaceVariant,
                                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                    )
                                    val activeWidth = slider.value / slider.valueRange.endInclusive * size.width
                                    drawRoundRect(
                                        color = colors.primary,
                                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                        size = androidx.compose.ui.geometry.Size(activeWidth, size.height)
                                    )
                                }
                            }
                        )
                    }
                }
            },
            bottomBar = { BottomBar() }
        ) { innerPadding ->

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentPos, 15f)
            }

            LaunchedEffect(currentPos) {
                markerPos = currentPos
                val update = CameraUpdateFactory.newLatLng(currentPos)
                cameraPositionState.animate(update, 1000)
            }

            LaunchedEffect(markerPos) {
                search(placesClient, markerPos, distance, localizedQuery) { foundPlaces ->
                    println(distance)
                    Log.d("Map", "Search over with ${foundPlaces.size} places")
                    places = foundPlaces.toList()
                }
                val update = CameraUpdateFactory.newLatLng(markerPos)
                cameraPositionState.animate(update, 250)
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                cameraPositionState = cameraPositionState,
                onMapClick = { markerPos = it },
                onPOIClick = { poi ->
                    println("Clicked on ${poi.name}")
                }
            ) {
                Marker(
                    state = rememberUpdatedMarkerState(position = markerPos)
                )

                places.forEach { place -> PlaceMarker(place) }
                RangeDisplay(markerPos, distance)

            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("Map", "Disposed Places")
            Places.deinitialize()
        }
    }

}

@Composable
fun PlaceMarker(place: Place) {
    if (place.location != null) {
        MarkerComposable(
            state = rememberUpdatedMarkerState(position = place.location!!),
            title = place.displayName,
            onClick = {
                navigate("place/${place.id}")
                false
            }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(36.dp))
                    .size(36.dp)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.myeye_logo),
                    contentDescription = null, tint = Color.White,
                    modifier = Modifier.scale(0.7f)
                )
            }
        }

    }
}

@Composable
private fun RangeDisplay(position: LatLng, distance: Double) {
    val bounds = getRectangularBounds(position, distance)
    val sw = bounds.southwest
    val ne = bounds.northeast
    val nw = LatLng(ne.latitude, sw.longitude)
    val se = LatLng(sw.latitude, ne.longitude)


    Polygon(
        points = listOf(sw, nw, ne, se, sw),
        strokeColor = Color.Blue,
        strokeWidth = 5f,
        fillColor = Color(0x5500FF00)
    )
}

fun search(
    client: PlacesClient,
    center: LatLng,
    distance: Double,
    prompt: String,
    callback: (List<Place>) -> Unit) {

    val bounds = getRectangularBounds(center, distance)

    val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)

    val request =
        SearchByTextRequest.builder(prompt, placeFields)
            .setMaxResultCount(10)
            .setLocationRestriction(bounds)
            .setPlaceFields(listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME, Place.Field.ID))
            .build()

    println("Started search")

    client.searchByText(request)
        .addOnSuccessListener { res ->
            callback(res.places)
        }
        .addOnFailureListener { fall ->
            Log.e("MapActivity", "search: ${fall.message}")
        }
}

fun getRectangularBounds(center: LatLng, distance: Double): RectangularBounds {
    val kilometr = 111.32

    val latOffset = distance / 2 / kilometr
    val lngOffset = distance / 2 / (kilometr * cos(Math.toRadians(center.latitude)))

    val southwest = LatLng(center.latitude - latOffset, center.longitude - lngOffset)
    val northeast = LatLng(center.latitude + latOffset, center.longitude + lngOffset)

    return RectangularBounds.newInstance(southwest, northeast)
}

fun getDistance(latLng1: LatLng, latLng2: LatLng): Double {
    val promienZiemi = 6371

    val lat1 = Math.toRadians(latLng1.latitude)
    val lon1 = Math.toRadians(latLng1.longitude)
    val lat2 = Math.toRadians(latLng2.latitude)
    val lon2 = Math.toRadians(latLng2.longitude)

    val dlat = lat2 - lat1
    val dlon = lon2 - lon1

    val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return promienZiemi * c
}
