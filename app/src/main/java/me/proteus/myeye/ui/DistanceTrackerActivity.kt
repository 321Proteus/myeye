package me.proteus.myeye.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.proteus.myeye.ui.theme.MyEyeTheme

class DistanceTrackerActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var coordinates = mutableStateListOf(0f, 0f, 0f)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyEyeTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Pomiar Dystansu") },
                            actions = { }
                        )

                    },
                    content = { innerPadding ->

                        val localWidth = LocalConfiguration.current.screenWidthDp.dp

                        var isExpanded by remember { mutableStateOf(false) }

                        val size by animateDpAsState(
                            targetValue = if (isExpanded) localWidth * 0.85f else 50.dp,
                            animationSpec = tween(durationMillis = 1000), label = ""
                        )

                        val buttonSize by animateDpAsState(
                            targetValue = if (isExpanded) 0.dp else localWidth * 0.35f,
                            animationSpec = tween(500), label = ""
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            ButtonBox(buttonSize) {
                                isExpanded = !isExpanded
                                initSensor()
                            }
                            CoordinateBox(size)
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun ButtonBox(size: Dp, action: () -> Unit) {
        Button(
            modifier = Modifier
                .padding(24.dp)
                .size(size)
                .aspectRatio(1f)
                .zIndex(3f),
            colors = ButtonColors(Color.Red, Color.White, Color.DarkGray, Color.Gray),
            shape = CircleShape,
            onClick = action,
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "GO",
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun CoordinateBox(size: Dp) {
        Box(
            modifier = Modifier
                .size(size)
                .border(width = 2.dp, brush = SolidColor(Color.Black), shape = RoundedCornerShape(8.dp))
                .zIndex(2f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    fontSize = 24.sp,
                    text = "X: " + coordinates[0].toString()
                )
                Text(
                    fontSize = 24.sp,
                    text = "Y: " + coordinates[1].toString()
                )
                Text(
                    fontSize = 24.sp,
                    text = "Z: " + coordinates[2].toString()
                )

            }
        }
    }

    fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "Brak akceleromteru", Toast.LENGTH_SHORT).show()
        }

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        println(accelerometer?.name)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        for (i in 0..<event?.values!!.size) {
            coordinates[i] = event.values[i]
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}