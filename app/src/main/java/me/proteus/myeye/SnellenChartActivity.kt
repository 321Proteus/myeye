package me.proteus.myeye

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.util.Random
import kotlin.math.*

class SnellenChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SnellenChart(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SnellenChart(modifier: Modifier = Modifier) {

    var stage: Int by remember { mutableIntStateOf(1) }
    var text: String by remember { mutableStateOf(generateText()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box (
            modifier = modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            LetterContainer(
                stage = stage,
                text = text,
                modifier = modifier
            )
        }

        ButtonRow(
            onRegenerate = { text = generateText() },
            onSizeIncrease = { if (stage > 1) { stage--; text = generateText()} },
            onSizeDecrease = { if (stage < 10) { stage++; text = generateText() } }
        )
    }
}

fun stageToCentimeters(stage: Int, distance: Float): Float {

    var marBase = ((PI/180) / 60) * 5  // 5 minut katowych
    var marCurrent = marBase * 10f.pow(-stage * 0.1f)

    var height = 2 * distance * tan(marCurrent / 2)

    return height.toFloat()

}

@Composable
fun LetterContainer(stage: Int, text: String, modifier: Modifier = Modifier) {

    val config = LocalConfiguration.current
    val opticianSansFamily = FontFamily(Font(R.font.opticiansans))

    var screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
    var calculatedSize = stageToCentimeters(stage, 100f)
    println(calculatedSize)
    var pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (char in text) {
                Text(
                    text = char.toString(),
                    color = Color.Black,
                    fontSize = pixelSize * 2,
                    fontFamily = opticianSansFamily,
                    modifier = modifier.padding(8.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (char in text) {
                Text(
                    text = char.toString(),
                    color = Color.Black,
                    fontSize = pixelSize * 2,
                    fontFamily = opticianSansFamily,
                    modifier = modifier.padding(8.dp)
                )
            }
        }
    }

}

@Composable
fun ButtonRow(
    onRegenerate: () -> Unit,
    onSizeIncrease: () -> Unit,
    onSizeDecrease: () -> Unit,
    modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { onSizeIncrease() }) {
            Text(text = "Wieksze litery")
        }
        Button(onClick = { onRegenerate() }) {
            Text(text = "Losuj litery")
        }
        Button(onClick = { onSizeDecrease() }) {
            Text(text = "Mniejsze litery")
        }
    }

}

fun generateText(): String {

    var random = Random()
    var text: String = ""
    var i: Int = 0
    while(i < 5)  {
        text += ((abs(random.nextInt() % 25)) + 65).toChar()
        i++
    }

    return text
}