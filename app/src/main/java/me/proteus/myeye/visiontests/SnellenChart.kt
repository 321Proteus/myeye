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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.proteus.myeye.VisionTest
import me.proteus.myeye.ui.VisionTestLayoutActivity
import java.util.Random
import kotlin.math.abs

class SnellenChart : VisionTest {

    private var correctAnswer: String = ""
    private var score: Int = 0

    override val stageCount: Int = 10

    override var currentStage: Int = 0


    @Composable
    override fun DisplayStage(activity: VisionTestLayoutActivity, modifier: Modifier) {

        println(this)

        var question: String = this.generateQuestion().toString()
        var answers: Array<String> = this.getExampleAnswers()
        println("---")

        for (el in answers) println(el)


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
                        if (this@SnellenChart.checkAnswer(ans)) score++

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

    override fun getExampleAnswers(): Array<String> {

        var random = Random()

        var arr = Array<String>(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomChar().toString()
            println(arr[i])
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer.toString()

        return arr

    }

    fun randomChar(): Char {

        var random = Random()
        return ((abs(random.nextInt() % 25)) + 65).toChar()
    }

}