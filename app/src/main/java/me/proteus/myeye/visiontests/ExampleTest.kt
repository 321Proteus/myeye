package me.proteus.myeye.visiontests

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.SerializablePair
import me.proteus.myeye.TestResult
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
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
    override fun DisplayStage(activity: VisionTestLayoutActivity, stages: List<SerializablePair>, isResult: Boolean) {

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
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stages[stageIterator].first,
                        color = Color.Black,
                        fontSize = 48.sp
                    )

                    if (isResult) {
                        Text(
                            text = stages[stageIterator].second,
                            color = (if (stages[stageIterator].first == stages[stageIterator].second) Color.Green else Color.Red),
                            fontSize = 48.sp
                        )
                    }
                }



            }

            if (!isResult) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var buttons = stages[stageIterator].second.filter { it != ' ' }
                    for (el in buttons) {

                        var ans: String = el.toString()

                        Button(onClick = {

                            if (ans == stages[stageIterator].first) score++

                            if (stageIterator < stageCount - 1) {
                                storeResult(stages[stageIterator].first, ans)
                                stageIterator++
                            } else {
                                storeResult(stages[stageIterator].first, ans)
                                if (!isResult) endTest(activity)
                            }

                        }) {
                            Text(if (isResult) "Dalej" else ans)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { stageIterator-- }) {
                        Text(text = "Poprzedni etap")
                    }
                    Button(onClick = { stageIterator++ }) {
                        Text(text = "Następny etap")
                    }
                }
            }

        }

    }

    override fun generateQuestion(): String {

        var question = randomChar().toString()

        correctAnswer = question

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

}