package me.proteus.myeye.visiontests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.abs

class ExampleTest : VisionTest {

    private var correctAnswer: String = ""
    private var score: Int = 0

    override val stageCount: Int = 10

    private var currentStageState = mutableIntStateOf(1)

    override val currentStage: Int get() = currentStageState.intValue

    override val resultCollector: ResultDataCollector = ResultDataCollector()

    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier) {

        println("$score $currentStage")

        var question by remember { mutableStateOf(this.generateQuestion().toString()) }
        var answers by remember { mutableStateOf(this.getExampleAnswers()) }

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
                Text(text = question.toString(), fontSize = 48.sp)
            }

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (ans in answers) {
                    Button(onClick = {
                        if (this@ExampleTest.checkAnswer(ans)) score++

                        if (currentStageState.intValue < stageCount) {

                            currentStageState.intValue++
                            question = this@ExampleTest.generateQuestion().toString()
                            answers = this@ExampleTest.getExampleAnswers()

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

        resultCollector.addResult(question, answer);

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