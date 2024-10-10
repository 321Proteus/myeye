package me.proteus.myeye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                    generateText(8, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun generateText(length: Int, modifier: Modifier = Modifier) {

    var random = Random()
    var text: String = ""

    for (i in 1..length) {
        text += ((abs(random.nextInt() % 25)) + 65).toChar()
    }
    println(text)

    Text(
        text = text,
        color = Color.Black,
        fontSize = 48.sp,
        letterSpacing = 16.sp,
        fontFamily = FontFamily(Font(R.font.opticiansans)),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyEyeTheme {
        generateText(8)
    }
}