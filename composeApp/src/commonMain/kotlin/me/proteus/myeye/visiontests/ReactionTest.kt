package me.proteus.myeye.visiontests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import me.proteus.myeye.resources.Res
import me.proteus.myeye.VisionTest
import me.proteus.myeye.io.DBConnector
import me.proteus.myeye.io.ResultDataCollector
import me.proteus.myeye.io.SerializableStage
import me.proteus.myeye.resources.next
import me.proteus.myeye.resources.prev
import me.proteus.myeye.resources.stage
import me.proteus.myeye.resources.test_reaction_elapsed
import me.proteus.myeye.resources.test_reaction_info
import me.proteus.myeye.resources.test_reaction_score
import me.proteus.myeye.ui.screens.res
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

class ReactionTest : VisionTest {

    override val testID = "TEST_MISC_REACTION"
    override var distance = -1f
    override val testIcon = Icons.TwoTone.Warning
    override val needsMicrophone = false
    override var conn: DBConnector? = null

    override val stageCount = 3
    override val resultCollector = ResultDataCollector()

    @Composable
    override fun DisplayStage(
        stage: SerializableStage,
        isResult: Boolean,
        onUpdate: (String) -> Unit
    ) {

        if (!isResult) {

            val time = stage.first.toFloat()

            var color by remember { mutableStateOf(Color.Red) }
            var elapsed by remember { mutableFloatStateOf(0f) }
            var clicked by remember { mutableStateOf(false) }

            println(stage.first + "/" + stage.second)

            LaunchedEffect(stage) {
                color = Color.Red
                elapsed = 0f
                clicked = false
                delay(time.times(1000).toLong())
                color = Color.Green

                while (!clicked) {
                    delay(10)
                    elapsed += 0.01f
                }

                delay(1000)
                onUpdate(elapsed.toString())

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .clickable { clicked = true },
                contentAlignment = Alignment.Center
            ) {
                Text(if (elapsed == 0f) stringResource(Res.string.test_reaction_info) else elapsed.toString())
            }
        } else {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(Res.string.stage.res() + " ${stage.difficulty + 1}", fontSize = 24.sp)
                        // TODO: Zaokraglanie
                        Text(Res.string.test_reaction_elapsed.res() + ": ${stage.first.toFloat()}s")
                        Text(Res.string.test_reaction_score.res() + ": ${stage.second.toFloat()}s")

                    }
                }

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
        var random = Random.nextFloat()
        while (random <= 0.6) random = Random.nextFloat()
        return random.times(10).toString()
    }

    override fun getExampleAnswers(): Array<String> {
        return arrayOf()
    }

    override fun checkAnswer(answer: String): Boolean {
        return true
    }

}