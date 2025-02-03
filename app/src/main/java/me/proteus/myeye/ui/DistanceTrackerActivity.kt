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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.proteus.myeye.ui.theme.MyEyeTheme
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class DistanceTrackerActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var oldAccTime = 0L
    private var oldGyroTime = 0L

    private var acceleration = mutableStateListOf(0f, 0f, 0f)
    private var rotation = mutableStateListOf(0f, 0f, 0f)
    private var velocity = mutableStateListOf(0f, 0f, 0f)
    private var position = mutableStateListOf(0f, 0f, 0f)

    private var daneX = mutableStateListOf<Float>()
    private var daneY = mutableStateListOf<Float>()
    private var daneZ = mutableStateListOf<Float>()

    private var totalDistance = 0f

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
                .border(
                    width = 2.dp,
                    brush = SolidColor(Color.Black),
                    shape = RoundedCornerShape(8.dp)
                )
                .zIndex(2f)
        ) {
            Column {
                Row(
                    modifier = Modifier.weight(0.6f).fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Przyspieszenia")
                        for (el in acceleration) Text(el.toString())
                        Text(getTotalAcceleration().toString())
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Rotacje")
                        for (el in rotation) Text(el.times(180).div(PI).toString())
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Prędkości")
                        for (el in velocity) Text(el.toString())
                    }
                }
                Box(
                    modifier = Modifier.weight(0.2f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(if (getTotalAcceleration() <= 0.01f) "STOP" else "RUCH")
                        Text(totalDistance.times(100).toString() + " cm")
                    }
                }
                Box(
                    modifier = Modifier.weight(0.4f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    val daneSuma = mutableListOf<Float>()
                    for (i in 0..<daneX.size) {
                        daneSuma.add(sqrt(daneX[i].pow(2) + daneY[i].pow(2) + daneZ[i].pow(2)))
                    }

                    val dataSeries = listOf(daneX, daneY, daneZ, daneSuma)
                    LineChart(
                        dane = dataSeries,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }
    }

    private val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 24f
        color = Color.Black.toArgb()
    }

    @Composable
    fun LineChart(dane: List<List<Float>>, modifier: Modifier) {

        var scale by remember { mutableFloatStateOf(1f) }

        val maksi: Float = scale
        val mini: Float = -scale

        Column(modifier = modifier) {
            Canvas(
                modifier = modifier
                    .background(Color.LightGray)
                    .weight(0.9f)
            ) {

                val width = size.width
                val height = size.height

                var colorIterator = 0

                for (axis in dane) {
                    if (axis.isEmpty()) continue

                    val odleglosc = width / (axis.size - 1).coerceAtLeast(1)

                    val path = Path().apply {
                        axis.forEachIndexed { index, value ->

                            val x = index * odleglosc
                            val y = height - ((value - mini) / (maksi - mini) * height)

                            if (index == 0) moveTo(x, y)
                            else lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = when (colorIterator) {
                            0 -> Color.Red
                            1 -> Color.Green
                            2 -> Color.Blue
                            else -> Color.Black
                        },
                        style = Stroke(width = 4f)
                    )

                    colorIterator++
                }

                drawContext.canvas.nativeCanvas.drawText(maksi.toString(), 0f, 0f, paint)
                drawContext.canvas.nativeCanvas.drawText(mini.toString(), 0f, height, paint)

            }
            Slider(
                modifier = Modifier.weight(0.1f),
                value = scale,
                onValueChange = { scale = it },
                valueRange = 0.01f..10f
            )
        }

    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null) {
            Toast.makeText(this, "Brak akcelerometru", Toast.LENGTH_SHORT).show()
        }
        if (gyroscope == null) {
            Toast.makeText(this, "Brak żyroskopu", Toast.LENGTH_SHORT).show()
        }

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)

        println(accelerometer?.name)
        println(gyroscope?.name)
    }

    override fun onSensorChanged(e: SensorEvent?) {
        if (e != null) {
            when (e.sensor.type) {
                Sensor.TYPE_LINEAR_ACCELERATION -> {

                    val current = e.timestamp

                    val dt = if (oldAccTime != 0L) (current - oldAccTime) * 10f.pow(-9) else 0f
                    oldAccTime = current

                    var sumDistance = 0f

                    for (i in 0..2) {

                        val isStationary: Boolean = e.values[i] > 0.01f

                        acceleration[i] = if (isStationary) e.values[i] else 0f
                        position[i] += dt * (velocity[i] + acceleration[i] * dt / 2)
                        velocity[i] += if (isStationary) dt * acceleration[i] else -velocity[i]
                        sumDistance += position[i] * position[i]

                    }

                    totalDistance = sqrt(sumDistance)

                    daneX.add(acceleration[0])
                    daneY.add(acceleration[1])
                    daneZ.add(acceleration[2])
                    if (daneX.size > 250) {
                        daneX.removeAt(0)
                        daneY.removeAt(0)
                        daneZ.removeAt(0)
                    }

                }

                Sensor.TYPE_GYROSCOPE -> {

                    val current = e.timestamp
                    
                    val dt = if (oldGyroTime != 0L) (current - oldGyroTime) * 10f.pow(-9) else 0f
                    oldGyroTime = current
                    
                    for (i in 0..2) rotation[i] += e.values[i] * dt

                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}


    private fun getTotalAcceleration(): Float {
        return sqrt(acceleration[0].pow(2) + acceleration[1].pow(2) + acceleration[2].pow(2))
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}