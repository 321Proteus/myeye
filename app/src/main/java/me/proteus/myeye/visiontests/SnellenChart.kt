package me.proteus.myeye.visiontests

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.R
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import me.proteus.myeye.ui.theme.MyEyeTheme
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.tan

class SnellenChart : VisionTest {

    private var correctAnswer: String = ""
    private var score: Int = 0

    override val stageCount: Int = 10

    private var currentStageState = mutableIntStateOf(1)

    override val currentStage: Int get() = currentStageState.intValue

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
                        fontSize = pixelSize * 20,
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
                        fontSize = pixelSize * 20,
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
        onSizeDecrease: () -> Unit,
        modifier: Modifier = Modifier) {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onRegenerate() }) {
                Text(text = "Losuj litery")
            }
            Button(onClick = { onSizeDecrease() }) {
                Text(text = "Dalej")
            }
        }

    }

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier) {

        println("$score $currentStage")

        var question: String by remember { mutableStateOf(this.generateQuestion().toString()) }

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
                    stage = currentStage,
                    text = question,
                    modifier = modifier
                )
            }

            ButtonRow(
                onRegenerate = { question = this@SnellenChart.generateQuestion().toString() },
                onSizeDecrease = {
                    if (currentStage < stageCount) {
                        currentStageState.intValue++
                        question = this@SnellenChart.generateQuestion().toString()
                    }
                }
            )
        }
    }

    override fun generateQuestion(): Any {

        var question: String = "";
        var i: Int = 0

        while(i++ < 5)  {
            question += randomChar()
        }

        correctAnswer = question.toString()
        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomChar().toString()
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer.toString()

        return arr

    }

    fun randomChar(): Char {

        var random = Random()
        return ((abs(random.nextInt() % 25)) + 65).toChar()
    }

}