package me.proteus.myeye.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MapScreen(controller: NavController) {

    val context = controller.context

    Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)

    val placesClient = Places.createClient(context)
    var currentPos by remember { mutableStateOf(LatLng(52.41, 16.93)) }

    MyEyeTheme {
        Scaffold (
            topBar = {
//                Box(
//                    modifier = Modifier.padding(8.dp),
//                    contentAlignment = Alignment.TopCenter
//                ) {
                    PlaceSearchBar(placesClient, currentPos) { newPos ->
                        currentPos = newPos
                        println(currentPos)
                    }
//                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchBar(
    placesClient: PlacesClient,
    position: LatLng,
    updatePosition: (LatLng) -> Unit
) {

    var currentText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .then(if (isExpanded) Modifier else Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
//                .height(48.dp)
            )
            .fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = currentText,
                onSearch = { isExpanded = false },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                placeholder = { Text("Wyszukaj ulicę lub miejsce") },
                leadingIcon = {
                    Icon(
                        if (isExpanded) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = null,
                        Modifier.clickable { isExpanded = !isExpanded }
                    )
                },
                trailingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                onQueryChange = { currentText = it }
            )
        },
        expanded = isExpanded,
        onExpandedChange = { println("changed expanded") }
    ) {

        var preds by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
        var lastPlaceIDs by remember { mutableStateOf(MutableList(5) { "" }) }
        var places by remember { mutableStateOf(MutableList<Place?>(5) { null }) }

        LaunchedEffect(currentText) {
            getAutocompletePlaces(currentText, placesClient, position) { predicted ->
                preds = predicted.sortedBy { it.distanceMeters }
            }
            if (preds.isNotEmpty()) {
                println("size ${preds.size}")
                for (i in preds.indices) {
                    if (preds[i].placeId != lastPlaceIDs[i]) {
                        getPlace(placesClient, preds[i].placeId, false) {
                                found -> places[i] = found
                            println("requested for place at index $i")
                            lastPlaceIDs[i] = preds[i].placeId
                        }
                    }
                }
            }

        }

        LazyColumn {
            itemsIndexed(places.filter { it != null }) { index, place ->
                println(places.filter { it != null })
//                distanceText = item.distanceMeters?.toString() + " m"
                val primaryText = preds[index].getFullText(null).toString()
                val distance = getDistance(place?.location!!, position)

                ListItem(
                    headlineContent = { Text(primaryText) },
                    supportingContent = { Text("${round(distance * 10) / 10} km") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier =
                    Modifier.clickable {
                        currentText = primaryText
                        updatePosition(place.location!!)
                        isExpanded = false
                    }
                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
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
        .setOrigin(center)
        .setSessionToken(AutocompleteSessionToken.newInstance())
        .build()

    client.findAutocompletePredictions(autocompleteRequest)
        .addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            callback(predictions)
        }
        .addOnFailureListener { exception ->
//            if (exception is PlacesException) {
//            }
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
