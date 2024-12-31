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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.proteus.myeye.ui.theme.MyEyeTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class DistanceTrackerActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    var dta = 0L
    var dtg = 0L
    var dtm = 0L

    var lastAcceleration = mutableStateListOf<Float>(0f, 0f, 0f)
    var lastOrientation = mutableStateListOf<Float>(0f, 0f, 0f)
    var rotation = mutableStateListOf<Float>(0f, 0f, 0f)
    var velocity = mutableStateListOf<Float>(0f, 0f, 0f)
    var position = mutableStateListOf<Float>(0f, 0f, 0f)
    var gravity = mutableStateListOf<Float>(0f, 0f, 0f)

    var daneX = mutableStateListOf<Float>()
    var daneY = mutableStateListOf<Float>()
    var daneZ = mutableStateListOf<Float>()

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
                        for (el in lastAcceleration) Text(el.toString())
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Rotacje")
                        for (el in rotation) Text(el.times(180).div(PI).toString())
                        // rotation[2] - os NESW, ona nas interesuje
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
                    var maxCurrent: Boolean = true
                    if (lastAcceleration[0] + lastAcceleration[1] + lastAcceleration[2] == 0f) {
                        maxCurrent = false
                    }
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()) {
                        var mapped  = lastAcceleration.map { abs(it) }
                        Text((88 + mapped.indexOf(mapped.max())).toChar().toString())
                        Text(if (velocity.sum() == 0f) "STOP" else "RUCH")
                        Text(totalDistance.times(100).toString() + " cm")
                    }
                }
                Box(
                    modifier = Modifier.weight(0.4f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LineChart(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }
    }

    @Composable
    fun LineChart(modifier: Modifier) {

        var daneSuma = mutableListOf<Float>()
        for (i in 0..<daneX.size) {
            daneSuma.add(daneX[i].pow(2) + daneY[i].pow(2) + daneZ[i].pow(2))
        }

        var dataSeries = listOf(daneX, daneY, daneZ, daneSuma)

        var mini: Float = 0f
        var maksi: Float = 0f

        for (seria in dataSeries) {
            if (seria.isEmpty()) continue
            maksi = max(maksi, seria.max())
            mini = min(mini, seria.min())
        }

        Column {
            Canvas(modifier = modifier.background(Color.LightGray)) {

                val width = size.width
                val height = size.height

                var colorIterator = 0

                for (axis in dataSeries) {
                    if (axis.isEmpty()) continue

                    val odleglosc = width / (axis.size - 1).coerceAtLeast(1)

                    val path = Path().apply {
                        axis.forEachIndexed { index, value ->

                            val x = index * odleglosc
                            val y = height - ((value - mini) / (maksi - mini) * height).toFloat()

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
            }
        }

    }

    fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        var accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        var gravitymeter = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        var gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        var magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null) {
            Toast.makeText(this, "Brak akcelerometru", Toast.LENGTH_SHORT).show()
        }
        if (gyroscope == null) {
            Toast.makeText(this, "Brak żyroskopu", Toast.LENGTH_SHORT).show()
        }
        if (magnetometer == null) {
            Toast.makeText(this, "Brak magnetometru", Toast.LENGTH_SHORT).show()
        }

        sensorManager.registerListener(this, gravitymeter, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)

        println(accelerometer?.name)
        println(gyroscope?.name)
        println(magnetometer?.name)
    }

    override fun onSensorChanged(e: SensorEvent?) {
        if (e != null) {
            when (e.sensor.type) {
                Sensor.TYPE_LINEAR_ACCELERATION -> {
//                    if (dta == 0L) firstd = e.timestamp
                    var delta = if (dta != 0L) (e.timestamp - dta) * 10f.pow(-9) else 0f

                    for (i in 0..2) lastAcceleration[i] = e.values[i]

                    daneX.add(lastAcceleration[0])
                    daneY.add(lastAcceleration[1])
                    daneZ.add(lastAcceleration[2])
                    if (daneX.size > 250) {
                        daneX.removeAt(0)
                        daneY.removeAt(0)
                        daneZ.removeAt(0)
                    }

                    updateDeadReckoning(delta)
                    dta = e.timestamp
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {

//                    var delta = if (dtm != 0L) (e.timestamp - dtm) * 10f.pow(-9) else 0f
//
//                    val obroty = FloatArray(9)
//                    val nachylenie = FloatArray(9)
//                    val remap = FloatArray(9)
//
//                    SensorManager.getRotationMatrix(obroty, nachylenie, gravity.toFloatArray(), e.values)
//                    SensorManager.remapCoordinateSystem(obroty, SensorManager.AXIS_X, SensorManager.AXIS_Z, remap)
//
//                    val orientation = FloatArray(3)
//                    SensorManager.getOrientation(remap, orientation)
//
//                    val azymut = orientation[0]
//
//                    totalDistance -= azymut * delta
//                    dtm = e.timestamp
                    
            }

                Sensor.TYPE_GYROSCOPE -> {
                    var delta = if (dtg != 0L) (e.timestamp - dtg) * 10f.pow(-9) else 0f


                    for (i in 0..2) lastOrientation[i] = e.values[i]
                   // System.arraycopy(e.values, 0, rotation, 0, e.values.size)
                    updateRotation(delta)
                    dtg = e.timestamp
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    fun updateRotation(time: Float) {
        for (i in 0..2) {
            rotation[i] += lastOrientation[i] * time
        }
    }

    fun updateDeadReckoning(time: Float) {
        for (i in 0..2) {

//            var acc = if(abs(lastAcceleration[i]) < 0.1) 0f else lastAcceleration[i]
//            var spd = if (abs(lastAcceleration[i]) < 0.1) 0f else velocity[i]

//            if (abs(lastAcceleration[i]) < 0.01) {
//                lastAcceleration[i] = 0f
//                velocity[i] = 0f
//            }
            velocity[i] += lastAcceleration[i] * time
            position[i] += velocity[i] * time

        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}