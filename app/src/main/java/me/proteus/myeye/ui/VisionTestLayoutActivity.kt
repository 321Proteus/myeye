package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.theme.MyEyeTheme
import me.proteus.myeye.visiontests.*
import java.lang.IllegalArgumentException

class VisionTestLayoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MyEyeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        VisionTestScreen(getTest(intent))
                    }

                }
            }
        }
    }
}

fun getTest(intent: Intent): VisionTest {
    val testID = intent.getStringExtra("TEST_ID")
    val test = when (testID) {
        "SNELLEN_CHART" -> SnellenChart()
        "TEST_CIRCLE" -> CircleTest()
        "TEST_BUILD" -> BuildTest()
        "TEST_INFO" -> InfoTest()
        else -> throw IllegalArgumentException("Nie znaleziono testu o podanym ID")
    }

    return test
}

@Composable
fun VisionTestScreen(test: VisionTest) {

    var question = test.generateQuestion()
    Text(text = question.toString())

}