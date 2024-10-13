package me.proteus.myeye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
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
    var text: String by remember { mutableStateOf(generateText(stage)) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box (
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            LetterRow(
                stage = stage,
                text = text,
                modifier = modifier
            )
        }

        ButtonRow(
            onRegenerate = { text = generateText(stage) },
            onSizeIncrease = { if (stage > 1) { stage--; text = generateText(stage)} },
            onSizeDecrease = { if (stage < 10) { stage++; text = generateText(stage) } }
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
fun LetterRow(stage: Int, text: String, modifier: Modifier = Modifier) {

    var screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
    var calculatedSize = stageToCentimeters(stage, 100f)
    println(calculatedSize)
    var pixelSize =  with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

    Text(
        text = text,
        color = Color.Black,
        fontSize = pixelSize * 2,
        letterSpacing = pixelSize,
        fontFamily = FontFamily(Font(R.font.opticiansans)),
        modifier = modifier
    )
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

fun generateText(stage: Int): String {

    var random = Random()
    var text: String = ""

    for (i in 1..stage)  {
        text += ((abs(random.nextInt() % 25)) + 65).toChar()
    }

    return text
}