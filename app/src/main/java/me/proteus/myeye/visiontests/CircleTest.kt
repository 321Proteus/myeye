package me.proteus.myeye.visiontests

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import me.proteus.myeye.R
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.*

class CircleTest : VisionTest {

    private var correctAnswer: String = ""

    override val stageCount: Int = 10

    private var currentStageState = mutableIntStateOf(1)

    override val currentStage: Int get() = currentStageState.intValue

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    fun stageToCentimeters(stage: Int, distance: Float): Float {

        var marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        var marCurrent = marBase * 10f.pow(-stage * 0.1f)

        var height = 2 * distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(directions: String, modifier: Modifier = Modifier) {

        val config = LocalConfiguration.current
        val opticianSansFamily = FontFamily(Font(R.font.opticiansans))

        var screenDensity = getScreenInfo(LocalContext.current).densityDpi / 2.54f
        var calculatedSize = stageToCentimeters(currentStage, 100f)
        println(calculatedSize)
        var pixelSize = with(LocalDensity.current) { (screenDensity * calculatedSize).toSp() }

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (char in directions) {
                    Text(
                        text = "C",
                        color = Color.Black,
                        fontSize = pixelSize * 20,
                        fontFamily = opticianSansFamily,
                        modifier = modifier.padding(8.dp).rotate(char.digitToInt().toFloat() * 90f)
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
                for (char in directions) {
                    Text(
                        text = "C",
                        color = Color.Black,
                        fontSize = pixelSize * 20,
                        fontFamily = opticianSansFamily,
                        modifier = modifier.padding(8.dp).rotate(char.digitToInt().toFloat() * 90f)
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
                Text(text = "Losuj ustawienie")
            }
            Button(onClick = { onSizeDecrease() }) {
                Text(text = "Dalej")
            }
        }

    }

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier) {

        var question: String by remember { mutableStateOf(this.generateQuestion().toString()) }
        var answers: Array<String> by remember { mutableStateOf(this.getExampleAnswers()) }

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
                    directions = question,
                    modifier = modifier
                )
            }

            ButtonRow(
                onRegenerate = {
                    question = this@CircleTest.generateQuestion().toString()
                    answers = this@CircleTest.getExampleAnswers()
                },

                onSizeDecrease = {

                    if (currentStage < stageCount) {

                        // TODO: Zaimplementowac polecenia glosowe do zbierania odpowiedzi
                        storeResult(question, generateDirections())

                        currentStageState.intValue++
                        question = this@CircleTest.generateQuestion().toString()
                        answers = this@CircleTest.getExampleAnswers()

                    } else {

                        storeResult(question, generateDirections())

                        var localSaver = ResultDataSaver(activity.applicationContext)
                        localSaver.insert("TEST_CIRCLE", resultCollector.stages)
                        localSaver.selectAll()

                    }
                }
            )
        }
    }

    override fun generateQuestion(): Any {

        var question: String = generateDirections()

        correctAnswer = question
        return question

    }

    fun generateDirections(): String {

        var directions = "0123"
        var text: String = ""
        var i: Int = 0

        var random = Random()

        while(i++ < 5)  {
            text += directions[abs(random.nextInt() % 4)]
        }

        return text

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = generateDirections()
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer

        return arr

    }

    override fun storeResult(question: String, answer: String) {

        resultCollector.addResult(question, answer)

    }

}