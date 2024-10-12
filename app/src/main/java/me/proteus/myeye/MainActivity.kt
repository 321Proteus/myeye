package me.proteus.myeye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.util.Random
import kotlin.math.*

class MainActivity : ComponentActivity() {
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

    var stage: Int by remember { mutableStateOf(1) }
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
            Text(
                text = text,
                color = Color.Black,
                fontSize = (27*(9-stage)).sp,
                letterSpacing = 16.sp,
                fontFamily = FontFamily(Font(R.font.opticiansans)),
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

    for (i in 1..stage) {
        text += ((abs(random.nextInt() % 25)) + 65).toChar()
    }
    println(text)

    return text
}