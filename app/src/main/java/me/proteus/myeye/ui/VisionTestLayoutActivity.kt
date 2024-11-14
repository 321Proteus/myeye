package me.proteus.myeye.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.proteus.myeye.ButtonRow
import me.proteus.myeye.LetterContainer
import me.proteus.myeye.VisionTest
import me.proteus.myeye.generateText
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
                        VisionTestScreen(test = getTest(intent), modifier = Modifier)
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
fun VisionTestScreen(test: VisionTest, modifier: Modifier) {

    var question by remember { mutableStateOf(test.generateQuestion()) }
    var answers by remember { mutableStateOf(test.getExampleAnswers()) }
    var score by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box (contentAlignment = Alignment.Center) {
            Text(text = score.toString())
        }
        Box (
            modifier = modifier
                .weight(1f)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = question.toString())
        }

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (ans in answers) {
                Button(onClick = {
                    if (test.checkAnswer(ans)) score++;
                    else println(ans)
                    question = test.generateQuestion()
                    answers = test.getExampleAnswers()
                }) {
                    Text(ans)
                }
            }
        }
    }

}