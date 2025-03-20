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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.resources.Res
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.prev
import me.proteus.myeye.ui.screens.res
import kotlin.math.abs
import kotlin.random.Random

class ExampleTest : VisionTest {

    override val testID: String = "TEST_MISC_EXAMPLE"
    override val testIcon: ImageVector = Icons.Outlined.Info
    override val needsMicrophone: Boolean = false
    override val stageCount: Int = 10
    override val resultCollector: ResultDataCollector = ResultDataCollector()
    override var conn: DBConnector? = null
    override var distance: Float = -1f

    private var correctAnswer: String = ""
    private var score: Int = 0

    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        println(isResult)

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

                    var update by remember { mutableStateOf(false) }

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
                        Text(text = Res.string.prev.res())
                    }
                    Button(onClick = { onUpdate("NEXT") }) {
                        Text(text = Res.string.next.res())
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

        val arr = Array(4) { "" }
        for (i in 0..3) {
            arr[i] = correctAnswer
            while (arr[i] == correctAnswer) arr[i] = randomChar().toString()
        }
        arr[abs(Random.nextInt()) % 4] = correctAnswer

        return arr

    }

    private fun randomChar(): Char {
        return ((abs(Random.nextInt() % 25)) + 65).toChar()
    }

}