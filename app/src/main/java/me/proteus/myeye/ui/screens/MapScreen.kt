package me.proteus.myeye.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import me.proteus.myeye.BuildConfig
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.R
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.TopBar
import kotlin.math.cos

@Composable
fun MapScreen(controller: NavController) {

    val context = controller.context

    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)

    val placesClient = Places.createClient(context)
    val startPos = LatLng(52.41, 16.93)

    MyEyeTheme {
        Scaffold (
            topBar = { TopBar() },
            bottomBar = { BottomBar(controller) }
        ) { innerPadding ->

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(startPos, 15f)
            }

            var markerPos by remember { mutableStateOf(startPos) }
            var places by remember { mutableStateOf(emptyList<Place>()) }

            LaunchedEffect(markerPos) {
                search(placesClient, markerPos) { foundPlaces ->
                    Log.e("MapActivity", "onCreate: Search over")
                    Log.e("MapActivity", foundPlaces.size.toString())
                    places = foundPlaces.toList()
                }
            }

            GoogleMap(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { markerPos = it }
            ) {
                Marker (
                    state = rememberUpdatedMarkerState(position = markerPos)
                )

                places.forEach { place -> PlaceMarker(controller, place) }
                RangeDisplay(markerPos)

            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Places.deinitialize()
        }
    }

}


@Composable
fun PlaceMarker(controller: NavController, place: Place) {
    if (place.location != null) {
        MarkerComposable(
            state = rememberUpdatedMarkerState(position = place.location!!),
            title = place.displayName,
            onClick = {
                controller.navigate("place/${place.id}")
                false
            }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(48.dp))
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.myeye_logo_white_playstore), null)
            }
        }

    }
}

//        val shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp)
//        val painter = rememberAsyncImagePainter(
//            ImageRequest.Builder(LocalContext.current)
//                .data(imageUrl)
//                .allowHardware(false)
//                .build()
//        )
//
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(shape)
//                    .background(Color.LightGray)
//                    .padding(4.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                if (!imageUrl.isNullOrEmpty()) {
//                    Image(
//                        painter = painter,
//                        contentDescription = "Profile Image",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Text(
//                        text = fullName.take(1).uppercase(),
//                        color = Color.White,
//                        style = MaterialTheme.typography.body2,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//            }
//        }
//    }

@Composable
private fun RangeDisplay(position: LatLng) {
    val bounds = getRectangularBounds(position, 1.0)
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
    callback: (List<Place>) -> Unit) {

    val bounds = getRectangularBounds(center, 1.0)

    val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)

    val request =
        SearchByTextRequest.builder("okulistyczny", placeFields)
//                .setMaxResultCount(10)
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

