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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.proteus.myeye.SerializableStage
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.ResultDataCollector
import java.util.Random
import kotlin.math.abs

class ExampleTest : VisionTest {

    override val testID: String = "TEST_SIGHT_INFO"
    override val testIcon: ImageVector = Icons.Outlined.Info
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 10
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var distance: Float = -1f

    private var correctAnswer: String = ""
    private var score: Int = 0

    @Composable
    override fun DisplayStage(
        controller: NavController,
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        println("$score")

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
                        text = stage.first,
                        color = Color.Black,
                        fontSize = 48.sp
                    )

                    if (isResult) {
                        Text(
                            text = stage.second,
                            color = (if (stage.first == stage.second) Color.Green else Color.Red),
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
                    val buttons = stage.second.filter { it != ' ' }
                    for (el in buttons) {

                        val ans: String = el.toString()

                        Button(onClick = { onUpdate(ans) }) {
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
                    Button(onClick = { onUpdate("PREV") }) {
                        Text(text = "Poprzedni etap")
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = "Następny etap")
                    }
                }
            }

        }

    }

    override fun generateQuestion(stage: Int?): String {

        val question = randomChar().toString()

        correctAnswer = question

        return question

    }

    override fun checkAnswer(answer: String): Boolean {
        return answer == correctAnswer
    }

    override fun getExampleAnswers(): Array<String> {

        val random = Random()

        val arr = Array(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomChar().toString()
        }
        arr[abs(random.nextInt()) % 4] = correctAnswer

        return arr

    }

    private fun randomChar(): Char {

        val random = Random()
        return ((abs(random.nextInt() % 25)) + 65).toChar()
    }

}