package me.proteus.myeye.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import me.proteus.myeye.BuildConfig
import me.proteus.myeye.ui.theme.MyEyeTheme

@Composable
fun PlaceDetailsScreen(controller: NavController, placeID: String) {

    val context = controller.context

    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)
    val placesClient = Places.createClient(context)

    MyEyeTheme {
        Scaffold { innerPadding ->

            var place by remember { mutableStateOf<Place?>(null) }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(place?.location ?: LatLng(50.0, 50.0), 20f)
            }

            LaunchedEffect(Unit) {

                getPlace(placesClient, placeID, true) {
                    place = it
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it.location!!, 18f)
                }
                println("Found place")

            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    MapView(cameraPositionState) {
                        if (place != null) {
                            PlaceMarker(controller, place!!)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {

                    if (place != null) {

                        val hours = place!!.currentOpeningHours
                        println(hours)
                        val lista = hours?.weekdayText?.joinToString("\n", "\n")

                        val numer = place!!.internationalPhoneNumber ?: "Brak informacji"

                        Text(place!!.displayName!!, fontSize = 28.sp)
                        Text(place!!.formattedAddress!!, fontSize = 20.sp)

                        Text("Godziny otwarcia: ${if (lista == null) "Brak informacji" else ""}", fontSize = 20.sp)
                        Text(lista ?: "", fontSize = 16.sp)

                        Text("Telefon: $numer", fontSize = 20.sp)

                    } else {
                        Text("Ładowanie danych...")
                    }

                }
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
fun MapView(cps: CameraPositionState, marker: @Composable () -> Unit) {
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cps,
        uiSettings = MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false
        ),
        properties = MapProperties(
            isBuildingEnabled = true,
        )

    ) {
//            Marker(
//                state = rememberUpdatedMarkerState(position = cps.position.target),
//                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
//            )
        marker()

    }
}

fun getPlace(
    client: PlacesClient,
    id: String,
    extended: Boolean,
    callback: (Place) -> Unit
) {

    val types = mutableListOf(
        Place.Field.DISPLAY_NAME,
        Place.Field.LOCATION
    )

    if (extended) {
        types.add(Place.Field.CURRENT_OPENING_HOURS)
        types.add(Place.Field.INTERNATIONAL_PHONE_NUMBER)
        types.add(Place.Field.FORMATTED_ADDRESS)
    }

    val request = FetchPlaceRequest.builder(id, types).build()

    client.fetchPlace(request)
        .addOnSuccessListener { res ->
            callback(res.place)
        }
        .addOnFailureListener { err ->
            Log.e("PlaceDetailsActivity", "getPlace: ${err.message}")
        }
}

