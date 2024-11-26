package me.proteus.myeye.visiontests

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.MenuActivity
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.ResultDataSaver
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.abs

class ExampleTest : VisionTest {

    override val testID: String = "TEST_INFO"
    override val testIcon: ImageVector = Icons.Outlined.Info

    private var correctAnswer: String = ""
    private var score: Int = 0

    override val stageCount: Int = 10

    override val resultCollector: ResultDataCollector = ResultDataCollector()


    @Composable
    override fun BeginTest(
        activity: VisionTestLayoutActivity,
        modifier: Modifier,
        isResult: Boolean,
        result: TestResult?
    ) {

        if (isResult) {

            var resultStages: MutableList<SerializablePair> = ArrayList<SerializablePair>()
            val resultData = ResultDataCollector.deserializeResult(result!!.result)

            for (i in 0..stageCount-1) {
                resultStages.add(resultData[i])
            }

            DisplayStage(activity, modifier, resultStages, true)

        } else {

            var testStages: MutableList<SerializablePair> = ArrayList<SerializablePair>()

            for (i in 0..stageCount-1) {

                correctAnswer = this.generateQuestion().toString()

                var variants = this.getExampleAnswers()
                var joinedVariants = variants.joinToString(separator = "")

                testStages.add(SerializablePair(correctAnswer, joinedVariants))
            }

            DisplayStage(activity, modifier, testStages, false)

        }

    }

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier, stages: List<SerializablePair>, isResult: Boolean) {

        var stageIterator: Int by remember { mutableIntStateOf(0) }

        println("$score $stageIterator")

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
                Text(text = stages[stageIterator].first, fontSize = 48.sp)
            }

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (el in stages[stageIterator].second) {

                    var ans: String = el.toString()

                    Button(onClick = {

                        println("$ans $correctAnswer")

                        if (ans == stages[stageIterator].first) score++

                        if (stageIterator < stageCount) {

                            storeResult(stages[stageIterator].first, ans)

                            stageIterator++

                        } else {

                            storeResult(stages[stageIterator].first, ans)
                            endTest(activity)

                        }

                    }) {
                        Text(ans)
                    }
                }
            }
        }

    }

    override fun generateQuestion(): Any {

        var question = randomChar()

        correctAnswer = question.toString()

        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun storeResult(question: String, answer: String) {

        resultCollector.addResult(question, answer)

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

    override fun endTest(activity: VisionTestLayoutActivity) {

        var localSaver = ResultDataSaver(activity.applicationContext)
        localSaver.insert("TEST_INFO", resultCollector.stages)

        val testLeavingIntent = Intent(activity, MenuActivity::class.java)
        activity.startActivity(testLeavingIntent)

    }

}