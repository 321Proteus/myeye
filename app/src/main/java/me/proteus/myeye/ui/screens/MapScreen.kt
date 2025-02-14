package me.proteus.myeye.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.*
import me.proteus.myeye.BuildConfig
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.R
import me.proteus.myeye.ui.components.BottomBar
import me.proteus.myeye.ui.components.PlaceSearchBar
import kotlin.math.*

@Composable
fun MapScreen(controller: NavController) {

    val context = controller.context

    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)

    val placesClient = Places.createClient(context)
    var currentPos by remember { mutableStateOf(LatLng(52.41, 16.93)) }

    MyEyeTheme {
        Scaffold (
            topBar = {
                PlaceSearchBar(placesClient, currentPos) { newPos ->
                    currentPos = newPos
                    println(currentPos)
                }
            },
            bottomBar = { BottomBar(controller) }
        ) { innerPadding ->

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentPos, 15f)
            }

            var markerPos by remember { mutableStateOf(currentPos) }
            var places by remember { mutableStateOf(emptyList<Place>()) }

            LaunchedEffect(currentPos) {
                markerPos = currentPos
                val update = CameraUpdateFactory.newLatLng(currentPos)
                cameraPositionState.animate(update, 1000)
            }

            LaunchedEffect(markerPos) {
                search(placesClient, markerPos) { foundPlaces ->
                    Log.e("MapActivity", "onCreate: Search over")
                    Log.e("MapActivity", foundPlaces.size.toString())
                    places = foundPlaces.toList()
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
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

fun getAutocompletePlaces(
    query: String,
    client: PlacesClient,
    center: LatLng,
    callback: (List<AutocompletePrediction>) -> Unit
) {
    val autocompleteRequest = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .setCountries("PL")
        .setRegionCode("PL")
        .setTypesFilter(listOf("route"))
        .setOrigin(center)
        .setSessionToken(AutocompleteSessionToken.newInstance())
        .build()

    client.findAutocompletePredictions(autocompleteRequest)
        .addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            callback(predictions)
        }
        .addOnFailureListener { exception ->
            Log.e("Map", "Error: ${exception.message}")
        }

}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, update: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let {
            println(LatLng(it.latitude, it.longitude))
            update(LatLng(it.latitude, it.longitude))
        }
    }
}

fun search(
    client: PlacesClient,
    center: LatLng,
    callback: (List<Place>) -> Unit) {

    val bounds = getRectangularBounds(center, 1.0)

    val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)

    val request =
        SearchByTextRequest.builder("okulistyczny", placeFields)
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
