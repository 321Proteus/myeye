package me.proteus.myeye.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import me.proteus.myeye.ui.screens.getAutocompletePlaces
import me.proteus.myeye.ui.screens.getCurrentLocation
import me.proteus.myeye.ui.screens.getDistance
import me.proteus.myeye.ui.screens.getPlace
import kotlin.math.round

data class PlaceField(
    val name: String,
    val title: String,
    val placeId: String,
    val pos: LatLng
)

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchBar(
    placesClient: PlacesClient,
    position: LatLng,
    updatePosition: (LatLng) -> Unit
) {

    var currentText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    val rpl = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            getCurrentLocation(context) { loc ->
                updatePosition(loc)
            }
        }
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    SearchBar(
        modifier = Modifier
            .then(
                if (isExpanded) Modifier else Modifier
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
                trailingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        Modifier.clickable {
                            if (hasPermission) {
                                getCurrentLocation(context) { loc ->
                                    updatePosition(loc)
                                }
                            } else {
                                rpl.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    )
                },
                onQueryChange = { currentText = it }
            )
        },
        expanded = isExpanded,
        onExpandedChange = { println("changed expanded") }
    ) {

        var preds by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
        val places by remember { mutableStateOf(MutableList<PlaceField?>(5) { null }) }
        val placesCache by remember { mutableStateOf(MutableList<PlaceField?>(1) { null }) }

        LaunchedEffect(currentText) {
            getAutocompletePlaces(currentText, placesClient, position) { predicted ->
                preds = predicted.sortedBy { it.distanceMeters }
                println("size ${preds.size}")
                println("cache size ${placesCache.size}")

                if (preds.isNotEmpty()) {
                    for (i in preds.indices) {

                        val j = placesCache.indexOfFirst { it?.placeId == preds[i].placeId }

                        if (j == -1) {
                            if (currentText.length < 2) return@getAutocompletePlaces

                            lateinit var place: PlaceField
                            Log.d("MapScreen", "requesting for id " +
                                    preds[i].placeId.substring(0, 10) + "..."
                            )

                            getPlace(placesClient, preds[i].placeId, false) {
                                place = PlaceField(
                                    title = preds[i].getFullText(null).toString(),
                                    name = preds[i].toPlaceDetails().primaryText.toString(),
                                    pos = it.location ?: LatLng(0.0, 0.0),
                                    placeId = preds[i].placeId,
                                )
                                println(preds[i].types)
                                placesCache.add(place)
                                places[i] = place
                            }
                        } else {
                            places[i] = placesCache[j]
                        }
                    }
                } else {
                    println("empty preds")
                }
            }

        }

        if (places.isNotEmpty()) {
            LazyColumn {
                val najblizsze = places
                    .filterNotNull()
                    .sortedBy { getDistance(it.pos, position) }
                items(najblizsze) { place ->
//                    println("lista ${places.filterNotNull().size}")
//                    println("last ${preds.size}")
//                    println("i ${preds[index].toPlaceDetails().primaryText}")
//                distanceText = item.distanceMeters?.toString() + " m"
//                    val primaryText = preds[index].getFullText(null).toString()
                    val distance = getDistance(place.pos, position)
                    ListItem(
                        headlineContent = { Text(place.title) },
                        supportingContent = { Text("${round(distance * 10) / 10} km") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier =
                        Modifier
                            .clickable {
                                println("selected")
                                currentText = place.name
                                updatePosition(place.pos)
                                isExpanded = false
                            }
                            .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}