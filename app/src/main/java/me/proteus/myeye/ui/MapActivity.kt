package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import me.proteus.myeye.BuildConfig
import me.proteus.myeye.ui.theme.MyEyeTheme
import kotlin.math.cos

class MapActivity : ComponentActivity() {

    private lateinit var placesClient: PlacesClient
    private var uniquePlaces = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.MAPS_API_KEY)

        placesClient = Places.createClient(this)
        val startPos = LatLng(52.41, 16.93)

        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                Scaffold { innerPadding ->

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(startPos, 15f)
                    }

                    var markerPos by remember { mutableStateOf(startPos) }
                    var places by remember { mutableStateOf(emptyList<Place>()) }

                    LaunchedEffect(markerPos) {
                        search(markerPos) { foundPlaces ->
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
                        Marker(
                            state = rememberUpdatedMarkerState(position = markerPos),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                        )

                        places.forEach { place ->
                            if (!uniquePlaces.contains(place)) {
                                uniquePlaces.add(place)
                                println("Nowe miejsce: ${place.displayName}")
                            }
                            place.location?.let {
                                Marker(
                                    state = rememberUpdatedMarkerState(position = it),
                                    icon = BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_YELLOW),
                                    title = place.displayName,
                                    onClick = {
                                        val intent = Intent(this, PlaceDetailsActivity::class.java)
                                        intent.putExtra("PLACE_ID", place.id)
                                        startActivity(intent)
                                        false
                                    }
                                )
                            }
                        }

                        val bounds = getRectangularBounds(markerPos, 1.0)
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
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Places.deinitialize()
    }

    private fun search(center: LatLng, callback: (List<Place>) -> Unit) {

        val bounds = getRectangularBounds(center, 1.0)

        val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)

        val request =
            SearchByTextRequest.builder("Gabinet okulistyczny", placeFields)
//                .setMaxResultCount(10)
                .setLocationRestriction(bounds)
                .setPlaceFields(listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME, Place.Field.ID))
                .build()

        println("Started search")

        placesClient.searchByText(request)
            .addOnSuccessListener { res ->
                callback(res.places)
//                Log.e("MapActivity", "search: $res", )
//                val places = res.places
//                for (place in places) println(place.displayName)
//                foundPlaces = places
            }
            .addOnFailureListener { fall ->
                Log.e("MapActivity", "search: ${fall.message}")
            }
    }

    private fun getRectangularBounds(center: LatLng, distance: Double): RectangularBounds {
        val kilometr = 111.32

        val latOffset = distance / 2 / kilometr
        val lngOffset = distance / 2 / (kilometr * cos(Math.toRadians(center.latitude)))

        val southwest = LatLng(center.latitude - latOffset, center.longitude - lngOffset)
        val northeast = LatLng(center.latitude + latOffset, center.longitude + lngOffset)

        return RectangularBounds.newInstance(southwest, northeast)
    }

}
