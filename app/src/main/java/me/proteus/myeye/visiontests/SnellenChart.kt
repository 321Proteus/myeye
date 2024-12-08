package me.proteus.myeye.visiontests

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import me.proteus.myeye.R
import me.proteus.myeye.ScreenScalingUtils.getScreenInfo
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.*

class SnellenChart : VisionTest {

    override val testID: String = "SNELLEN_CHART"
    override val testIcon: ImageVector = Icons.TwoTone.Face

    private var correctAnswer: String = ""

    override val stageCount: Int = 10

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    fun stageToCentimeters(stage: Int, distance: Float): Float {

        var marBase = ((PI/180) / 60) * 5  // 5 minut katowych
        var marCurrent = marBase * 10f.pow(-stage * 0.1f)

        var height = 2 * distance * tan(marCurrent / 2)

        return height.toFloat()

    }

    @Composable
    fun LetterContainer(stage: Int, text: String, key: String?, modifier: Modifier = Modifier) {

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
                for (i in 0..<text.length) {
                    Text(
                        text = text[i].toString(),
                        color = (if (key != null) (if (text[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
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
                for (i in 0..<text.length) {
                    Text(
                        text = text[i].toString(),
                        color = (if (key != null) (if (text[i] == key[i]) Color.Green else Color.Red) else Color.Black),
                        fontSize = pixelSize * 15,
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
    override fun BeginTest(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        isResult: Boolean,
        result: TestResult?
    ) {

        if (isResult) {

            var resultStages: MutableList<SerializablePair> = ArrayList<SerializablePair>(stageCount)

            val resultData = ResultDataCollector.deserializeResult(result!!.result)


            for (i in 0..<stageCount) {
                resultStages.add(resultData[i])
            }

            DisplayStage(activity, modifier, resultStages, true)

        } else {

            var testStages: MutableList<SerializablePair> = ArrayList<SerializablePair>(stageCount)

            for (i in 0..<stageCount) {
                testStages.add(SerializablePair(this.generateQuestion().toString(), randomText(5)))
            }

            DisplayStage(activity, modifier, testStages, false)

        }

    }

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier, stages: List<SerializablePair>, isResult: Boolean) {

        var questionIterator: Int by remember { mutableIntStateOf(0) }

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
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LetterContainer(
                        stage = questionIterator,
                        text = stages[questionIterator].first,
                        key = null,
                        modifier = modifier
                    )
                    if (isResult) {
                        LetterContainer(
                            stage = questionIterator,
                            text = stages[questionIterator].second,
                            key = stages[questionIterator].first,
                            modifier = modifier
                        )
                    }

                }

            }

            if (!isResult) {
                ButtonRow(
                    onRegenerate = { questionIterator++ },
                    onSizeDecrease = {

                        if (questionIterator < stageCount - 1) {

                            // TODO: Zaimplementowac polecenia glosowe do zbierania odpowiedzi
                            storeResult(stages[questionIterator].first, randomText(5))
                            questionIterator++
                        } else {
                            storeResult(stages[questionIterator].first, randomText(5))
                            if (!isResult) endTest(activity)

                        }
                    }
                )
            } else {

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { questionIterator-- }) {
                        Text(text = "Poprzedni etap")
                    }
                    Button(onClick = { questionIterator++ }) {
                        Text(text = "Następny etap")
                    }
                }
            }

        }
    }

    override fun storeResult(question: String, answer: String) {

        resultCollector.addResult(question, answer)

    }

    override fun generateQuestion(): Any {

        var question: String = randomText(5)

        correctAnswer = question
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
            while (arr[i] == correctAnswer) arr[i] = randomText(5)
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer.toString()

        return arr

    }

    fun randomChar(): Char {

        var random = Random()
        return ((abs(random.nextInt() % 25)) + 65).toChar()
    }

    fun randomText(n: Int): String {

        var text: String = ""
        var i: Int = 0

        while(i++ < n)  {
            text += randomChar()
        }

        return text
    }

}